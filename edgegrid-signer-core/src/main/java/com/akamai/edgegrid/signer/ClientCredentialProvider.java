package com.akamai.edgegrid.signer;

public interface ClientCredentialProvider {

    /**
     * Gets a {@link ClientCredential} that is appropriate for signing {@code request}. The result
     * of this method may be {@code null} if no reasonable {@link ClientCredential} can be located.
     *
     * @param request a Request
     * @return a {@link ClientCredential} (can be {@code null})
     */
    public ClientCredential getClientCredential(Request request);

}
