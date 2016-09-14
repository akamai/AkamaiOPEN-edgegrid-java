package com.akamai.edgegrid.signer;

/**
 * Signing and hashing algorithms.
 */
public enum Algorithm {

    EG1_HMAC_SHA256("EG1-HMAC-SHA256", "HmacSHA256", "SHA-256");

    private final String algorithm, signingAlgorithm, messageDigestAlgorithm;

    Algorithm(String algorithm, String signingAlgorithm, String messageDigestAlgorithm) {
        this.algorithm = algorithm;
        this.signingAlgorithm = signingAlgorithm;
        this.messageDigestAlgorithm = messageDigestAlgorithm;
    }

    String getAlgorithm() {
        return algorithm;
    }

    String getMessageDigestAlgorithm() {
        return messageDigestAlgorithm;
    }

    String getSigningAlgorithm() {
        return signingAlgorithm;
    }
}
