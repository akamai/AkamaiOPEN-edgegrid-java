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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.akamai.edgegrid.signer.AbstractEdgeGridRequestSigner;
import com.akamai.edgegrid.signer.Request;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.util.FieldInfo;
import com.google.api.client.util.Types;

/**
 * Google HTTP Client Library binding for EdgeGrid signer for signing {@link HttpRequest}.
 *
 * @author mgawinec@akamai.com
 */
public class GoogleHttpClientEdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<HttpRequest> {

    private static Map<String, List<String>> getHeaders(HttpHeaders headers) {
        Map<String, List<String>> ret = new HashMap<>();
        for (Map.Entry<String, Object> e : headers.entrySet()) {
            List<String> values = ret.get(e.getKey());
            if (values == null) {
                values = new LinkedList<>();
                ret.put(e.getKey(), values);
            }
            Object value = e.getValue();
            if (value instanceof Iterable<?> || value.getClass().isArray()) {
                for (Object repeatedValue : Types.iterableOf(value)) {
                    values.add(toStringValue(repeatedValue));
                }
            } else {
                values.add(toStringValue(value));
            }
        }
        return ret;
    }

    /**
     * Creates an EdgeGrid request signer for Google HTTP Client.
     */
    public GoogleHttpClientEdgeGridRequestSigner() {
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
