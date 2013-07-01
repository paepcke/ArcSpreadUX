/**
 * 
 */
package edu.stanford.arcspreadux.hadoop;

/**
 * @author paepcke
 *
 */

import java.io.File;
import java.net.URI;
import java.util.Properties;

import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;

import edu.stanford.pigir.Common;


public class PigScriptRunner {

	private static PigServer pserver;
	Properties props = new Properties();
	File scriptFile  = null;
	File outFile     = null;
	String pigVar    = null;

	public PigScriptRunner(File theScriptFile, String theVarToPrintOrIterate) {
		scriptFile = theScriptFile;
		pigVar = theVarToPrintOrIterate;
		initPig();
	}
	
	public PigScriptRunner(File theScriptFile, File theOutfile, String varToStore) {
		scriptFile = theScriptFile;
		outFile = theOutfile;
		pigVar  = varToStore;
		initPig();
	}
	
	public void addScriptCallParam(String paramName, String paramValue) {
		props.setProperty(paramName, paramValue);
	}
	
	public void run() {
		startServer();
		// The following needs to be an in stream:
		pserver.registerScript(scriptFile);
	}
	
	private void initPig() {
		props.setProperty("pig.usenewlogicalplan", "false");
		props.setProperty("pig.temp.dir","/user/paepcke");
		props.setProperty("PIG_HOME",System.getenv("PIG_HOME"));
		props.setProperty("USER_CONTRIB", System.getenv("PIG_USER_JAR_DIR"));
		props.setProperty("PIGIR_HOME", System.getenv("PIGIR_HOME"));
	}
	
	private void startServer() {
		try {
			//pserver = new PigServer(ExecType.MAPREDUCE, props);
			pserver = new PigServer(ExecType.LOCAL, props);
		} catch (ExecException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
