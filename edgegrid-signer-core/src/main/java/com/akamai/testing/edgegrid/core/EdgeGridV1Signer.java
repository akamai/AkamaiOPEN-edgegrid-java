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

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Generates Authorization header based on <a href="https://developer.akamai.com/introduction/Client_Auth.html">EdgeGrid
 * V1 signing algorithm</a>. Agnostic to implementation of HTTP request.
 *
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public class EdgeGridV1Signer {

    /**
     * Default algorithm for signing and hashing.
     */
    public static final Algorithm DEFAULT_SIGNING_ALGORITHM = Algorithm.EG1_HMAC_SHA256;

    private static final Logger log = LoggerFactory.getLogger(EdgeGridV1Signer.class);
    private static final String AUTH_CLIENT_TOKEN_NAME = "client_token";
    private static final String AUTH_ACCESS_TOKEN_NAME = "access_token";
    private static final String AUTH_TIMESTAMP_NAME = "timestamp";
    private static final String AUTH_NONCE_NAME = "nonce";
    private static final String AUTH_SIGNATURE_NAME = "signature";
    private final Algorithm algorithm;
    private final Set<String> headersToSign;
    private final Base64.Encoder base64 = Base64.getEncoder();

    /**
     * Creates signer with default configuration.
     */
    public EdgeGridV1Signer() {
        this(DEFAULT_SIGNING_ALGORITHM, Collections.<String>emptySet());
    }

    /**
     * Creates EdgeGrid signer with a custom configuration.
     *
     * Configuration parameters should be published by the service provider when the service
     * is published. Refer to the API documentation for any special instructions.
     *
     * @param algorithm   algorithm for signing and hashing
     * @param headersToSign the set of header names to include in the signature
     *
     * @see <a href="https://developer.akamai.com/">OPEN API documentation</a>
     */
    public EdgeGridV1Signer(Algorithm algorithm, Set<String> headersToSign) {
        Validate.notNull(algorithm, "algorithm cannot be null");
        this.algorithm = algorithm;
        if (headersToSign == null) {
            this.headersToSign = Collections.emptySet();
        } else {
            this.headersToSign = Collections.unmodifiableSet(headersToSign);
        }
    }

    /**
     * Generates signature for a given HTTP request and client credential. To be put in Authorization header.
     *
     * @param request    a HTTP request to sign.
     * @param credential client credential used to sign a request
     * @return Authorization header value with a request signature
     * @throws RequestSigningException if signing of a given request failed
     * @throws NullPointerException    if <code>request</code> or <code>credential</code> is <code>null</code>
     * @throws IllegalArgumentException if request contains multiple request headers with the same header name
     */
    public String getAuthorizationHeaderValue(Request request, ClientCredential credential) throws RequestSigningException {
        return getAuthorizationHeaderValue(request, credential, System.currentTimeMillis(), UUID.randomUUID());
    }

    private boolean containsDuplicateHeaderNames(Request request) {
        for (Map.Entry<String, List<String>> entry : request.getHeaders().entrySet()) {
            if (entry.getValue().size() > 1) {
                return true;
            }
        }
        return false;
    }

    private static String getAuthorizationHeaderValue(String authData, String signature) {
        return authData + AUTH_SIGNATURE_NAME + '=' + signature;
    }

    private static String getRelativePathWithQuery(URI uri) {
        StringBuilder sb = new StringBuilder(uri.getPath());
        if (uri.getQuery() != null) {
            sb.append("?").append(uri.getQuery());
        }
        return sb.toString();
    }

    private static byte[] sign(String s, String clientSecret, String algorithm) throws RequestSigningException {
        return sign(s, clientSecret.getBytes(StandardCharsets.UTF_8), algorithm);
    }

    private static byte[] sign(String s, byte[] key, String algorithm) throws RequestSigningException {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key, algorithm);
            Mac mac = Mac.getInstance(algorithm);
            mac.init(signingKey);

            byte[] valueBytes = s.getBytes(StandardCharsets.UTF_8);
            return mac.doFinal(valueBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RequestSigningException("Failed to sign: your JDK does not recognize signing algorithm <" + algorithm +">", e);
        } catch (InvalidKeyException e) {
            throw new RequestSigningException("Failed to sign: invalid key", e);
        }
    }

    private static String formatTimeStamp(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd'T'HH:mm:ssZ");
        Date date = new Date(time);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date);
    }

    private static String canonicalizeUri(String uri) {
        if (StringUtils.isEmpty(uri)) {
            return "/";
        }

        if (uri.charAt(0) != '/') {
            uri = "/" + uri;
        }

        return uri;
    }

    String getAuthorizationHeaderValue(Request request, ClientCredential credential, long timestamp, UUID nonce) throws RequestSigningException {
        Validate.notNull(credential, "credential cannot be null");
        Validate.notNull(request, "request cannot be null");
        Validate.isTrue(!containsDuplicateHeaderNames(request), "The protocol does not support multiple request headers with the same header name");

        String timeStamp = formatTimeStamp(timestamp);
        String authData = getAuthData(credential, timeStamp, nonce);
        String signature = getSignature(request, credential, timeStamp, authData);
        log.debug(String.format("Signature: '%s'", signature));

        return getAuthorizationHeaderValue(authData, signature);
    }

    private String getSignature(Request request, ClientCredential credential, String timeStamp, String authData) throws RequestSigningException {
        String signingKey = getSigningKey(timeStamp, credential.getClientSecret());
        String canonicalizedRequest = getCanonicalizedRequest(request, credential);
        String stringToSign = getStringToSign(canonicalizedRequest, authData);
        log.debug(String.format("String to sign: '%s'", stringToSign));

        return signAndEncode(stringToSign, signingKey);
    }

    private String signAndEncode(String stringToSign, String signingKey) throws RequestSigningException {
        byte[] signatureBytes = sign(stringToSign, signingKey, algorithm.getSigningAlgorithm());
        return base64.encodeToString(signatureBytes);
    }

    private String getSigningKey(String timeStamp, String clientSecret) throws RequestSigningException {
        byte[] signingKeyBytes = sign(timeStamp, clientSecret, algorithm.getSigningAlgorithm());
        return base64.encodeToString(signingKeyBytes);
    }

    private String getStringToSign(String canonicalizedRequest, String authData) {
        return canonicalizedRequest + authData;
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


    private String getCanonicalizedRequest(Request request, ClientCredential credential) throws RequestSigningException {
        StringBuilder sb = new StringBuilder();
        sb.append(request.getMethod().toUpperCase());
        sb.append('\t');

        String scheme = request.getUriWithQuery().getScheme();
        sb.append(scheme.toLowerCase());
        sb.append('\t');

        String host = credential.getHost();
        sb.append(host.toLowerCase());
        sb.append('\t');

        String relativePath = getRelativePathWithQuery(request.getUriWithQuery());
        String relativeUrl = canonicalizeUri(relativePath);
        sb.append(relativeUrl);
        sb.append('\t');

        String canonicalizedHeaders = canonicalizeHeaders(request.getHeaders());
        sb.append(canonicalizedHeaders);
        sb.append('\t');

        sb.append(getContentHash(request.getMethod(), request.getBody(), credential.getMaxBodySize()));
        sb.append('\t');

        return sb.toString();
    }


    private byte[] getHash(byte[] requestBody, int offset, int len) throws RequestSigningException {
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm.getMessageDigestAlgorithm());
            md.update(requestBody, offset, len);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RequestSigningException("Failed to get request hash: your JDK does not recognize algorithm <" + algorithm.getMessageDigestAlgorithm() +">", e);
        }
    }

    private String canonicalizeHeaders(Map<String, List<String>> requestHeaders) {
        StringBuilder sb = new StringBuilder();
        for (String headerName : headersToSign) {
            // we validated before that request contains no multiple headers with same header name
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

    private String getContentHash(String requestMethod, byte[] requestBody, int maxBodySize)
            throws RequestSigningException {
        // only do hash for POSTs for this version
        if (!"POST".equals(requestMethod)) {
            return "";
        }

        if (requestBody == null) {
            return "";
        }

        int lengthToHash = requestBody.length;
        if (lengthToHash > maxBodySize) {
            log.info(String.format("Content length '%d' is larger than the max '%d'. " +
                    "Using first '%d' bytes for computing the hash.", lengthToHash, maxBodySize, maxBodySize));
            lengthToHash = maxBodySize;
        } else {
            log.debug(String.format("Content (Base64): %s", base64.encodeToString(requestBody)));
        }

        byte[] digestBytes = getHash(requestBody, 0, lengthToHash);
        log.debug(String.format("Content hash (Base64): %s", base64.encodeToString(digestBytes)));

        // (mgawinec) I removed support for non-retryable content, that used to reset the content for downstream handlers
        return base64.encodeToString(digestBytes);
    }

}