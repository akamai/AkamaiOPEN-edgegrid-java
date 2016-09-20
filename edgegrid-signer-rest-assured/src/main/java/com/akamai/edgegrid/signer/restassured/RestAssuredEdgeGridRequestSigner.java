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

package com.akamai.edgegrid.signer.restassured;


import com.akamai.edgegrid.signer.*;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.specification.FilterableRequestSpecification;
import org.apache.commons.lang3.Validate;

import java.lang.reflect.Field;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * REST-assured binding of EdgeGrid signer for signing {@link FilterableRequestSpecification}.
 * The request specification must contain a relative path in {@code get(path)}, {@code post(path)}, {@code put(path)},
 * etc. methods. Request specifications with absolute path in those methods will result in {@code IllegalArgumentException }.
 *
 * @author mgawinec@akamai.com
 */
public class RestAssuredEdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<FilterableRequestSpecification> {

    /**
     * Creates an EdgeGrid request signer using the same {@link ClientCredential} for all requests.
     *
     * @param clientCredential a {@link ClientCredential} to be used for all requests
     */
    public RestAssuredEdgeGridRequestSigner(ClientCredential clientCredential) {
        super(clientCredential);
    }

    /**
     * Creates an EdgeGrid request signer selecting a {@link ClientCredential} via
     * {@link ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider} to be used for selecting
     *                                 credentials for each request
     */
    public RestAssuredEdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        super(clientCredentialProvider);
    }

    @Override
    protected Request map(FilterableRequestSpecification requestSpec)
            throws RequestSigningException {
        Request.RequestBuilder builder = Request.builder()
                .method(requestSpec.getMethod())
                .uriWithQuery(URI.create(requestSpec.getURI()))
                .body(requestSpec.getBody() != null ? requestSpec.<byte[]>getBody() : new byte[]{});
        for (Header header : requestSpec.getHeaders()) {
            builder.header(header.getName(), header.getValue());
        }
        return builder.build();
    }

    @Override
    protected FilterableRequestSpecification setAuthorization(FilterableRequestSpecification requestSpec, String signature) {
        requestSpec.header("Authorization", signature);
        return requestSpec;
    }

    @Override
    protected FilterableRequestSpecification setHost(FilterableRequestSpecification requestSpec, String host) {

        // Due to limitations of REST-assured design only requests with relative paths can be updated
        Validate.isTrue(isRelativeUrl(getRequestPath(requestSpec)), "path in request cannot be absolute");

        requestSpec
                .baseUri("http://" + host)
                .header("Host", host);

        return requestSpec;
    }

    private String getRequestPath(FilterableRequestSpecification requestSpec) {
        try {
            Field f = requestSpec.getClass().getDeclaredField("path");
            f.setAccessible(true);
            return (String) f.get(requestSpec);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e); // should never occur
        }
    }

    private boolean isRelativeUrl(String uri) {
        return !URI.create(uri).isAbsolute();
    }

}
