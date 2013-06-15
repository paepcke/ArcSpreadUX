package edu.stanford.infolab.arcspreadux.photoSpread;

import junit.framework.TestCase;
import edu.stanford.infolab.arcspreadux.photoSpread.PhotoSpreadException.IllegalPreferenceException;

public class PhotoSpreadExceptionTest extends TestCase {

	
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testThrow () {

		try {
			throw new IllegalPreferenceException("Testing.");
		} catch (IllegalPreferenceException e) {
			assertEquals(
					"PhotoSpread Exception IllegalPreferenceException(\"Testing.\")",
					"PhotoSpread: Testing.",
					e.getMessage());
		}
	}
}
