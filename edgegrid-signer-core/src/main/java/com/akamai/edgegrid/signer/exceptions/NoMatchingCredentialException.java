package com.akamai.edgegrid.signer.exceptions;

import com.akamai.edgegrid.signer.ClientCredential;

/**
 * Exception representing failure to obtain a {@link ClientCredential} in order to sign a request.
 *
 */
public class NoMatchingCredentialException extends RequestSigningException {

    private static final String MESSAGE = "No ClientCredential found for request";

    private static final long serialVersionUID = -6663545494847315492L;

    /**
     * Creates a {@link NoMatchingCredentialException} with default message.
     *
     */
    public NoMatchingCredentialException() {
        super(MESSAGE);
    }

    /**
     * Creates a {@link NoMatchingCredentialException} using {@link Exception}.
     *
     * @param e a {@link Exception}
     */
    public NoMatchingCredentialException(Exception e) {
        super(MESSAGE, e);
    }
}
