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

package com.akamai.edgegrid.signer;

import com.akamai.edgegrid.signer.exceptions.NoMatchingCredentialException;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Objects;

/**
 * <p> This is an abstract base class for implementing EdgeGrid request signing in a
 * library-specific way. There are several HTTP client libraries available for Java, and this class
 * offers a simple mechanism for supporting them. </p> <p> This class uses a {@link
 * ClientCredentialProvider} to select a {@link ClientCredential} appropriately for each request.
 * This interface permits sharing a single instance of the class for a variety of API calls. It also
 * offers a more configurable way to retrieve credentials any way the user wants. </p>
 *
 * @param <RequestT> a type of HTTP client specific request.
 * @param <MutableRequestT> a type of HTTP client request to update.
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public abstract class AbstractEdgeGridRequestSigner<RequestT, MutableRequestT> {

    private final ClientCredentialProvider clientCredentialProvider;

    private final EdgeGridV1Signer edgeGridSigner;

    /**
     * Creates an EdgeGrid request signer that will always sign requests with the same {@link
     * ClientCredential}. This constructor will automatically produce a {@link
     * DefaultClientCredentialProvider} out of {@code clientCredential}.
     *
     * @param clientCredential a {@link ClientCredential} to be used for all requests
     */
    public AbstractEdgeGridRequestSigner(ClientCredential clientCredential) {
        this(new DefaultClientCredentialProvider(clientCredential));
    }

    /**
     * Creates an EdgeGrid request signer selecting a {@link ClientCredential} via {@link
     * ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider} to be used for selecting
     *                                 credentials for each request
     */
    public AbstractEdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        this.clientCredentialProvider = clientCredentialProvider;
        this.edgeGridSigner = createEdgeGridSigner();
    }

    /**
     * Returns new instance of EdgeGridV1Signer.
     *
     * @return a {@link EdgeGridV1Signer} new instance
     */
    protected EdgeGridV1Signer createEdgeGridSigner() {
        return new EdgeGridV1Signer();
    }

    /**
     * Retrieves {@link ClientCredentialProvider}.
     *
     * @return  {@link ClientCredentialProvider}
     *
     */
    public final ClientCredentialProvider getClientCredentialProvider() {
        return clientCredentialProvider;
    }

    /**
     * Signs {@code request} with appropriate credentials using EdgeGrid signer algorithm and
     * replaces {@code request}'s host name with the one specified by the credential.
     *
     * @param request an HTTP request with data used to sign
     * @param requestToUpdate an HTTP request to update with signature
     * @throws RequestSigningException       if failed to sign a request
     * @throws NoMatchingCredentialException if acquiring a {@link ClientCredential} throws {@code
     *                                       NoMatchingCredentialException} or returns {@code null}
     */
    public void sign(RequestT request, MutableRequestT requestToUpdate) throws RequestSigningException {
        Request req = map(request);
        ClientCredential credential;
        try {
            credential = clientCredentialProvider.getClientCredential(req);
        } catch (NoMatchingCredentialException e) {
            throw e;
        }
        if (credential == null) {
            throw new NoMatchingCredentialException();
        }
        String newHost = credential.getHost();
        URI originalUri = Objects.requireNonNull(requestUri(request), "Request-URI cannot be null");
        URI newUri = withNewHost(originalUri, newHost);
        setHost(requestToUpdate, newHost, newUri);
        String authorization = edgeGridSigner.getSignature(req, credential);
        setAuthorization(requestToUpdate, authorization);
    }

    /**
     * Returns Request-URI of an original request.
     *
     * @param request an HTTP client-specific request
     * @return a {@link URI} of {@code request}
     */
    protected abstract URI requestUri(RequestT request);

    /**
     * Maps HTTP client-specific request to client-agnostic model of this request.
     *
     * @param request an HTTP client-specific request
     * @return a {@link Request} representation of {@code request}
     * @throws IllegalArgumentException if duplicate header definitions are found
     */
    protected abstract Request map(RequestT request);

    /**
     * Updates a given HTTP request by adding Authorization header with a value containing request
     * signature.
     *
     * @param request   HTTP request to update
     * @param signature HTTP request signature
     */
    protected abstract void setAuthorization(MutableRequestT request, String signature);

    /**
     * Updates a given HTTP request by replacing the request hostname with {@code host} instead.
     * Usually, it means updating Host header and Request-URI.
     *
     * @param request HTTP request to update
     * @param host    an OPEN API hostname
     * @param uri     request URI with OPEN API hostname
     */
    protected abstract void setHost(MutableRequestT request, String host, URI uri);

    private URI withNewHost(URI uri, String host) {
        // We allow host to contain port only for because mocking OPEN API service requires it
        String[] hostAndPort = host.split(":");
        String hostName = hostAndPort[0];
        int port = (hostAndPort.length == 2)
            ? Integer.parseInt(hostAndPort[1])
            : uri.getPort();

        try {
            return new URI(
                uri.getScheme(),
                uri.getUserInfo(),
                hostName,
                port,
                uri.getPath(),
                uri.getQuery(),
                uri.getFragment());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
