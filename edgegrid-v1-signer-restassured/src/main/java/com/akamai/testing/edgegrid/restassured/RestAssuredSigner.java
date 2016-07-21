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

package com.akamai.testing.edgegrid.restassured;


import com.akamai.testing.edgegrid.core.AbstractSignerBinding;
import com.akamai.testing.edgegrid.core.EdgeGridV1Signer;
import com.akamai.testing.edgegrid.core.Request;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.specification.FilterableRequestSpecification;

import java.net.URI;

/**
 * REST-assured binding of EdgeGrid signer for signing {@link FilterableRequestSpecification}.
 * @author mgawinec@akamai.com
 */
public class RestAssuredSigner extends AbstractSignerBinding<FilterableRequestSpecification> {

    /**
     * Creates a signer binding with default EdgeGrid signer.
     */
    public RestAssuredSigner() {
        super();
    }

    /**
     * Creates a signer binding with a custom EdgeGrid signer.
     * @param edgeGridSigner a custom edge grid signer that will be used to sign requests
     */
    public RestAssuredSigner(EdgeGridV1Signer edgeGridSigner) {
        super(edgeGridSigner);
    }

    @Override
    protected Request map(FilterableRequestSpecification requestSpec) {
        return Request.builder()
                .method(requestSpec.getMethod())
                .uriWithQuery(URI.create(requestSpec.getURI()))
                .headers(getHeaders(requestSpec.getHeaders()))
                .body(requestSpec.getBody() != null ? requestSpec.getBody() : new byte[]{} )
                .build();
    }

    private static Multimap<String, String> getHeaders(Headers headers) {
        Multimap<String, Header> indexedHeaders = Multimaps.index(headers.asList(), (Header::getValue));
        return Multimaps.transformValues(indexedHeaders, (Header::getValue));
    }

    @Override
    protected FilterableRequestSpecification setAuthorization(FilterableRequestSpecification requestSpec, String signature) {
        requestSpec.header("Authorization", signature);
        return requestSpec;
    }
}
