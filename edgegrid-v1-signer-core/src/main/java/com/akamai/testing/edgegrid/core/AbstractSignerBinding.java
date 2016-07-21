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

/**
 * A template for signer bindings for specific HTTP client.
 *
 * @param <RequestT> a type of HTTP client specific request.
 *
 * @author mgawinec@akamai.com
 */
public abstract class AbstractSignerBinding<RequestT> {

    private final EdgeGridV1Signer edgeGridSigner;

    /**
     * Creates signer binding with a default EdgeGrid signer.
     */
    public AbstractSignerBinding() {
        this(new EdgeGridV1Signer());
    }

    /**
     * Creates signer binding with a custom EdgeGrid signer.
     * @param edgeGridSigner EdgeGrid signer that will be used to sign requests.
     */
    public AbstractSignerBinding(EdgeGridV1Signer edgeGridSigner) {
        this.edgeGridSigner = edgeGridSigner;
    }

    /**
     * Signs a given requests with a given client credential using EdgeGrid signer algorithm.
     * @param request a HTTP request to sign
     * @param credential a client credential to sign a request with
     * @throws RequestSigningException if failed to sign a request
     */
    public void sign(RequestT request, ClientCredential credential) throws RequestSigningException{
        String authorization = edgeGridSigner.getAuthorizationHeaderValue(map(request), credential);
        setAuthorization(request, authorization);
    }

    /**
     * Maps HTTP client-specific request to client-agnostic model of this request.
     * @param request HTTP client-specific request
     * @return an instance of <code>Request</code> corresponding to a given
     */
    protected abstract Request map(RequestT request);

    /**
     * Updates a given HTTP request by adding Authorization header with a value containing request signature.
     * @param request HTTP request to update
     * @param signature HTTP request signature
     * @return updated request
     */
    protected abstract RequestT setAuthorization(RequestT request, String signature);
}
