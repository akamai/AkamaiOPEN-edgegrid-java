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

import com.google.api.client.http.HttpRequest;

/**
 * Interface describing a request signer that signs service requests.
 *
 */
public interface RequestSigner {
	
	/**
	 * Signs a request with the client credential.
	 * 
	 * @param request the request to sign.
	 * @param credential the credential used in the signing.
	 * @return the signed request.
	 * @throws RequestSigningException
	 */
	public HttpRequest sign(HttpRequest request, ClientCredential credential) throws RequestSigningException;
}
