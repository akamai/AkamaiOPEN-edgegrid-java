package com.akamai.edgegrid.signer.exceptions;

/**
 * Exception representing errors during request signing.
 *
 */
public class RequestSigningException extends Exception {

    private static final long serialVersionUID = -4716437270940718895L;

    /**
     * Creates a default {@link RequestSigningException} .
     *
     */
    public RequestSigningException() {
        super();
    }

    /**
     * Creates a {@link RequestSigningException} using message.
     *
     * @param message exception message
     */
    public RequestSigningException(String message) {
        super(message);
    }

    /**
     * Creates a {@link RequestSigningException} using {@link Throwable}.
     *
     * @param t a {@link Throwable}
     */
    public RequestSigningException(Throwable t) {
        super(t);
    }

    /**
     * Creates a {@link RequestSigningException} using {@link Throwable} and message.
     *
     * @param message exception message
     * @param t a {@link Throwable}
     */
    public RequestSigningException(String message, Throwable t) {
        super(message, t);
    }
}
