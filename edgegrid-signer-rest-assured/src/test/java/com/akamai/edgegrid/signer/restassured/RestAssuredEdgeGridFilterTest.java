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


import com.akamai.edgegrid.signer.ClientCredential;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


/**
 * Unit tests for {@link RestAssuredEdgeGridFilterTest}.
 *
 * @author mgawinec@akamai.com
 */
public class RestAssuredEdgeGridFilterTest {

    static final String SERVICE_MOCK_HOST = "localhost";
    static final int SERVICE_MOCK_PORT = 9089;
    static final String SERVICE_MOCK = SERVICE_MOCK_HOST + ":" + SERVICE_MOCK_PORT;

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host(SERVICE_MOCK)
            .build();

    WireMockServer wireMockServer = new WireMockServer(wireMockConfig().httpsPort(SERVICE_MOCK_PORT));



    @BeforeClass
    public void setUp() {
        wireMockServer.start();
    }

    @BeforeMethod
    public void reset() {
        wireMockServer.resetMappings();
        wireMockServer.resetRequests();
    }

    @Test
    public void signEachRequest() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test
    public void signEachRequestWithPathParams() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathMatching("/config-gtm/v1/domains/.*"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("/config-gtm/v1/domains/{domain}", "storage1.akadns.net")
                .then().statusCode(200);
    }

    @Test
    public void signWithHostHeader() throws URISyntaxException, IOException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));

        RestAssured.given()
                .relaxedHTTPSValidation()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .header("Host", "ignored-hostname.com")
                .get("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignEachRequestWithAbsolutePath() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .get("https://ignored-hostname.com/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignRequestWithFileContent() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .body(new File("/home/johan/some_large_file.bin"))
                .post("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignRequestWithInputStreamContent() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .body(new ByteArrayInputStream("exampleString".getBytes(StandardCharsets.UTF_8)))
                .post("/billing-usage/v1/reportSources")
        .then().statusCode(200);
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void dontSignRequestWithMultipartContent() throws URISyntaxException, IOException {

        RestAssured.given()
                .filter(new RestAssuredEdgeGridFilter(credential))
                .multiPart("file", new File("/home/johan/some_large_file.bin"))
                .post("/billing-usage/v1/reportSources")
                .then().statusCode(200);
    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }

}
