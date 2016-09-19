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


import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.specification.FilterableRequestSpecification;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.akamai.edgegrid.signer.AbstractEdgeGridRequestSigner;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;

/**
 * REST-assured binding of EdgeGrid signer for signing {@link FilterableRequestSpecification}.
 * @author mgawinec@akamai.com
 */
public class RestAssuredEdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<FilterableRequestSpecification> {

    private static Map<String, List<String>> getHeaders(Headers headers) {
        Map<String, List<String>> ret = new HashMap<>();
        for (Header header : headers) {
            List<String> values = ret.get(header.getName());
            if (values == null) {
                values = new LinkedList<>();
                ret.put(header.getName(), values);
            }
            values.add(header.getValue());
        }
        return ret;
    }

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
     *        credentials for each request
     */
    public RestAssuredEdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        super(clientCredentialProvider);
    }

    @Override
    protected Request map(FilterableRequestSpecification requestSpec) {
        return Request.builder()
                .method(requestSpec.getMethod())
                .uriWithQuery(URI.create(requestSpec.getURI()))
                .headers(getHeaders(requestSpec.getHeaders()))
                .body(requestSpec.getBody() != null ? requestSpec.<byte[]>getBody() : new byte[]{} )
                .build();
    }

    @Override
    protected FilterableRequestSpecification setAuthorization(FilterableRequestSpecification requestSpec, String signature) {
        requestSpec.header("Authorization", signature);
        return requestSpec;
    }

    @Override
    protected FilterableRequestSpecification setHost(FilterableRequestSpecification requestSpec, String host) {
        if (requestSpec.getHeaders().hasHeaderWithName("Host")) {
            requestSpec.header("Host", host);
        }
        // REST-assured needs to deal with the specific hostname at the time of the call.
        return requestSpec;
    }

}
