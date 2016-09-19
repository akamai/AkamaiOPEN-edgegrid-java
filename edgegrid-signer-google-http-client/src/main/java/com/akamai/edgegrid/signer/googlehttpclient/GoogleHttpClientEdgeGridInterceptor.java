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
package com.akamai.edgegrid.signer.googlehttpclient;


import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;
import com.akamai.edgegrid.signer.RequestSigningException;
import com.google.api.client.http.HttpExecuteInterceptor;
import com.google.api.client.http.HttpRequest;

import java.io.IOException;

/**
 * Google HTTP Client Library interceptor that signs a request using EdgeGrid V1 signing algorithm.
 * Signing is a process of adding an Authorization header with a request signature. If signing fails then <code>RuntimeException</code> is thrown.
 *
 * @see <a href="https://developers.google.com/api-client-library/java/google-http-java-client/reference/1.20.0/com/google/api/client/http/HttpExecuteInterceptor">HttpExecuteInterceptor</a> from Google HTTP Client library for Java
 * @author mgawinec@akamai.com
 */
public class GoogleHttpClientEdgeGridInterceptor implements HttpExecuteInterceptor {

    private final GoogleHttpClientEdgeGridRequestSigner binding;

    /**
     * Creates an EdgeGrid signing interceptor using the same {@link ClientCredential} for each
     * request.
     *
     * @param credential a {@link ClientCredential}
     */
    public GoogleHttpClientEdgeGridInterceptor(ClientCredential credential) {
        this.binding = new GoogleHttpClientEdgeGridRequestSigner(credential);
    }

    /**
     * Creates an EdgeGrid signing interceptor selecting a {@link ClientCredential} via
     * {@link ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public GoogleHttpClientEdgeGridInterceptor(ClientCredentialProvider clientCredentialProvider) {
        this.binding = new GoogleHttpClientEdgeGridRequestSigner(clientCredentialProvider);
    }

    @Override
    public void intercept(HttpRequest request) throws IOException {
        try {
            binding.sign(request);
        } catch (RequestSigningException e) {
            throw new RuntimeException(e);
        }
    }
}
