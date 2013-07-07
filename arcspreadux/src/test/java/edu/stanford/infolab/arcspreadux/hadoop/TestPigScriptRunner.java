package edu.stanford.infolab.arcspreadux.hadoop;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.pig.data.Tuple;

import edu.stanford.arcspreadux.hadoop.PigScriptRunner;

public class TestPigScriptRunner extends TestCase {
	
	public void testSimpleScript() throws IOException {
		
		File scriptFile = new File("src/test/resources/pigtest.pig");
		//****PigScriptRunner runner = new PigScriptRunner(scriptFile, "theCount");
		PigScriptRunner runner = new PigScriptRunner(scriptFile, "/home/paepcke/tmp/maryTest.txt", "theCount");
		runner.store();
		//*****runner.run();
/*		Iterator<Tuple> resultIt = runner.iterator();
		while (resultIt.hasNext()) {
			System.out.println(resultIt.next());
		}
*/	}

}
