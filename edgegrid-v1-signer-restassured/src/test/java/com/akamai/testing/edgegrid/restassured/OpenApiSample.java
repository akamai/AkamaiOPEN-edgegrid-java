/*
 * Copyright 2016 Copyright 2016 Akamai Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.akamai.testing.edgegrid.restassured;


import com.akamai.testing.edgegrid.core.ClientCredential;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

// TODO(mgawinec) Not sure if this information here can leak to external teams, including both credential and endpoint
public class OpenApiSample {

    String baseUri = "https://akaa-cf6fooumkselbx6j-spubmyp7ygje4vyx.luna-dev.akamaiapis.net";

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz2rp")
            .clientSecret("12rvdn/myhSSiuYAC6ZPGaI91ezhdbYd7WyagzhGxms=")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .build();

    @Test(enabled = false)
    public void test() {

        //@formatter:off
        given().
                baseUri(baseUri).
                filter(EdgeGridV1SignerFilter.sign(credential)).
        when().
                get("/authz/v2/identities/self").
        then().
                statusCode(200);
        //@formatter:off
    }
}
