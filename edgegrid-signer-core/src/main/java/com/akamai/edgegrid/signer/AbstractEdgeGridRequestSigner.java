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

import com.akamai.edgegrid.signer.exceptions.NoMatchingCredentialException;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

/**
 * <p>
 * This is an abstract base class for implementing EdgeGrid request signing in a library-specific
 * way. There are several HTTP client libraries available for Java, and this class offers a simple
 * mechanism for supporting them.
 * </p>
 * <p>
 * This class uses a {@link ClientCredentialProvider} to select a {@link ClientCredential}
 * appropriately for each request. This interface permits sharing a single instance of the class for
 * a variety of API calls. It also offers a more configurable way to retrieve credentials any way
 * the user wants.
 * </p>
 *
 * @param <RequestT> a type of HTTP client specific request.
 *
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public abstract class AbstractEdgeGridRequestSigner<RequestT> {

    private final ClientCredentialProvider clientCredentialProvider;

    private final EdgeGridV1Signer edgeGridSigner;

    /**
     * Creates an EdgeGrid request signer that will always sign requests with the same
     * {@link ClientCredential}. This constructor will automatically produce a
     * {@link DefaultClientCredentialProvider} out of {@code clientCredential}.
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
     * @throws NoMatchingCredentialException if acquiring a {@link ClientCredential} throws or
     *         returns {@code null}
     */
    public void sign(RequestT request) throws RequestSigningException {
        Request req = null;
        try {
            req = map(request);
        } catch (Exception e) {
            throw new NoMatchingCredentialException(e);
        }
        if (req == null) {
            throw new NoMatchingCredentialException();
        }
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
     * @throws RequestSigningException if duplicate header definitions are found
     */
    protected abstract Request map(RequestT request) throws RequestSigningException;

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
