// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.nereids.jobs.cascades;

import org.apache.doris.nereids.PlannerContext;
import org.apache.doris.nereids.jobs.Job;
import org.apache.doris.nereids.jobs.JobType;
import org.apache.doris.nereids.memo.Group;
import org.apache.doris.nereids.memo.PlanReference;
import org.apache.doris.nereids.pattern.Pattern;
import org.apache.doris.nereids.rules.Rule;
import org.apache.doris.nereids.trees.plans.Plan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Job to optimize {@link org.apache.doris.nereids.trees.plans.Plan} in {@link org.apache.doris.nereids.memo.Memo}.
 */
public class OptimizePlanJob extends Job {
    private final PlanReference planReference;

    public OptimizePlanJob(PlanReference planReference, PlannerContext context) {
        super(JobType.OPTIMIZE_PLAN, context);
        this.planReference = planReference;
    }

    @Override
    public void execute() {
        List<Rule<Plan>> validRules = new ArrayList<>();
        List<Rule<Plan>> explorationRules = getRuleSet().getExplorationRules();
        List<Rule<Plan>> implementationRules = getRuleSet().getImplementationRules();
        prunedInvalidRules(planReference, explorationRules);
        prunedInvalidRules(planReference, implementationRules);
        validRules.addAll(explorationRules);
        validRules.addAll(implementationRules);
        validRules.sort(Comparator.comparingInt(o -> o.getRulePromise().promise()));

        for (Rule rule : validRules) {
            pushTask(new ApplyRuleJob(planReference, rule, context));

            // If child_pattern has any more children (i.e non-leaf), then we will explore the
            // child before applying the rule. (assumes task pool is effectively a stack)
            for (int i = 0; i < rule.getPattern().children().size(); ++i) {
                Pattern childPattern = rule.getPattern().child(i);
                if (childPattern.arity() > 0) {
                    Group childSet = planReference.getChildren().get(i);
                    pushTask(new ExploreGroupJob(childSet, context));
                }
            }
        }

    }
}
