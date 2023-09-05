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

package com.akamai.edgegrid.signer.ahc;

import com.google.common.primitives.Bytes;

import com.akamai.edgegrid.signer.AbstractEdgeGridRequestSigner;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;

import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilderBase;
import org.asynchttpclient.request.body.generator.FileBodyGenerator;
import org.asynchttpclient.request.body.generator.InputStreamBodyGenerator;
import org.asynchttpclient.request.body.generator.ReactiveStreamsBodyGenerator;
import org.asynchttpclient.uri.Uri;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.asynchttpclient.util.MiscUtils.isNonEmpty;

/**
 * Async HTTP Client binding for EdgeGrid signer for signing {@link Request}.
 *
 * @author mgawinec@akamai.com
 */
public class AsyncHttpClientEdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<Request, RequestBuilderBase> {

    /**
     * Creates an EdgeGrid signer using {@link ClientCredential}.
     *
     * @param credential a {@link ClientCredential}
     */
    public AsyncHttpClientEdgeGridRequestSigner(ClientCredential credential) {
        super(credential);
    }

    /**
     * Creates an EdgeGrid signer using {@link ClientCredentialProvider}.
     *
     * @param credentialProvider a {@link ClientCredentialProvider}
     */
    public AsyncHttpClientEdgeGridRequestSigner(ClientCredentialProvider credentialProvider) {
        super(credentialProvider);
    }

    @Override
    protected URI requestUri(Request request) {
        try {
            return request.getUri().toJavaNetURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected com.akamai.edgegrid.signer.Request map(Request request) {

        com.akamai.edgegrid.signer.Request.RequestBuilder builder = com.akamai.edgegrid.signer.Request.builder()
            .uri(request.getUrl())
            .method(request.getMethod())
            .body(serializeBody(request));

        for (Map.Entry<String, String> e : request.getHeaders().entries()) {
            builder.header(e.getKey(), e.getValue());
        }

        return builder.build();
    }

    private byte[] serializeBody(Request request) {

        if (request.getByteData() != null) {
            return request.getByteData();
        } else if (request.getCompositeByteData() != null) {
            List<Byte> buff = new ArrayList<>();
            for (byte[] bytes : request.getCompositeByteData()) {
                buff.addAll(Bytes.asList(bytes)); // Without Guava that would be quite cumbersome
            }
            return Bytes.toArray(buff);
        } else if (request.getStringData() != null) {
            return request.getStringData().getBytes();
        } else if (request.getByteBufferData() != null) {
            throw new UnsupportedOperationException("Serializing ByteBufferData in request body is not supported");
        } else if (request.getStreamData() != null) {
            throw new UnsupportedOperationException("Serializing StreamData in request body is not supported");
        } else if (isNonEmpty(request.getFormParams())) {
            throw new UnsupportedOperationException("Serializing FormParams in request body is not supported");
        } else if (isNonEmpty(request.getBodyParts())) {
            throw new UnsupportedOperationException("Serializing BodyParts in request body is not supported");
        } else if (request.getFile() != null) {
            throw new UnsupportedOperationException("Serializing File in request body is not supported");
        } else if (request.getBodyGenerator() instanceof FileBodyGenerator) {
            throw new UnsupportedOperationException("Serializing FileBodyGenerator in request body is not supported");
        } else if (request.getBodyGenerator() instanceof InputStreamBodyGenerator) {
            throw new UnsupportedOperationException("Serializing InputStreamBodyGenerator in request body is not supported");
        } else if (request.getBodyGenerator() instanceof ReactiveStreamsBodyGenerator) {
            throw new UnsupportedOperationException("Serializing ReactiveStreamsBodyGenerator in request body is not supported");
        } else if (request.getBodyGenerator() != null) {
            throw new UnsupportedOperationException("Serializing generic BodyGenerator in request body is not supported");
        } else {
            return new byte[]{};
        }
    }


    @Override
    protected void setAuthorization(RequestBuilderBase requestToUpdate, String signature) {
        requestToUpdate.setHeader("Authorization", signature);
    }

    @Override
    protected void setHost(RequestBuilderBase requestToUpdate, String host, URI uri) {
        requestToUpdate.setHeader("Host", host);
        // uri already contains query params that existed in original query
        requestToUpdate.resetQuery();
        requestToUpdate.setUri(toAsyncHttpClientUri(uri));
    }

    private static Uri toAsyncHttpClientUri(URI uri) {
        return Uri.create(uri.toString());
    }

}
