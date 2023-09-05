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

package com.akamai.edgegrid.signer.apachehttpclient;

import com.akamai.edgegrid.signer.AbstractEdgeGridRequestSigner;
import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.Request;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.RequestWrapper;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;

/**
 * Apache HTTP Client binding for EdgeGrid signer for signing {@link HttpRequest}.
 *
 * @author mgawinec@akamai.com
 */
public class ApacheHttpClientEdgeGridRequestSigner extends AbstractEdgeGridRequestSigner<HttpRequest, HttpRequest> {

    /**
     * Creates an EdgeGrid signer using {@link ClientCredential}.
     *
     * @param clientCredential a {@link ClientCredential}
     */
    public ApacheHttpClientEdgeGridRequestSigner(ClientCredential clientCredential) {
        super(clientCredential);
    }

    /**
     * Creates an EdgeGrid signer using {@link ClientCredentialProvider}.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public ApacheHttpClientEdgeGridRequestSigner(ClientCredentialProvider clientCredentialProvider) {
        super(clientCredentialProvider);
    }

    @Override
    protected URI requestUri(HttpRequest request) {
      if (request instanceof HttpRequestWrapper) {
          String uri = ((HttpRequestWrapper) request).getOriginal().getRequestLine().getUri();
          return URI.create(uri);
      } else if (request instanceof RequestWrapper) {
          String uri = ((RequestWrapper) request).getOriginal().getRequestLine().getUri();
          return URI.create(uri);
      } else {
          return ((HttpRequestBase) request).getURI();
      }
    }

    @Override
    protected Request map(HttpRequest request) {
        Request.RequestBuilder builder = Request.builder()
                .method(request.getRequestLine().getMethod())
                .uri(request.getRequestLine().getUri())
                .body(serializeContent(request));
        for (Header h : request.getAllHeaders()) {
            builder.header(h.getName(), h.getValue());
        }

        return builder.build();
    }

    private byte[] serializeContent(HttpRequest request) {
        if (!(request instanceof HttpEntityEnclosingRequest)) {
            return new byte[]{};
        }

        final HttpEntityEnclosingRequest entityWithRequest = (HttpEntityEnclosingRequest) request;
        HttpEntity entity = entityWithRequest.getEntity();
        if (entity == null) {
            return new byte[]{};
        }

        try {
            // Buffer non-repeatable entities
            if (!entity.isRepeatable()) {
                entityWithRequest.setEntity(new BufferedHttpEntity(entity));
            }
            return EntityUtils.toByteArray(entityWithRequest.getEntity());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void setAuthorization(HttpRequest request, String signature) {
        request.setHeader("Authorization", signature);
    }

    @Override
    protected void setHost(HttpRequest request, String host, URI uri) {
        request.setHeader("Host", host);
        setRequestUri(request, uri);
    }

    private void setRequestUri(HttpRequest request, URI uri) {
        if (request instanceof HttpRequestWrapper) {
            setRequestUri(((HttpRequestWrapper) request).getOriginal(), uri);
        } else if (request instanceof RequestWrapper) {
            setRequestUri(((RequestWrapper) request).getOriginal(), uri);
        } else {
            ((HttpRequestBase) request).setURI(uri);
        }
    }

}
