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
package com.akamai.testing.edgegrid.googlehttp;


import com.akamai.testing.edgegrid.core.ClientCredential;
import com.akamai.testing.edgegrid.core.EdgeGridV1Signer;
import com.akamai.testing.edgegrid.core.RequestSigningException;
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
public class GoogleHttpSignInterceptor implements HttpExecuteInterceptor {

    private final GoogleHttpSigner binding;
    private final ClientCredential credential;

    /**
     * Creates a sign interceptor with default EdgeGrid signer.
     *
     * @param credential
     */
    public GoogleHttpSignInterceptor(ClientCredential credential) {
        this(new EdgeGridV1Signer(), credential);
    }

    /**
     * Creates a sign interceptor with a custom EdgeGrid signer.
     *
     * @param edgeGridSigner a custom edge grid signer that will be used to sign requests
     */
    public GoogleHttpSignInterceptor(EdgeGridV1Signer edgeGridSigner, ClientCredential credential) {
        this.binding = new GoogleHttpSigner(edgeGridSigner);
        this.credential = credential;
    }


    @Override
    public void intercept(HttpRequest request) throws IOException {
        try {
            binding.sign(request, credential);
        } catch (RequestSigningException e) {
            throw new RuntimeException(e);
        }
    }
}
