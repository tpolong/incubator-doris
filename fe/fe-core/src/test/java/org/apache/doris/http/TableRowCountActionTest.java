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

package org.apache.doris.http;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;


public class TableRowCountActionTest extends DorisHttpTestCase {
    private static final String PATH_URI = "/_count";

    @Test
    public void testTableCount() throws IOException {
        Request request = new Request.Builder()
                .get()
                .addHeader("Authorization", rootAuth)
                .url(URI + PATH_URI)
                .build();

        Response response = networkClient.newCall(request).execute();
        String res = response.body().string();
        System.out.println(res);
        JSONObject jsonObject = (JSONObject) JSONValue.parse(res);
        Assert.assertEquals(200, (long) ((JSONObject) jsonObject.get("data")).get("status"));
        Assert.assertEquals(2000, (long) ((JSONObject) jsonObject.get("data")).get("size"));
    }
}
