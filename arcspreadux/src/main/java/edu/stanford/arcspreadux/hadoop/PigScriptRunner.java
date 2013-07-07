/**
 * 
 */
package edu.stanford.arcspreadux.hadoop;

/**
 * @author paepcke
 *
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

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
		scriptFile = theScriptFile;
		scriptInStream = new FileInputStream(theScriptFile);		
		pigVar = theVarToPrintOrIterate;
		initPig();
	}
	
	public PigScriptRunner(File theScriptFile, String theOutfile, String varToStore) throws FileNotFoundException {
		scriptFile = theScriptFile;
		scriptInStream = new FileInputStream(theScriptFile);
		outFilePath = theOutfile;
		pigVar  = varToStore;
		initPig();
	}

	public PigScriptRunner(InputStream theScriptStream, String theOutfile, String varToStore) {
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
		pserver.store(pigVar, outFilePath);
		//**************
		Set<String> aliasSet = pserver.getAliasKeySet();
		for (String el : aliasSet) {
			System.out.println(el);
		}
		//**************		
	}
	
	public void run() {
		startServer();
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
