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
