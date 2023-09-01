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

import com.akamai.edgegrid.signer.ClientCredential;
import com.akamai.edgegrid.signer.ClientCredentialProvider;
import com.akamai.edgegrid.signer.exceptions.RequestSigningException;

import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilderBase;
import org.asynchttpclient.SignatureCalculator;

/**
 * Async HTTP Client binding for EdgeGrid signature calculator {@link SignatureCalculator}.
 *
 * @author mgawinec@akamai.com
 */
public class AsyncHttpClientEdgeGridSignatureCalculator implements SignatureCalculator {

    private final AsyncHttpClientEdgeGridRequestSigner binding;

    /**
     * Creates an EdgeGrid signature calculator using {@link ClientCredential}.
     *
     * @param credential a {@link ClientCredential}
     */
    public AsyncHttpClientEdgeGridSignatureCalculator(ClientCredential credential) {
        this.binding = new AsyncHttpClientEdgeGridRequestSigner(credential);
    }

    /**
     * Creates an EdgeGrid signature calculator using {@link ClientCredentialProvider}.
     *
     * @param credentialProvider a {@link ClientCredentialProvider}
     */
    public AsyncHttpClientEdgeGridSignatureCalculator(ClientCredentialProvider credentialProvider) {
        this.binding = new AsyncHttpClientEdgeGridRequestSigner(credentialProvider);
    }

    @Override
    public void calculateAndAddSignature(Request request, RequestBuilderBase<?> requestToUpdate) {
        try {
            binding.sign(request, requestToUpdate);
        } catch (RequestSigningException e) {
            throw new RuntimeException(e);
        }
    }
}
