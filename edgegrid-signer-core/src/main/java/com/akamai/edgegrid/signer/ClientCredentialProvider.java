package com.akamai.edgegrid.signer;

import com.akamai.edgegrid.signer.exceptions.NoMatchingCredentialException;

/**
 * <p>
 * This interface provides a mechanism to select a {@link ClientCredential}. Implementations of
 * {@link AbstractEdgeGridRequestSigner} will call {@link #getClientCredential(Request)} during the
 * request signing phase to select the {@link ClientCredential} to be used.
 * </p>
 * <p>
 * If you are looking for a basic implementation of this interface, see
 * {@link DefaultClientCredentialProvider}. If you would like to read your configuration from an
 * EdgeRc file, see {@code EdgeRcClientCredentialProvider}.
 * </p>
 *
 */
public interface ClientCredentialProvider {

    /**
     * Gets a {@link ClientCredential} that is appropriate for signing {@code request}. The result
     * of this method may be {@code null} if no reasonable {@link ClientCredential} can be located.
     *
     * @param request a Request
     * @return a {@link ClientCredential} (can be {@code null})
     * @throws NoMatchingCredentialException if no {@link ClientCredential} can be selected
     */
    public ClientCredential getClientCredential(Request request)
            throws NoMatchingCredentialException;

}
