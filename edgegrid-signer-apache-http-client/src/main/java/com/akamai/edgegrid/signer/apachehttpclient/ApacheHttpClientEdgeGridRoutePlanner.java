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

import java.net.ProxySelector;

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.exceptions.NoMatchingCredentialException;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.conn.routing.HttpRoute;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;
/**
 * Apache HTTP Client binding for EdgeGrid route planner for computing {@link HttpRoute}.
 *
 * @author mgawinec@akamai.com
 */
public class ApacheHttpClientEdgeGridRoutePlanner extends SystemDefaultRoutePlanner {

    private final ApacheHttpClientEdgeGridRequestSigner binding;

    /**
     * Creates an EdgeGrid route planner using {@link ClientCredential}.
     *
     * @param clientCredential a {@link ClientCredential}
     */
    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredential clientCredential) {
        super(ProxySelector.getDefault());
        this.binding = new ApacheHttpClientEdgeGridRequestSigner(clientCredential);
    }

    /**
     * Creates an EdgeGrid route planner using {@link ClientCredentialProvider}.
     *
     * @param clientCredentialProvider a {@link ClientCredentialProvider}
     */
    public ApacheHttpClientEdgeGridRoutePlanner(ClientCredentialProvider clientCredentialProvider) {
        super(ProxySelector.getDefault());
        this.binding = new ApacheHttpClientEdgeGridRequestSigner(clientCredentialProvider);
    }

    @Override
    public HttpRoute determineRoute(HttpHost host, HttpRequest request, HttpContext context) throws HttpException {
        try {
            ClientCredential clientCredential = binding.getClientCredentialProvider().getClientCredential(binding.map(request));
            String hostname = clientCredential.getHost();
            int port = -1;
            final int pos = hostname.lastIndexOf(":");
            if (pos > 0) {
                try {
                    port = Integer.parseInt(hostname.substring(pos + 1));
                } catch (NumberFormatException ex) {
                    throw new IllegalArgumentException("Host contains invalid port number: " + hostname);
                }
                hostname = hostname.substring(0, pos);
            }
            HttpHost target = new HttpHost(hostname, port, "https");
            return super.determineRoute(target, request, context);
        } catch (NoMatchingCredentialException e) {
            throw new RuntimeException(e);
        }
    }
}
