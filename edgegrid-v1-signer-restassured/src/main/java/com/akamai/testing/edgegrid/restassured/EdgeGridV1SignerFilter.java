/*
 * Copyright 2016 Akamai Technologies, Inc. All Rights Reserved.
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


import com.akamai.testing.edgegrid.core.ClientCredential;
import com.akamai.testing.edgegrid.core.EdgeGridV1Signer;
import com.akamai.testing.edgegrid.core.Request;
import com.akamai.testing.edgegrid.core.RequestSigningException;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

/**
 * REST-assured filter that signs a request usings EdgeGrid V1 signing algorithm. Signing is a process of adding an Authorization header with a request signature.
 *
 * @see <a href="https://github.com/rest-assured/rest-assured/wiki/Usage#filters">REST-assured filters</a>
 * @author mgawinec@akamai.com
 */
public class EdgeGridV1SignerFilter implements Filter {

    private final ClientCredential credential;
    private final EdgeGridV1Signer edgeGridV1Signer;

    private EdgeGridV1SignerFilter(EdgeGridV1Signer edgeGridV1Signer, ClientCredential credential) {
        this.edgeGridV1Signer = edgeGridV1Signer;
        this.credential = credential;
    }

    /**
     * Creates a REST-assured filter that will sign a request with a given credential using a default signing
     * configuration. See {@link EdgeGridV1Signer} for default signing configuration.
     *
     * @param credential a client credential to sign a request
     * @return a REST-assured filter to be added to {@link io.restassured.specification.RequestSpecification} definition.
     */
    public static EdgeGridV1SignerFilter sign(ClientCredential credential) {
        return sign(new EdgeGridV1Signer(), credential);
    }

    /**
     * Creates a REST-assured filter that will sign a request with a given credential using a custom signer.
     *
     * @param edgeGridV1Signer a custom signer used to sign a request
     * @param credential       a client credential to sign a request
     * @return a REST-assured filter to be added to {@link io.restassured.specification.RequestSpecification} definition.
     */
    public static EdgeGridV1SignerFilter sign(EdgeGridV1Signer edgeGridV1Signer, ClientCredential credential) {
        return new EdgeGridV1SignerFilter(edgeGridV1Signer, credential);
    }

    private static Multimap<String, String> getHeaders(Headers headers) {
        Multimap<String, Header> indexedHeaders = Multimaps.index(headers.asList(), (Header::getValue));
        return Multimaps.transformValues(indexedHeaders, (Header::getValue));
    }

    @Override
    public Response filter(FilterableRequestSpecification requestSpec, FilterableResponseSpecification responseSpec, FilterContext ctx) {
        try {
            Request request = map(requestSpec);
            requestSpec.header("Authorization", edgeGridV1Signer.getAuthorizationHeaderValue(request, credential));
        } catch (RequestSigningException e) {
            throw new RuntimeException(e);
        }
        return ctx.next(requestSpec, responseSpec);
    }

    private Request map(FilterableRequestSpecification requestSpec) {
        return Request.builder()
                .method(requestSpec.getMethod())
                .uriWithQuery(requestSpec.getURI())
                .headers(getHeaders(requestSpec.getHeaders()))
                .body(requestSpec.getBody())
                .build();
    }
}
