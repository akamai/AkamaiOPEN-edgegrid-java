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

package com.akamai.edgegrid.signer.apachehttpclient;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;

import java.io.IOException;

/**
 * Apache HTTP Client Library interceptor that signs a request using EdgeGrid V1 signing algorithm.
 * Signing is a process of adding an Authorization header with a request signature. If signing fails then <code>RuntimeException</code> is thrown.
 *
 * @see <a href="https://hc.apache.org/httpcomponents-core-ga/httpcore/apidocs/org/apache/http/HttpRequestInterceptor.html">HttpRequestInterceptor</a> from Apache HTTP Client
 * @author mgawinec@akamai.com
 */
public class ApacheHttpClientEdgeGridInterceptor implements HttpRequestInterceptor {

    private final ApacheHttpClientEdgeGridRequestSigner binding;

    /**
     * Creates an EdgeGrid signing interceptor using the same {@link ClientCredential} for each
     * request.
     *
     * @param credential a {@link ClientCredential}
     */
    public ApacheHttpClientEdgeGridInterceptor(ClientCredential credential) {
        this.binding = new ApacheHttpClientEdgeGridRequestSigner(credential);
    }

    /**
     * Creates an EdgeGrid signing interceptor selecting a {@link ClientCredential} via
     * {@link ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public ApacheHttpClientEdgeGridInterceptor(ClientCredentialProvider clientCredentialProvider) {
        this.binding = new ApacheHttpClientEdgeGridRequestSigner(clientCredentialProvider);
    }

    @Override
    public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
        try {
            binding.sign(request, request);
        } catch (RequestSigningException e) {
            throw new RuntimeException(e);
        }
    }
}
