package edu.stanford.infolab.arcspreadux.hadoop;

import java.io.FileNotFoundException;

import junit.framework.TestCase;
import edu.stanford.arcspreadux.hadoop.PigScriptReader;

public class testPigScriptReader extends TestCase {

	PigScriptReader reader = null;

	public void testOneCommentLine() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest1.pig");
		assertTrue(reader.hasNext());
		String singleComment = reader.next();
		assertEquals("", singleComment);
	}
	
	public void testOneCodeLine() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest2.pig");
		assertTrue(reader.hasNext());
		String singleCodeLine= reader.next();
		assertEquals("lines = LOAD 'foo.txt' USING PigStorage() AS(line:chararray);", singleCodeLine);
		assertFalse(reader.hasNext());
	}
	
	public void testCodeAndCommentsMixed() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest3.pig");
		assertTrue(reader.hasNext());
		String singleCodeLine= reader.next();
		assertEquals("lines = LOAD 'foo.txt' USING PigStorage() AS(line:chararray);", singleCodeLine.trim());
		assertFalse(reader.hasNext());
	}
	
	public void testMultilineCode() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest4.pig");
		assertTrue(reader.hasNext());
		String singleCodeLine= reader.next();
		assertEquals("lines = LOAD 'foo.txt' USING PigStorage() AS(line:chararray);", singleCodeLine.trim());
		assertFalse(reader.hasNext());
	}
	
	public void testBlockCommentOnly() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest5.pig");
		assertTrue(reader.hasNext());
		String singleCodeLine= reader.next();
		assertEquals("", singleCodeLine);
		assertFalse(reader.hasNext());
	}

	public void testBlockCommentPlusCodeDiffLine() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest6.pig");
		assertTrue(reader.hasNext());
		String singleCodeLine = reader.next();
		assertEquals("STORE foo;", singleCodeLine);
		assertFalse(reader.hasNext());
	}

	public void testBlockCommentPlusCodeSameLine() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest7.pig");
		assertTrue(reader.hasNext());
		String singleCodeLine = reader.next();
		assertEquals("STORE foo;", singleCodeLine);
		assertFalse(reader.hasNext());
	}

	public void testBlockCommentPlusCodeSameLineReversed() throws FileNotFoundException {
		reader = new PigScriptReader("src/test/resources/pigReadTest8.pig");
		assertTrue(reader.hasNext());
		String singleCodeLine = reader.next();
		assertEquals("STORE foo;", singleCodeLine);
		assertFalse(reader.hasNext());
	}

}
