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

package com.akamai.edgegrid.signer.apachehttpclient;


import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.matching.RequestPattern;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;


/**
 * Integration tests for {@link com.akamai.edgegrid.signer.apachehttpclient.ApacheHttpClientEdgeGridInterceptor}.
 *
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public class ApacheHttpClientEdgeGridInterceptorIntegrationTest {

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
    public void testInterceptor() throws URISyntaxException, IOException, RequestSigningException {

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "/billing-usage/v1/reportSources/alternative")));

        wireMockServer.stubFor(get(urlPathEqualTo("/billing-usage/v1/reportSources/alternative"))
                .withHeader("Authorization", matching(".*"))
                .withHeader("Host", equalTo(SERVICE_MOCK))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "text/xml")
                        .withBody("<response>Some content</response>")));


        HttpGet request = new HttpGet("http://endpoint.net/billing-usage/v1/reportSources");

        HttpClient client = HttpClientSetup.getHttpClientWithRelaxedSsl()
                .addInterceptorFirst(new ApacheHttpClientEdgeGridInterceptor(credential))
                .setRoutePlanner(new ApacheHttpClientEdgeGridRoutePlanner(credential))
                .build();

        client.execute(request);

        List<LoggedRequest> loggedRequests = wireMockServer.findRequestsMatching(RequestPattern
                .everything()).getRequests();

        MatcherAssert.assertThat(loggedRequests.get(0).getHeader("Authorization"),
                Matchers.not(CoreMatchers.equalTo(loggedRequests.get(1).getHeader("Authorization"))));
    }

    @AfterClass
    public void tearDownAll() {
        wireMockServer.stop();
    }


}
