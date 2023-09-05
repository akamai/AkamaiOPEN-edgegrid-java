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

/**
 * Exception representing errors during request signing.
 *
 * @author mgawinec@akamai.com
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
