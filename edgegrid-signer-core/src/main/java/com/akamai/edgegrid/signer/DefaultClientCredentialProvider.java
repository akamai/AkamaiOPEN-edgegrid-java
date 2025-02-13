package com.akamai.edgegrid.signer;

import java.util.Objects;

/**
 * This is a basic implementation of {@link ClientCredentialProvider} that returns the same
 * {@link ClientCredential} for every request.
 *
 */
public class DefaultClientCredentialProvider implements ClientCredentialProvider {

    private ClientCredential clientCredential;

    /**
     * Creates a {@link DefaultClientCredentialProvider} using {@link ClientCredential}.
     *
     * @param clientCredential a {@link ClientCredential}
     */
    public DefaultClientCredentialProvider(ClientCredential clientCredential) {
        this.clientCredential = Objects.requireNonNull(clientCredential, "clientCredential cannot be null");
    }
    @Override
    public ClientCredential getClientCredential(Request request) {
        return clientCredential;
    }

}
