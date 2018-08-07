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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.Matchers.not;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.testng.annotations.Test;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;

/**
 * Unit tests for {@link GoogleHttpClientEdgeGridInterceptor}.
 *
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public class GoogleHttpClientEdgeGridInterceptorTest {

    ClientCredential credential = ClientCredential.builder()
            .accessToken("akaa-dm5g2bfwoodqnc6k-ju7vlao2wz6oz2rp")
            .clientToken("akaa-k7glklzuxkkh2ycw-oadjphopvpn6yjoj")
            .clientSecret("SOMESECRET")
            .host("endpoint.net")
            .build();

    @Test
    public void testInterceptor() throws URISyntaxException, IOException, RequestSigningException {
        HttpRequestFactory requestFactory = createSigningRequestFactory();

        URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
        // Mimic what the library does to process the interceptor.
        request.getInterceptor().intercept(request);

        assertThat(request.getHeaders().containsKey("host"), is(false));
        assertThat(request.getUrl().getHost(), equalTo("endpoint.net"));
        assertThat(request.getHeaders().getAuthorization(), not(isEmptyOrNullString()));
    }

    @Test
    public void testInterceptorWithHeader() throws URISyntaxException, IOException, RequestSigningException {
        HttpRequestFactory requestFactory = createSigningRequestFactory();

        URI uri = URI.create("https://endpoint.net/billing-usage/v1/reportSources");
        HttpRequest request = requestFactory.buildGetRequest(new GenericUrl(uri));
        request.getHeaders().put("Host", "ignored-hostname.com");
        // Mimic what the library does to process the interceptor.
        request.getInterceptor().intercept(request);

        assertThat(request.getHeaders().containsKey("Host"), is(false));
        assertThat(request.getHeaders().containsKey("host"), is(true));
        assertThat((String) request.getHeaders().get("host"), equalTo("endpoint.net"));
        assertThat(request.getUrl().getHost(), equalTo("endpoint.net"));
        assertThat(request.getHeaders().getAuthorization(), not(isEmptyOrNullString()));
    }

    private HttpRequestFactory createSigningRequestFactory() {
        HttpTransport httpTransport = new ApacheHttpTransport();
        return httpTransport.createRequestFactory(new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest request) throws IOException {
                request.setInterceptor(new GoogleHttpClientEdgeGridInterceptor(credential));
            }
        });
    }

}
