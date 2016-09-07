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
package com.akamai.testing.edgegrid.googlehttp;


import com.akamai.testing.edgegrid.core.*;
import com.google.api.client.http.*;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.Types;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Google HTTP Client Library binding for EdgeGrid signer for signing {@link HttpRequest}.
 *
 * @author mgawinec@akamai.com
 */
public class GoogleHttpSigner extends AbstractSignerBinding<HttpRequest> {


    /**
     * Creates a signer binding with default EdgeGrid signer.
     */
    public GoogleHttpSigner() {
        super();
    }

    /**
     * Creates a signer binding with a custom EdgeGrid signer.
     * @param edgeGridSigner a custom edge grid signer that will be used to sign requests
     */
    public GoogleHttpSigner(EdgeGridV1Signer edgeGridSigner) {
        super(edgeGridSigner);
    }

    @Override
    protected Request map(HttpRequest request) {
        return Request.builder()
                .method(request.getRequestMethod())
                .uriWithQuery(request.getUrl().toURI())
                .headers(getHeaders(request.getHeaders()))
                .body(serializeContent(request))
                .build();
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

    private Multimap<String, String> getHeaders(HttpHeaders headers) {

        Multimap<String, String> newHeaders = HashMultimap.create();
        for (Map.Entry<String, Object> e : headers.entrySet()) {
            Object value = e.getValue();
            if (value instanceof Iterable<?> || value.getClass().isArray()) {
                for (Object repeatedValue : Types.iterableOf(value)) {
                    newHeaders.put(e.getKey(),  toStringValue(repeatedValue));
                }
            } else {
                newHeaders.put(e.getKey(), toStringValue(value));
            }
        }
        return newHeaders;
    }

    private static String toStringValue(Object headerValue) {
        return headerValue instanceof Enum<?>
                ? FieldInfo.of((Enum<?>) headerValue).getName() : headerValue.toString();
    }

}
