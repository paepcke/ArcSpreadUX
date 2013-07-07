/**
 * 
 */
package edu.stanford.arcspreadux.hadoop;

/**
 * @author paepcke
 *
 */

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

import org.apache.pig.ExecType;
import org.apache.pig.PigServer;
import org.apache.pig.backend.executionengine.ExecException;
import org.apache.pig.data.Tuple;


public class PigScriptRunner {

	private static PigServer pserver;
	Properties props = new Properties();
	File scriptFile  = null;
	String outFilePath = null;
	String pigVar    = null;
	InputStream scriptInStream = null;

	public PigScriptRunner(File theScriptFile, String theVarToPrintOrIterate) throws FileNotFoundException {
		if (theScriptFile == null) {
			throw new IllegalArgumentException("Script file must be provided; null was passed instead.");
		}
		
		if (theVarToPrintOrIterate == null) {
			throw new IllegalArgumentException("Script variable to print or iterate over must be provided; null was passed instead.");
		}
		
		scriptFile = theScriptFile;
		scriptInStream = new FileInputStream(theScriptFile);		
		pigVar = theVarToPrintOrIterate;
		initPig();
	}
	
	public PigScriptRunner(File theScriptFile, String theOutfile, String varToStore) throws FileNotFoundException {
		if (theScriptFile == null) {
			throw new IllegalArgumentException("Script file must be provided; null was passed instead.");
		}
		
		if (varToStore == null) {
			throw new IllegalArgumentException("Script variable to store must be provided; null was passed instead.");
		}
		
		if (theOutfile == null) {
			throw new IllegalArgumentException("Output file name must be provided; null was passed instead.");
		}
		
		scriptFile = theScriptFile;
		scriptInStream = new FileInputStream(theScriptFile);
		outFilePath = theOutfile;
		pigVar  = varToStore;
		initPig();
	}

	public PigScriptRunner(InputStream theScriptStream, String theOutfile, String varToStore) {
		if (theScriptStream== null) {
			throw new IllegalArgumentException("Script stream must be provided; null was passed instead.");
		}
		
		if (varToStore == null) {
			throw new IllegalArgumentException("Script variable to store must be provided; null was passed instead.");
		}
		
		if (theOutfile == null) {
			throw new IllegalArgumentException("Output file name must be provided; null was passed instead.");
		}
		
		scriptInStream = theScriptStream;
		outFilePath = theOutfile;
		pigVar  = varToStore;
		initPig();
	}
	
	public PigScriptRunner(InputStream theScriptStream, String theVarToPrintOrIterate) {
		scriptInStream = theScriptStream;
		pigVar  = theVarToPrintOrIterate;
		initPig();
	}
	
	
	public void addScriptCallParam(String paramName, String paramValue) {
		props.setProperty(paramName, paramValue);
	}
	
	public Iterator<Tuple> iterator() throws IOException {
		startServer();
		pserver.registerScript(scriptInStream);
		return pserver.openIterator(pigVar);
	}
	
	public void store() throws IOException {
		startServer();
		pserver.registerScript(scriptInStream);
		pserver.store(pigVar, outFilePath);
	}
	
	public void run() throws IOException {
		startServer();
		PigScriptReader scriptReader;
		if (scriptFile != null) {
			scriptReader = new PigScriptReader(scriptFile);
		} else {
			scriptReader = new PigScriptReader(scriptInStream);
		}
		while (scriptReader.hasNext()) {
			String scriptCodeLine = scriptReader.next();
			pserver.registerQuery(scriptCodeLine);
		}
	}
	
	private void initPig() {
		props.setProperty("pig.usenewlogicalplan", "false");
		props.setProperty("pig.temp.dir","/user/paepcke");
		props.setProperty("PIG_HOME",System.getenv("PIG_HOME"));
		String pigUserJarDir = System.getenv("PIG_USER_JAR_DIR");
		if (pigUserJarDir == null)
			pigUserJarDir = "target";
		props.setProperty("USER_CONTRIB", pigUserJarDir);
		props.setProperty("PIGIR_HOME", System.getenv("PIGIR_HOME"));
	}
	
	private void startServer() {
		try {
			//pserver = new PigServer(ExecType.MAPREDUCE, props);
			pserver = new PigServer(ExecType.LOCAL, props);
			pserver.debugOn();
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
