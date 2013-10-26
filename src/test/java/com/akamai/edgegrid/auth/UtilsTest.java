package com.akamai.edgegrid.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * 
 * @author Ajay K
 *
 */
public class UtilsTest {

	@Test
	public void testIsNullOrEmpty() {
		String empty = "";
		String nullstring = null;
		String notEmpty = "NotEmpty";

		assertTrue(Utils.isNullOrEmpty(empty));
		assertFalse(Utils.isNullOrEmpty(notEmpty));
		assertTrue(Utils.isNullOrEmpty(nullstring));

	}

}
