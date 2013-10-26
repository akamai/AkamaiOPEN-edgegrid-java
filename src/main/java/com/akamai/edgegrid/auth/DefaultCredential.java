/*
 * Copyright 2013 Akamai Technologies, Inc. All Rights Reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.akamai.edgegrid.auth;

/**
 * Default implementation of the {@link ClientCredential}.
 *
 */
public class DefaultCredential implements ClientCredential {

	/**
	 * The client token.
	 */
	private final String clientToken;
	
	/**
	 * The access token.
	 */
	private final String accessToken;
	
	/**
	 * The secret associated with the client token.
	 */
	private final String clientSecret;
	
	/**
	 * Constructor.
	 * 
	 * @param clientToken the client token, cannot be null or empty.
	 * @param accessToken the access token, cannot be null or empty.
	 * @param clientSecret the client secret, cannot be null or empty.
	 * 
	 * @throws IllegalArgumentException if any of the parameters is null or empty.
	 */
	public DefaultCredential(String clientToken, String accessToken, String clientSecret) {
		if (Utils.isNullOrEmpty(clientToken)) {
			throw new IllegalArgumentException("clientToken cannot be empty.");
		}
		if (Utils.isNullOrEmpty(accessToken)) {
			throw new IllegalArgumentException("accessToken cannot be empty.");
		}
		if (Utils.isNullOrEmpty(clientSecret)) {
			throw new IllegalArgumentException("clientSecret cannot be empty.");
		}
		
		this.clientToken = clientToken;
		this.accessToken = accessToken;
		this.clientSecret = clientSecret;
	}
	
	/**
	 * Gets the client token.
	 * @return The client token.
	 */
	public String getClientToken() {
		return clientToken;
	}

	/**
	 * Gets the access token.
	 * @return the access token.
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * Gets the secret associated with the client token.
	 * @return the secret.
	 */
	public String getClientSecret() {
		return clientSecret;
	}

}
