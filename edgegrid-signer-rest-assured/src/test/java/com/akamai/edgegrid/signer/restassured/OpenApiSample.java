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

package com.akamai.edgegrid.signer.restassured;


import org.testng.annotations.Test;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.restassured.RestAssuredEdgeGridFilter;

import static io.restassured.RestAssured.given;

/**
 * Example of use of EdgeGrid signer with REST-assured.
 *
 * @author mgawinec@akamai.com
 */
public class OpenApiSample {

    String baseUri = "https://endpoint.net";

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientSecret("SOMESECRET")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .host("endpoint.net")
            .build();

    @Test(enabled = false)
    public void test() {

        //@formatter:off
        given().
                baseUri(baseUri).
                filter(RestAssuredEdgeGridFilter.sign(credential)).
        when().
                get("/service/v2/users").
        then().
                statusCode(200);
        //@formatter:off
    }
}
