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


import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.GregorianCalendar;
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

    static final long DEFAULT_TIMESTAMP = new GregorianCalendar(2016, 7, 4, 9, 0, 0).getTimeInMillis();

    @Test(dataProvider = "requestsForDefaultSettings")
    public void testForDefaultSettings(String caseName, String expectedAuthorizationHeader, Request request) throws RequestSigningException {
        String actualAuthorizationHeader = DEFAULT_SIGNER.getAuthorizationHeaderValue(request, DEFAULT_CREDENTIAL, DEFAULT_TIMESTAMP, DEFAULT_NONCE);
        assertThat(actualAuthorizationHeader, is(equalTo(expectedAuthorizationHeader)));
    }

    @Test(dataProvider = "requestsForHeadersSigning")
    public void testForHeadersSigning(String caseName, String expectedAuthorizationHeader, Set<String> headersToSign, Request request) throws RequestSigningException {
        EdgeGridV1Signer signer = new EdgeGridV1Signer(Algorithm.EG1_HMAC_SHA256, headersToSign, EdgeGridV1Signer.DEFAULT_MAX_BODY_SIZE_IN_BYTES);
        String actualAuthorizationHeader = signer.getAuthorizationHeaderValue(request, DEFAULT_CREDENTIAL, DEFAULT_TIMESTAMP, DEFAULT_NONCE);
        assertThat(actualAuthorizationHeader, is(equalTo(expectedAuthorizationHeader)));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void shouldRejectRequestWithDuplicateHeaderNames() throws RequestSigningException {
        Request request = Request.builder()
                .method("GET")
                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                .headers(ImmutableMultimap.<String, String>builder().put("Duplicate", "X").put("Duplicate", "Y").build())
                .build();

        DEFAULT_SIGNER.getAuthorizationHeaderValue(request, DEFAULT_CREDENTIAL, DEFAULT_TIMESTAMP, DEFAULT_NONCE);
    }

    @DataProvider
    public Object[][] requestsForDefaultSettings() {
        return new Object[][]{
                {"GET request",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=0dCwIUaObZaXrTO1CwojlVBwuNbh1av+nO7VS2YC8is=",
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                                .build()},
                {"GET request with query",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=OkiBaPX/HORjhPPu2Vyo35aQrO3+GhDM1x4NHXUoOio=",
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("http://control.akamai.com/check?maciek=value"))
                                .build()},
                {"POST request",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=AY5RxJqWU9EO3iMM1x/Fd6AdsJF8kzz7NYVmyc8QixA=",
                        Request.builder()
                                .method("POST")
                                .uriWithQuery(URI.create("http://control.akamai.com/send"))
                                .body("x=y&a=b".getBytes())
                                .build()},
                {"For PUT request we ignore body",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=DvV3p2X66F3qHopVX3tk3pHm8vIqLR9aJCKFCgIQS5Q=",
                        Request.builder()
                                .method("PUT")
                                .uriWithQuery(URI.create("http://control.akamai.com/send"))
                                .body("x=y&a=b".getBytes())
                                .build()}
        };
    }

    @DataProvider
    public Object[][] requestsForHeadersSigning() {
        return new Object[][]{
                {"Headers should be included in signature",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=S32xN/Essd1Y9mMexnPefngle9tNfcVad0yyYxVBKzA=",
                        ImmutableSet.of("Content-Type"),
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                                .headers(ImmutableMultimap.<String, String>builder()
                                        .put("Content-Type", "application/json")
                                        .build())
                                .build()},
                {"Not listed headers should not impact signature",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=S32xN/Essd1Y9mMexnPefngle9tNfcVad0yyYxVBKzA=",
                        ImmutableSet.of("Content-Type"),
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                                .headers(ImmutableMultimap.<String, String>builder()
                                        .put("Content-Type", "application/json")
                                        .put("Cache-Control", "no-cache")
                                        .build())
                                .build()}

        };

    }

}
