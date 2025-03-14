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

package org.apache.doris.nereids.trees.plans;

import org.apache.doris.nereids.exceptions.UnboundException;
import org.apache.doris.nereids.memo.PlanReference;
import org.apache.doris.nereids.trees.TreeNode;
import org.apache.doris.nereids.trees.expressions.Slot;

import java.util.List;

/**
 * Abstract class for all plan node.
 */
public interface Plan<PLAN_TYPE extends Plan<PLAN_TYPE>> extends TreeNode<PLAN_TYPE> {

    List<Slot> getOutput() throws UnboundException;

    PlanReference getPlanReference();

    void setPlanReference(PlanReference planReference);

    String treeString();

    @Override
    List<Plan> children();

    @Override
    Plan child(int index);
}
