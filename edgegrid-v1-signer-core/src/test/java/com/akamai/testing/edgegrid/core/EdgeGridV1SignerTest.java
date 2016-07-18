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

package com.akamai.testing.edgegrid.core;


import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests EdgeGridV1Signer.
 *
 * @author mgawinec@akamai.com
 */
public class EdgeGridV1SignerTest {

    static final EdgeGridV1Signer DEFAULT_SIGNER = new EdgeGridV1Signer();
    static final UUID DEFAULT_NONCE = UUID.fromString("ec9d20ee-1e9b-4c1f-925a-f0017754f86c");
    static final ClientCredential DEFAULT_CREDENTIAL = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234")
            .clientSecret("12rvdn/myhSSiuYAC6ZPGaI91ezhdbYd7WyTRKhGxms=")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj")
            .build();

    static final long DEFAULT_TIMESTAMP = new Date(2016, 7, 4, 9, 0, 0).getTime();

    @Test(dataProvider = "requestsForDefaultSettings")
    public void testForDefaultSettings(String caseName, String expectedAuthorizationHeader, EdgeGridV1Signer.Request request) throws RequestSigningException {
        String actualAuthorizationHeader = DEFAULT_SIGNER.getAuthorizationHeaderValue(request, DEFAULT_CREDENTIAL, DEFAULT_TIMESTAMP, DEFAULT_NONCE);
        assertThat(actualAuthorizationHeader, is(equalTo(expectedAuthorizationHeader)));
    }

    @Test(dataProvider = "requestsForHeadersSigning")
    public void testForHeadersSigning(String caseName, String expectedAuthorizationHeader, Set<String> headersToSign, EdgeGridV1Signer.Request request) throws RequestSigningException {
        EdgeGridV1Signer signer = new EdgeGridV1Signer(EdgeGridV1Signer.Algorithm.EG1_HMAC_SHA256, headersToSign, EdgeGridV1Signer.DEFAULT_MAX_BODY_SIZE_IN_BYTES);
        String actualAuthorizationHeader = signer.getAuthorizationHeaderValue(request, DEFAULT_CREDENTIAL, DEFAULT_TIMESTAMP, DEFAULT_NONCE);
        assertThat(actualAuthorizationHeader, is(equalTo(expectedAuthorizationHeader)));
    }

    @DataProvider
    public Object[][] requestsForDefaultSettings() {
        return new Object[][]{
                {"GET request",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=39160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=VwF7FDzZeEj8FRPStK4CqbGslhYKqGOpgxh19KQTZe4=",
                        EdgeGridV1Signer.Request.builder()
                                .method("GET")
                                .uriWithQuery("http://control.akamai.com/check")
                                .build()},
                {"GET request with query",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=39160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=6yKXfMlCpIdeG9dxhnQagBSIXAaDDVbFTDV9W8wMoec=",
                        EdgeGridV1Signer.Request.builder()
                                .method("GET")
                                .uriWithQuery("http://control.akamai.com/check?maciek=value")
                                .build()},
                {"POST request",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=39160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=scRJfNqVY3gHVMst75nussh2Pw7Pglkstsp1AvsRYGo=",
                        EdgeGridV1Signer.Request.builder()
                                .method("POST")
                                .uriWithQuery("http://control.akamai.com/send")
                                .body("x=y&a=b")
                                .build()}
        };
    }

    @DataProvider
    public Object[][] requestsForHeadersSigning() {
        return new Object[][]{
                {"Headers should be included in signature",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=39160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=32aKKzMLSWQQtAhi99QIKvzKHi0kKGdZKPLM7sRrVfY=",
                        ImmutableSet.of("Content-Type"),
                        EdgeGridV1Signer.Request.builder()
                                .method("GET")
                                .uriWithQuery("http://control.akamai.com/check")
                                .headers(ImmutableMultimap.<String, String>builder()
                                        .put("Content-Type", "application/json")
                                        .build())
                                .build()},
                {"Not listed headers should not impact signature",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=39160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=32aKKzMLSWQQtAhi99QIKvzKHi0kKGdZKPLM7sRrVfY=",
                        ImmutableSet.of("Content-Type"),
                        EdgeGridV1Signer.Request.builder()
                                .method("GET")
                                .uriWithQuery("http://control.akamai.com/check")
                                .headers(ImmutableMultimap.<String, String>builder()
                                        .put("Content-Type", "application/json")
                                        .put("Cache-Control", "no-cache")
                                        .build())
                                .build()}

        };

    }

}
