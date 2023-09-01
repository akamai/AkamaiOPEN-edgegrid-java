/*
 * Copyright 2018 Akamai Technologies, Inc. All Rights Reserved.
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

package com.akamai.edgegrid.signer.exceptions;

import com.akamai.edgegrid.signer.ClientCredential;

/**
 * Exception representing failure to obtain a {@link ClientCredential} in order to sign a request.
 *
 * @author mmeyer@akamai.com
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
