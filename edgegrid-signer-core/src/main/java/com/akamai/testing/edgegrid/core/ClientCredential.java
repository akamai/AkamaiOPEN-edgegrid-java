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

import java.util.Collections;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.builder.Builder;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * Client credential used to sign a request.
 *
 * @author mgawinec@akamai.com
 * @author mmeyer@akamai.com
 */
public class ClientCredential implements Comparator<ClientCredential>, Comparable<ClientCredential> {

    /** This is the default {@code maxBodySize} to apply if not explicitly set in a credential. */
    public static final int DEFAULT_MAX_BODY_SIZE = 131072;

    /**
     * Loads a {@link ClientCredential} from {@code file}. The {@link File} should contain INI-style
     * EdgeRc content.
     *
     * @param file a {@link File} pointing to an EdgeRc file
     * @param section a config section (null for the default section)
     * @return a {@link ClientCredential}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static ClientCredential fromEdgeRc(File file, String section)
            throws ConfigurationException, IOException {
        return fromEdgeRc(new FileReader(file), section);
    }

    /**
     * Loads a {@link ClientCredential} from {@code inputStream}. The {@link InputStream} should
     * contain INI-style EdgeRc content.
     *
     * @param inputStream an open {@link InputStream} to an EdgeRc file
     * @param section a config section (null for the default section)
     * @return a {@link ClientCredential}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static ClientCredential fromEdgeRc(InputStream inputStream, String section)
            throws ConfigurationException, IOException {
        return fromEdgeRc(new InputStreamReader(inputStream), section);
    }

    /**
     * Loads a {@link ClientCredential} from {@code reader}. The {@link Reader} should contain
     * INI-style EdgeRc content.
     *
     * @param reader an open {@link Reader} to an EdgeRc file
     * @param section a config section (null for the default section)
     * @return a {@link ClientCredential}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static ClientCredential fromEdgeRc(Reader reader, String section)
            throws ConfigurationException, IOException {
        INIConfiguration config = new INIConfiguration();
        config.read(reader);
        SubnodeConfiguration s = config.getSection(section);
        ClientCredentialBuilder builder = builder()
                .accessToken(s.getString("access_token"))
                .clientSecret(s.getString("client_secret"))
                .clientToken(s.getString("client_token"))
                .host(s.getString("host"));
        if (s.getInteger("max-body", null) != null) {
            builder.maxBodySize(s.getInteger("max-body", null));
        }
        String headersString = s.getString("headers_to_sign");
        if (StringUtils.isNotBlank(headersString)) {
            for (String h : headersString.split(",")) {
                builder.headerToSign(h);
            }
        }
        return builder.build();
    }

    /**
     * Loads a {@link ClientCredential} from {@code filename}. The file should contain INI-style
     * EdgeRc content. Note that this method intelligently replaces "~" with a reference to the
     * JVM's user home directory ({@code System.getProperty("user.home")}).
     *
     * @param filename a filename pointing to an EdgeRc file
     * @param section a config section (null for the default section)
     * @return a {@link ClientCredential}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static ClientCredential fromEdgeRc(String filename, String section)
            throws ConfigurationException, IOException {
        filename = filename.replaceFirst("^~", System.getProperty("user.home"));
        File file = new File(filename);
        return fromEdgeRc(new FileReader(file), section);
    }

    private String accessToken;
    private String clientSecret;
    private String clientToken;
    private Set<String> headersToSign;
    private String host;
    private Integer maxBodySize;

    ClientCredential(ClientCredentialBuilder b) {
        this.accessToken = b.accessToken;
        this.clientSecret = b.clientSecret;
        this.clientToken = b.clientToken;
        this.headersToSign = b.headersToSign;
        this.host = b.host;
        this.maxBodySize = b.maxBodySize;
    }

    /**
     * Returns a new builder. The returned builder is equivalent to the builder
     * generated by {@link ClientCredentialBuilder}.
     *
     * @return a fresh {@link ClientCredentialBuilder}
     */
    public static ClientCredentialBuilder builder() {
        return new ClientCredentialBuilder();
    }

    @Override
    public int compare(ClientCredential o1, ClientCredential o2) {
        return new CompareToBuilder()
                .append(o1.accessToken, o2.accessToken)
                .append(o1.clientSecret, o2.clientSecret)
                .append(o1.clientToken, o2.clientToken)
                .append(o1.host, o2.host)
                .append(o1.maxBodySize, o2.maxBodySize)
                .build();
    }

    @Override
    public int compareTo(ClientCredential that) {
        return compare(this, that);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (getClass() != o.getClass()) return false;
        final ClientCredential that = (ClientCredential) o;
        return compareTo(that) == 0;
    }

    String getAccessToken() {
        return accessToken;
    }

    String getClientSecret() {
        return clientSecret;
    }

    String getClientToken() {
        return clientToken;
    }

    int getMaxBodySize() {
        if (maxBodySize == null) {
            return DEFAULT_MAX_BODY_SIZE;
        }
        return maxBodySize;
    }

    Set<String> getHeadersToSign() {
        if (headersToSign == null) {
            return Collections.emptySet();
        }
        return headersToSign;
    }

    String getHost() {
        return host;
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, clientSecret, clientToken, headersToSign, host, maxBodySize);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.JSON_STYLE)
                .append("accessToken", accessToken)
                .append("clientSecret", clientSecret)
                .append("clientToken", clientToken)
                .append("headersToSign", headersToSign)
                .append("host", host)
                .append("maxBodySize", getMaxBodySize()) // note: intentionally using accessor here.
                .build();
    }

    public static class ClientCredentialBuilder implements Builder<ClientCredential> {
        private String accessToken;
        private String clientSecret;
        private String clientToken;
        private Set<String> headersToSign;
        private String host;
        private Integer maxBodySize;

        /**
         * Creates a new builder. The returned builder is equivalent to the builder
         * generated by {@link ClientCredential#builder}.
         */
        public ClientCredentialBuilder() {
        }

        /**
         * Sets a token representing an OPEN API service client.
         *
         * @param clientToken a client token
         * @return reference back to this builder instance
         */
        public ClientCredentialBuilder clientToken(String clientToken) {
            Validate.notBlank(clientToken, "clientToken cannot be blank");
            this.clientToken = clientToken;
            return this;
        }


        /**
         * Sets a secret associated with a client token.
         *
         * @param clientSecret a client secret
         * @return reference back to this builder instance
         */
        public ClientCredentialBuilder clientSecret(String clientSecret) {
            Validate.notBlank(clientSecret, "clientSecret cannot be blank");
            this.clientSecret = clientSecret;
            return this;
        }

        /**
         * Sets an access token representing authorizations a client has for OPEN API service.
         *
         * @param accessToken an access token
         * @return reference back to this builder instance
         */
        public ClientCredentialBuilder accessToken(String accessToken) {
            Validate.notBlank(accessToken, "accessToken cannot be blank");
            this.accessToken = accessToken;
            return this;
        }

        /**
         * Adds all of {@code headersToSign} into the builder's internal collection. This can be
         * called multiple times to continue adding them. The set passed in is not stored directly,
         * a copy is made instead.
         *
         * @param headersToSign a {@link Set} of header names
         * @return reference back to this builder instance
         */
        public ClientCredentialBuilder headersToSign(Set<String> headersToSign) {
            for (String headerName : headersToSign) {
                headerToSign(headerName);
            }
            return this;
        }

        /**
         * Adds {@code headerName} into the builder's internal collection. This can be called
         * multiple times to continue adding them.
         *
         * @param headerName a header name
         * @return reference back to this builder instance
         */
        public ClientCredentialBuilder headerToSign(String headerName) {
            Validate.notBlank(headerName, "headerName cannot be blank");
            if (this.headersToSign == null) {
                this.headersToSign = new HashSet<>();
            }

            this.headersToSign.add(headerName);
            return this;
        }

        /**
         * Sets a hostname to be used when making OPEN API requests with this credential.
         *
         * @param host a host name
         * @return reference back to this builder instance
         */
        public ClientCredentialBuilder host(String host) {
            Validate.notBlank(host, "host cannot be blank");
            this.host = host;
            return this;
        }

        /**
         * Sets the maximum body size that will be used for producing request signatures.
         *
         * @param maxBodySize a number of bytes
         * @return reference back to this builder instance
         */
        public ClientCredentialBuilder maxBodySize(int maxBodySize) {
            this.maxBodySize = maxBodySize;
            return this;
        }

        /**
         * Returns a newly-created immutable client credential.
         *
         * @return reference back to this builder instance
         */
        @Override
        public ClientCredential build() {
            Validate.notBlank(accessToken, "accessToken cannot be blank");
            Validate.notBlank(clientSecret, "clientSecret cannot be blank");
            Validate.notBlank(clientToken, "clientToken cannot be blank");
            Validate.notBlank(host, "host cannot be blank");
            return new ClientCredential(this);
        }

    }

}