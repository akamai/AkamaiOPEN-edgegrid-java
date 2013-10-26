package com.akamai.edgegrid.auth;

import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 * @author Ajay K
 * 
 */
public class DefaultCredentialSignerTest {

	@Test(expected = IllegalArgumentException.class)
	public void testDefaultCredentialWithoutClientToken() {
		DefaultCredential defaultCredential = new DefaultCredential("", "", "");

	}

	@Test(expected = IllegalArgumentException.class)
	public void testDefaultCredentialWithoutAccessToken() {
		DefaultCredential defaultCredential = new DefaultCredential(
				"ClientToken", "", "");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDefaultCredentialWithoutClientSecret() {
		DefaultCredential defaultCredential = new DefaultCredential(
				"ClientToken", "AccessToken", "");
	}

	@Test
	public void testDefaultCredential() {
		DefaultCredential defaultCredential = new DefaultCredential(
				"ClientToken", "AccessToken", "ClientSecret");

		Assert.assertEquals("ClientToken", defaultCredential.getClientToken());

		Assert.assertEquals("AccessToken", defaultCredential.getAccessToken());

		Assert.assertEquals("ClientSecret", defaultCredential.getClientSecret());

	}

}
