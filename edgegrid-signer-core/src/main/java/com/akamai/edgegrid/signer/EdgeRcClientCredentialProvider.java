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

package com.akamai.edgegrid.signer;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.configuration2.INIConfiguration;
import org.apache.commons.configuration2.SubnodeConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.akamai.edgegrid.signer.ClientCredential.ClientCredentialBuilder;

/**
 * This is a {@link ClientCredentialProvider} implementation that reads from EdgeRc files. A variety
 * static methods are available to initially create the object. A default section name is required
 * at creation time, and sub-classes may override {@link #pickSectionName(Request)} in order to make
 * a more complicated decision on a per-request basis.
 *
 * @author mmeyer
 */
public class EdgeRcClientCredentialProvider implements ClientCredentialProvider {

    /** This is an {@link INIConfiguration} that will hold the EdgeRc configuration. */
    private final INIConfiguration configuration;

    /**
     * This is the default section name that will be returned by {@link #pickSectionName(Request)}
     * unless it is overridden by a subclass.
     */
    private final String defaultSectionName;

    /**
     * Loads an EdgeRc configuration file and returns an {@link EdgeRcClientCredentialProvider} to
     * read {@link ClientCredential}s from it.
     *
     * @param file a {@link File} pointing to an EdgeRc file
     * @param section a config section ({@code null} for the default section)
     * @return a {@link EdgeRcClientCredentialProvider}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static EdgeRcClientCredentialProvider fromEdgeRc(File file, String section)
            throws ConfigurationException, IOException {
        Validate.notNull(file, "file cannot be null");
        return fromEdgeRc(new FileReader(file), section);
    }

    /**
     * Loads an EdgeRc configuration file and returns an {@link EdgeRcClientCredentialProvider} to
     * read {@link ClientCredential}s from it.
     *
     * @param inputStream an open {@link InputStream} to an EdgeRc file
     * @param section a config section ({@code null} for the default section)
     * @return a {@link EdgeRcClientCredentialProvider}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static EdgeRcClientCredentialProvider fromEdgeRc(InputStream inputStream, String section)
            throws ConfigurationException, IOException {
        Validate.notNull(inputStream, "inputStream cannot be null");
        return fromEdgeRc(new InputStreamReader(inputStream), section);
    }

    /**
     * Loads an EdgeRc configuration file and returns an {@link EdgeRcClientCredentialProvider} to
     * read {@link ClientCredential}s from it.
     *
     * @param reader an open {@link Reader} to an EdgeRc file
     * @param section a config section ({@code null} for the default section)
     * @return a {@link EdgeRcClientCredentialProvider}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static EdgeRcClientCredentialProvider fromEdgeRc(Reader reader, String section)
            throws ConfigurationException, IOException {
        Validate.notNull(reader, "reader cannot be null");
        return new EdgeRcClientCredentialProvider(reader, section);
    }

    /**
     * Loads an EdgeRc configuration file and returns an {@link EdgeRcClientCredentialProvider} to
     * read {@link ClientCredential}s from it.
     *
     * @param filename a filename pointing to an EdgeRc file
     * @param section a config section ({@code null} for the default section)
     * @return a {@link EdgeRcClientCredentialProvider}
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public static EdgeRcClientCredentialProvider fromEdgeRc(String filename, String section)
            throws ConfigurationException, IOException {
        Validate.notEmpty(filename, "filename cannot be null");
        filename = filename.replaceFirst("^~", System.getProperty("user.home"));
        File file = new File(filename);
        return fromEdgeRc(new FileReader(file), section);
    }

    /**
     * Loads an EdgeRc configuration file and returns an {@link EdgeRcClientCredentialProvider} to
     * read {@link ClientCredential}s from it.
     *
     * @param reader an open {@link Reader} to an EdgeRc file
     * @param section a config section ({@code null} for the default section)
     * @throws ConfigurationException If an error occurs while reading the configuration
     * @throws IOException if an I/O error occurs
     */
    public EdgeRcClientCredentialProvider(Reader reader, String section)
            throws ConfigurationException, IOException {
        Validate.notNull(reader, "reader cannot be null");
        configuration = new INIConfiguration();
        configuration.read(reader);
        this.defaultSectionName = section;
    }

    @Override
    public ClientCredential getClientCredential(Request request) {
        String sectionName = pickSectionName(request);
        SubnodeConfiguration s = configuration.getSection(sectionName);
        ClientCredentialBuilder builder = ClientCredential.builder()
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
     * Picks an appropriate section name from the configuration to sign {@code request}. By default
     * this method will always return {@link #defaultSectionName}, which was provided as part the
     * object construction. Users may extend this class and override this method to
     * perform more complex decisions about how to decide which {@link ClientCredential} will be
     * retrieved from the EdgeRc file.
     *
     * @param request a {@link Request}
     * @return a section name ({@code null} for the default section)
     */
    protected String pickSectionName(Request request) {
        return defaultSectionName;
    }

}
