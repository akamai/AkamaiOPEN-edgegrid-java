/*
 * Copyright 2018 Akamai Technologies, Inc. All Rights Reserved.
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

package com.akamai.edgegrid.signer.googlehttpclient;


import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.akamai.edgegrid.signer.googlehttpclient.GoogleHttpClientEdgeGridRequestSigner;
import com.google.api.client.http.*;
import com.google.api.client.http.apache.ApacheHttpTransport;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.isEmptyOrNullString;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Example of use of EdgeGrid signer with Google HTTP Client Library.
 *
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public class GoogleHttpClientEdgeGridRequestSignerTest {

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

    @Test
    public void signEachRequest() throws URISyntaxException, IOException, RequestSigningException {
        HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        URI uri = URI.create("https://ignored-hostname.com/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));

        GoogleHttpClientEdgeGridRequestSigner googleHttpSigner = new GoogleHttpClientEdgeGridRequestSigner(credential);
        googleHttpSigner.sign(request, request);

        assertThat(request.getHeaders().containsKey("host"), is(false));
        assertThat(request.getUrl().getHost(), equalTo("endpoint.net"));
        assertThat(request.getHeaders().containsKey("authorization"), is(true));
        assertThat(request.getHeaders().getAuthorization(), not(isEmptyOrNullString()));
    }

    @Test
    public void signWithHostHeader() throws URISyntaxException, IOException, RequestSigningException {
        HttpTransport HTTP_TRANSPORT = new ApacheHttpTransport();
        HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
        URI uri = URI.create("https://ignored-hostname.com/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
        request.getHeaders().put("Host", "ignored-hostname.com");

        GoogleHttpClientEdgeGridRequestSigner googleHttpSigner = new GoogleHttpClientEdgeGridRequestSigner(credential);
        googleHttpSigner.sign(request, request);

        // NOTE: The library lower-cases all header names.
        assertThat(request.getHeaders().containsKey("Host"), is(false));
        assertThat(request.getHeaders().containsKey("host"), is(true));
        assertThat((String) request.getHeaders().get("host"), equalTo("endpoint.net"));
        assertThat(request.getUrl().getHost(), equalTo("endpoint.net"));
        assertThat(request.getHeaders().containsKey("authorization"), is(true));
        assertThat(request.getHeaders().getAuthorization(), not(isEmptyOrNullString()));
    }

}
