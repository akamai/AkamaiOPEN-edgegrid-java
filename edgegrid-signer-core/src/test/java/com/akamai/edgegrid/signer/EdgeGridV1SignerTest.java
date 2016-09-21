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

package com.akamai.edgegrid.signer;


import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.EdgeGridV1Signer;
import com.akamai.edgegrid.signer.Request;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/**
 * Unit tests EdgeGridV1Signer.
 *
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public class EdgeGridV1SignerTest {

    static final UUID DEFAULT_NONCE = UUID.fromString("ec9d20ee-1e9b-4c1f-925a-f0017754f86c");

    static final ClientCredential DEFAULT_CREDENTIAL = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234")
            .clientSecret("12rvdn/myhSSiuYAC6ZPGaI91ezhdbYd7WyTRKhGxms=")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj")
            .host("control.akamai.com")
            .build();

    /** Static timestamp corresponds to 2016-08-04T07:00:00+0000. */
    static final long DEFAULT_TIMESTAMP = 1470294000000L;

    @Test(dataProvider = "requestsForDefaultSettings")
    public void testForDefaultSettings(String caseName, String expectedAuthorizationHeader, Request request) throws RequestSigningException {
        String actualAuthorizationHeader = new EdgeGridV1Signer().getSignature(request, DEFAULT_CREDENTIAL, DEFAULT_TIMESTAMP, DEFAULT_NONCE);

        assertThat(actualAuthorizationHeader, is(equalTo(expectedAuthorizationHeader)));
    }

    @Test(dataProvider = "requestsForHeadersSigning")
    public void testForHeadersSigning(String caseName, String expectedAuthorizationHeader, Set<String> headersToSign, Request request) throws RequestSigningException {
        ClientCredential credential = ClientCredential.builder()
                .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234")
                .clientSecret("12rvdn/myhSSiuYAC6ZPGaI91ezhdbYd7WyTRKhGxms=")
                .clientToken("akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj")
                .headersToSign(headersToSign)
                .host("control.akamai.com")
                .build();
        String actualAuthorizationHeader = new EdgeGridV1Signer().getSignature(request, credential, DEFAULT_TIMESTAMP, DEFAULT_NONCE);

        assertThat(actualAuthorizationHeader, is(equalTo(expectedAuthorizationHeader)));
    }

    @DataProvider
    public Object[][] requestsForDefaultSettings() {
        return new Object[][]{
                {"GET request",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=0dCwIUaObZaXrTO1CwojlVBwuNbh1av+nO7VS2YC8is=",
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("http://any-hostname-at-all.com/check"))
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
                                .uriWithQuery(URI.create("http://any-hostname-at-all.com/send"))
                                .body("x=y&a=b".getBytes())
                                .build()},
                {"For PUT request we ignore body",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=DvV3p2X66F3qHopVX3tk3pHm8vIqLR9aJCKFCgIQS5Q=",
                        Request.builder()
                                .method("PUT")
                                .uriWithQuery(URI.create("http://control.akamai.com/send"))
                                .body("x=y&a=b".getBytes())
                                .build()},
                {"GET without scheme or hostname",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=8GpKbZnIx4XEw/zXtQdbVwIu0zJSG0RpNiVTSyIUwr0=",
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("/check"))
                                .build()},
        };
    }

    @DataProvider
    public Object[][] requestsForHeadersSigning() throws RequestSigningException {
        Set<String> headerToSign = new HashSet<>();
        headerToSign.add("Content-Type");
        return new Object[][]{
                {"Headers should be included in signature",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=S32xN/Essd1Y9mMexnPefngle9tNfcVad0yyYxVBKzA=",
                        headerToSign,
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("http://control.akamai.com/check"))
                                .header("Content-Type", "application/json")
                                .build()},
                {"Not listed headers should not impact signature",
                        "EG1-HMAC-SHA256 client_token=akaa-k7glklzuxkkh2ycw-oadjrtwpvpn6yjoj;access_token=akaa-dm5g2bfwoodqnc6k-ju7vlao2gz6oz234;timestamp=20160804T07:00:00+0000;nonce=ec9d20ee-1e9b-4c1f-925a-f0017754f86c;signature=S32xN/Essd1Y9mMexnPefngle9tNfcVad0yyYxVBKzA=",
                        headerToSign,
                        Request.builder()
                                .method("GET")
                                .uriWithQuery(URI.create("http://this-is-ignored.com/check"))
                                .header("Content-Type", "application/json")
                                .header("Cache-Control", "no-cache")
                                .build()}
        };

    }

}
