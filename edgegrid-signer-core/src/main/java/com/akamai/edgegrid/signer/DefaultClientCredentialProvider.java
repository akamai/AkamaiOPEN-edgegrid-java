package com.akamai.edgegrid.signer;

/**
 * This is a basic implementation of {@link ClientCredentialProvider} that returns the same
 * {@link ClientCredential} for every request.
 *
 * @author mmeyer
 */
public class DefaultClientCredentialProvider implements ClientCredentialProvider {

    private ClientCredential clientCredential;

    public DefaultClientCredentialProvider(ClientCredential clientCredential) {
        this.clientCredential = clientCredential;
    }
    @Override
    public ClientCredential getClientCredential(Request request) {
        return clientCredential;
    }

}
