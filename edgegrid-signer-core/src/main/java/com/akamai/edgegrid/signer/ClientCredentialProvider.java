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

/**
 * <p>
 * This interface provides a mechanism to select a {@link ClientCredential}. Implementations of
 * {@link AbstractEdgeGridRequestSigner} will call {@link #getClientCredential(Request)} during the
 * request signing phase to select the {@link ClientCredential} to be used.
 * </p>
 * <p>
 * If you are looking for a basic implementation of this interface, see
 * {@link DefaultClientCredentialProvider}. If you would like to read your configuration from an
 * EdgeRc file, see {@link EdgeRcClientCredentialProvider}.
 * </p>
 *
 * @author mmeyer@akamai.com
 */
public interface ClientCredentialProvider {

    /**
     * Gets a {@link ClientCredential} that is appropriate for signing {@code request}. The result
     * of this method may be {@code null} if no reasonable {@link ClientCredential} can be located.
     *
     * @param request a Request
     * @return a {@link ClientCredential} (can be {@code null})
     */
    ClientCredential getClientCredential(Request request);

}
