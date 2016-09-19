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

/**
 * This is an abstract base class for implementing EdgeGrid request signing in a library-specific
 * way. There are several HTTP client libraries available for Java, and this class offers a simple
 * mechanism for supporting them.
 *
 * @param <RequestT> a type of HTTP client specific request.
 *
 * @author mgawinec@akamai.com
 */
public abstract class AbstractEdgeGridRequestSigner<RequestT> {

    private final ClientCredentialProvider clientCredentialProvider;

    private final EdgeGridV1Signer edgeGridSigner;

    /**
     * Creates an EdgeGrid request signer using the same {@link ClientCredential} for all requests.
     *
     * @param clientCredential a {@link ClientCredential} to be used for all requests
     */
    public AbstractEdgeGridRequestSigner(ClientCredential clientCredential) {
        this(new DefaultClientCredentialProvider(clientCredential));
    }

    /**
     * Creates an EdgeGrid request signer selecting a {@link ClientCredential} via
     * {@link ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider} to be used for selecting
     *        credentials for each request
     */
    public AbstractEdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        this.clientCredentialProvider = clientCredentialProvider;
        this.edgeGridSigner = new EdgeGridV1Signer();
    }

    /**
     * Signs {@code request} with appropriate credentials using EdgeGrid signer algorithm and
     * replaces {@code request}'s host name with the one specified by the credential.
     *
     * @param request an HTTP request to sign
     * @throws RequestSigningException if failed to sign a request
     */
    public void sign(RequestT request) throws RequestSigningException {
        Request req = map(request);
        ClientCredential credential = clientCredentialProvider.getClientCredential(req);
        setHost(request, credential.getHost());
        String authorization = edgeGridSigner.getSignature(req, credential);
        setAuthorization(request, authorization);
    }

    /**
     * Maps HTTP client-specific request to client-agnostic model of this request.
     *
     * @param request an HTTP client-specific request
     * @return a {@link Request} representation of {@code request}
     */
    protected abstract Request map(RequestT request);

    /**
     * Updates a given HTTP request by adding Authorization header with a value containing request
     * signature.
     *
     * @param request HTTP request to update
     * @param signature HTTP request signature
     * @return updated request
     */
    protected abstract RequestT setAuthorization(RequestT request, String signature);

    /**
     * Updates a given HTTP request by replacing the request hostname with {@code host} instead.
     *
     * @param request HTTP request to update
     * @param host an OPEN API hostname
     * @return updated request
     */
    protected abstract RequestT setHost(RequestT request, String host);

}
