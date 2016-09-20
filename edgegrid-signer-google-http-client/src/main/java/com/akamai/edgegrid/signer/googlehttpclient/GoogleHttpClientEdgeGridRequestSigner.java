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
package com.akamai.edgegrid.signer.googlehttpclient;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

import com.akamai.edgegrid.signer.AbstractEdgeGridRequestSigner;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;
import com.akamai.edgegrid.signer.RequestSigningException;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.Types;

/**
 * Google HTTP Client Library binding for EdgeGrid signer for signing {@link HttpRequest}.
 *
 * @author mgawinec@akamai.com
 */
public class GoogleHttpClientEdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<HttpRequest> {

    /**
     * Creates an EdgeGrid request signer using the same {@link ClientCredential} for all requests.
     *
     * @param clientCredential a {@link ClientCredential} to be used for all requests
     */
    public GoogleHttpClientEdgeGridRequestSigner(ClientCredential clientCredential) {
        super(clientCredential);
    }

    /**
     * Creates an EdgeGrid request signer selecting a {@link ClientCredential} via
     * {@link ClientCredentialProvider#getClientCredential(Request)} for each request.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider} to be used for selecting
     *        credentials for each request
     */
    public GoogleHttpClientEdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        super(clientCredentialProvider);
    }

    @Override
    protected Request map(HttpRequest request) throws RequestSigningException {
        Request.RequestBuilder builder = Request.builder()
                .method(request.getRequestMethod())
                .uriWithQuery(request.getUrl().toURI())
                .body(serializeContent(request));
        for (Map.Entry<String, Object> entry : request.getHeaders().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Iterable<?> || value.getClass().isArray()) {
                for (Object repeatedValue : Types.iterableOf(value)) {
                    // NOTE: Request is about to throw an exception!
                    builder.header(entry.getKey(), toStringValue(repeatedValue));
                }
            } else {
                builder.header(entry.getKey(), toStringValue(value));
            }
        }
        return builder.build();
    }

    @Override
    protected HttpRequest setAuthorization(HttpRequest request, String signature) {
        request.getHeaders().setAuthorization(signature);
        return request;
    }

    private byte[] serializeContent(HttpRequest request) {

        byte[] contentBytes;
        try {
            HttpContent content = request.getContent();
            if (content == null) {
                return new byte[]{};
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            content.writeTo(bos);
            contentBytes = bos.toByteArray();
            // for non-retryable content, reset the content for downstream handlers
            if (!content.retrySupported()) {
                HttpContent newContent = new ByteArrayContent(content.getType(), contentBytes);
                request.setContent(newContent);
            }
            return contentBytes;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String toStringValue(Object headerValue) {
        return headerValue instanceof Enum<?>
                ? FieldInfo.of((Enum<?>) headerValue).getName() : headerValue.toString();
    }

    @Override
    protected HttpRequest setHost(HttpRequest request, String host) {
        // NOTE: Header names are lower-cased by the library.
        if (request.getHeaders().containsKey("host")) {
            request.getHeaders().put("host", host);
        }
        request.getUrl().setHost(host);
        return request;
    }

}
