/*
 * Copyright 2016 Copyright 2016 Akamai Technologies, Inc. All Rights Reserved.
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

package com.akamai.testing.edgegrid.core;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * Generates Authorization header based on EdgeGrid V1 signing algorithm. Agnostic to implementation of HTTP request.
 *
 * @author mgawinec@akamai.com
 */
public class EdgeGridV1Signer {

    private static final Logger log = LoggerFactory.getLogger(EdgeGridV1Signer.class);

    /**
     * Default algorithm for signing and hashing.
     */
    public static final Algorithm DEFAULT_SIGNING_ALGORITHM = Algorithm.EG1_HMAC_SHA256;
    /**
     * Default maximum allowed body size in bytes for POST and PUT requests.
     */
    public static final int DEFAULT_MAX_BODY_SIZE_IN_BYTES = 1024 * 2;
    /**
     * Default headers used for signing.
     */
    public static final Set<String> DEFAULT_HEADERS_TO_SIGN = ImmutableSet.of();

    private static final String AUTH_CLIENT_TOKEN_NAME = "client_token";
    private static final String AUTH_ACCESS_TOKEN_NAME = "access_token";
    private static final String AUTH_TIMESTAMP_NAME = "timestamp";
    private static final String AUTH_NONCE_NAME = "nonce";
    private static final String AUTH_SIGNATURE_NAME = "signature";
    private static final String STRING2BYTES_CHARSET = "UTF-8";
    private final Algorithm algorithm;
    private final Set<String> headersToInclude;
    private final int maxBodySize;

    /**
     * Note: the parameters should be published by the service provider when the service
     * is published. Refer to the API documentation for any special instructions.
     *
     * @param algorithm   algorithm for signing and hashing
     * @param headers     the ordered list of header names to include in the signature.
     * @param maxBodySize the maximum allowed body size in bytes for POST and PUT requests.
     */
    public EdgeGridV1Signer(Algorithm algorithm, Set<String> headers, int maxBodySize) {
        this.algorithm = checkNotNull(algorithm);
        this.headersToInclude = checkNotNull(headers);
        this.maxBodySize = maxBodySize;
    }

    /**
     * Creates signer with default configuration.
     */
    public EdgeGridV1Signer() {
        this(DEFAULT_SIGNING_ALGORITHM, DEFAULT_HEADERS_TO_SIGN, DEFAULT_MAX_BODY_SIZE_IN_BYTES);
    }

    /**
     * Generates signature for a given HTTP request and client credential. To be put in Authorization header.
     *
     * @param request    a HTTP request to sign.
     * @param credential client credential used to sign a request
     * @return Authorization header value with
     * @throws RequestSigningException if signing of a given request failed.
     * @throws NullPointerException    if <code>request</code> or <code>credential</code> is <code>null</code>.
     */
    public String getAuthorizationHeaderValue(Request request, ClientCredential credential) throws RequestSigningException {
        checkNotNull(request);
        checkNotNull(credential);
        return getAuthorizationHeaderValue(request, credential, System.currentTimeMillis(), UUID.randomUUID());
    }

    String getAuthorizationHeaderValue(Request request, ClientCredential credential, long timestamp, UUID nonce) throws RequestSigningException {
        String timeStamp = formatTimeStamp(timestamp);
        String authData = getAuthData(credential, timeStamp, nonce);
        String signature = getSignature(request, credential, timeStamp, authData);
        log.debug(String.format("Signature: '%s'", signature));

        return getAuthorizationHeaderValue(authData, signature);
    }

    private static String getAuthorizationHeaderValue(String authData, String signature) {
        StringBuilder sb = new StringBuilder(authData);
        sb.append(AUTH_SIGNATURE_NAME);
        sb.append('=');
        sb.append(signature);
        return sb.toString();
    }

    private static String getRelativePathWithQuery(String uriString) {
        URI uri = URI.create(uriString);
        StringBuffer sb = new StringBuffer(uri.getPath());
        if (uri.getQuery() != null) {
            sb.append("?").append(uri.getQuery());
        }
        return sb.toString();
    }

    private static byte[] sign(String s, String key, String algorithm) throws RequestSigningException {
        try {
            return sign(s, key.getBytes(STRING2BYTES_CHARSET), algorithm);
        } catch (UnsupportedEncodingException e) {
            throw new RequestSigningException("Failed to sign: invalid string encoding", e);
        }
    }

    private static byte[] sign(String s, byte[] key, String algorithm) throws RequestSigningException {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);

            byte[] valueBytes = s.getBytes(STRING2BYTES_CHARSET);
            return mac.doFinal(valueBytes);
        } catch (NoSuchAlgorithmException nsae) {
            throw new RequestSigningException("Failed to sign: algorithm not found", nsae);
        } catch (InvalidKeyException ike) {
            throw new RequestSigningException("Failed to sign: invalid key", ike);
        } catch (UnsupportedEncodingException uee) {
            throw new RequestSigningException("Failed to sign: invalid string encoding", uee);
        }
    }

    private static String formatTimeStamp(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
        Date date = new Date(time);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    private static String canonicalizeUri(String uri) {
        if (Strings.isNullOrEmpty(uri)) {
            return "/";
        }

        if (uri.charAt(0) != '/') {
            uri = "/" + uri;
        }

        return uri;
    }

    private String getSignature(Request request, ClientCredential credential, String timeStamp, String authData) throws RequestSigningException {
        String signingKey = getSigningKey(timeStamp, credential.getClientSecret());
        String canonicalizedRequest = getCanonicalizedRequest(request);
        String stringToSign = getStringToSign(canonicalizedRequest, authData);
        log.debug(String.format("String to sign: '%s'", stringToSign));

        return signAndEncode(stringToSign, signingKey);
    }

    private String signAndEncode(String stringToSign, String signingKey) throws RequestSigningException {
        byte[] signatureBytes = sign(stringToSign, signingKey, algorithm.getSigningAlgorithm());
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    private String getSigningKey(String timeStamp, String clientSecret) throws RequestSigningException {
        byte[] signingKeyBytes = sign(timeStamp, clientSecret, algorithm.getSigningAlgorithm());
        return Base64.getEncoder().encodeToString(signingKeyBytes);
    }

    private String getStringToSign(String canonicalizedRequest, String authData) {
        StringBuilder signData = new StringBuilder(canonicalizedRequest);
        signData.append(authData);
        return signData.toString();
    }

    private String getAuthData(ClientCredential credential, String timeStamp, UUID nonce) {
        StringBuilder sb = new StringBuilder();
        sb.append(algorithm.getAlgorithm());
        sb.append(' ');
        sb.append(AUTH_CLIENT_TOKEN_NAME);
        sb.append('=');
        sb.append(credential.getClientToken());
        sb.append(';');

        sb.append(AUTH_ACCESS_TOKEN_NAME);
        sb.append('=');
        sb.append(credential.getAccessToken());
        sb.append(';');

        sb.append(AUTH_TIMESTAMP_NAME);
        sb.append('=');
        sb.append(timeStamp);
        sb.append(';');

        sb.append(AUTH_NONCE_NAME);
        sb.append('=');
        sb.append(nonce.toString());
        sb.append(';');
        return sb.toString();
    }


    private String getCanonicalizedRequest(Request request) throws RequestSigningException {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod().toUpperCase());
        sb.append('\t');

        URI uri = URI.create(request.getUriWithQuery());

        String scheme = uri.getScheme();
        sb.append(scheme.toLowerCase());
        sb.append('\t');

        String host = uri.getHost();
        sb.append(host.toLowerCase());
        sb.append('\t');


        String relativePath = getRelativePathWithQuery(request.getUriWithQuery());
        String relativeUrl = canonicalizeUri(relativePath);
        sb.append(relativeUrl);
        sb.append('\t');

        String canonicalizedHeaders = canonicalizeHeaders(request.getHeaders());
        sb.append(canonicalizedHeaders);
        sb.append('\t');

        sb.append(getContentHash(request.getMethod(), request.getBody()));
        sb.append('\t');

        return sb.toString();
    }


    private byte[] getHash(byte[] contentBytes, int offset, int len) throws RequestSigningException {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm.getMessageDigestAlgorithm());
            md.update(contentBytes, offset, len);
            byte[] digestBytes = md.digest();
            return digestBytes;
        } catch (NoSuchAlgorithmException nsae) {
            throw new RequestSigningException("Failed to get request hash: algorithm not found", nsae);
        }
    }

    private String canonicalizeHeaders(Multimap<String, String> requestHeaders) {
        StringBuilder sb = new StringBuilder();
        for (String headerName : headersToInclude) {
            // only use the first entry if more than one headers with the same name
            Optional<String> headerValue = requestHeaders.get(headerName).stream().findFirst();
            if (headerValue.isPresent()) {
                sb.append(headerName.toLowerCase());
                sb.append(':');
                sb.append(canonicalizeHeaderValue(headerValue.get()));
                sb.append('\t');
            }
        }
        return sb.toString();
    }

    private String canonicalizeHeaderValue(String headerValue) {
        headerValue = headerValue.trim();
        if (!headerValue.isEmpty()) {
            Pattern p = Pattern.compile("\\s+");
            Matcher matcher = p.matcher(headerValue);
            headerValue = matcher.replaceAll(" ");
        }
        return headerValue;
    }

    private String getContentHash(String requestMethod, String requestBody) throws RequestSigningException {
        // only do hash for POSTs for this version
        if ("POST".equals(requestMethod)) {
            return "";
        }

        if (requestBody == null) {
            return "";
        }

        byte[] contentBytes = requestBody.getBytes();
        int lengthToHash = contentBytes.length;
        if (lengthToHash > maxBodySize) {
            log.warn(String.format("Message body length '%d' is larger than the max '%d'. " +
                    "Using first '%d' bytes for computing the hash.", lengthToHash, maxBodySize, maxBodySize));
            lengthToHash = maxBodySize;
        } else {
            log.debug(String.format("Content: %s", Base64.getEncoder().encodeToString(contentBytes)));
        }

        byte[] digestBytes = getHash(contentBytes, 0, lengthToHash);
        log.debug(String.format("Content hash: %s", Base64.getEncoder().encodeToString(digestBytes)));

        // (mgawinec) I removed support for non-retryable content, that used to reset the content for downstream handlers
        return Base64.getEncoder().encodeToString(digestBytes);
    }

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

    /**
     * HTTP Request agnostic to any HTTP client implementation.
     */
    public static class Request {

        private final String method;
        private final String uriWithQuery;
        private final Multimap<String, String> headers;
        private final String body;

        /**
         * Returns a new builder. The returned builder is equivalent to the builder
         * generated by {@link Builder}.
         */
        public static Builder builder() {
            return new Builder();
        }

        private Request(Builder b) {
            this.method = checkNotNull(b.method);
            this.uriWithQuery = checkNotNull(b.uriWithQuery);
            this.headers = b.headers;
            this.body = checkNotNull(b.body);
        }

        String getBody() {
            return body;
        }

        Multimap<String, String> getHeaders() {
            return headers;
        }

        String getMethod() {
            return method;
        }

        String getUriWithQuery() {
            return uriWithQuery;
        }

        public String toString() {
            return "Request(method=" + this.getMethod() + ", uriWithQuery=" + this.getUriWithQuery() + ", body=" + this.getBody() + ", headers=" + this.getHeaders() + ")";
        }

        /**
         * Creates a new builder. The returned builder is equivalent to the builder
         * generated by {@link Request#builder()}.
         */
        public static class Builder {

            private String method;
            private String uriWithQuery;
            private Multimap<String, String> headers = ImmutableMultimap.of();
            private String body = "";

            /**
             * Sets a content of HTTP request body. If not set, body is empty by default.
             */
            public Builder body(String requestBody) {
                this.body = requestBody;
                return this;
            }

            /**
             * Sets headers of HTTP request. If not set, headers list is empty by default
             */
            public Builder headers(Multimap<String, String> headers) {
                this.headers = ImmutableMultimap.copyOf(checkNotNull(headers));
                return this;
            }

            /**
             * Sets HTTP method: GET, PUT, POST, DELETE. Mandatory to set.
             */
            public Builder method(String method) {
                this.method = checkNotNull(method);
                return this;
            }

            /**
             * Sets absolute URI of HTTP request including query string. Mandatory to set.
             */
            public Builder uriWithQuery(String uriWithQuery) {
                this.uriWithQuery = checkNotNull(uriWithQuery);
                return this;
            }

            /**
             * Returns a newly-created immutable HTTP request.
             */
            public Request build() {
                return new Request(this);
            }

        }
    }
}