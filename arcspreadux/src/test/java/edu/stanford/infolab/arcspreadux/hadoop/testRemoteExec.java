package edu.stanford.infolab.arcspreadux.hadoop;

import java.net.InetAddress;
import java.util.concurrent.Callable;

import org.gridkit.nanocloud.CloudFactory;
import org.gridkit.vicluster.ViManager;
import org.junit.Test;

public class testRemoteExec {

	@Test 
	public void remote_hello_world() throws InterruptedException { 
		ViManager cloud = CloudFactory.createSimpleSshCloud(); 
		//cloud.node("ilhead2.stanford.edu"); 
		cloud.node("shark.stanford.edu"); 
		cloud.node("**").exec(
				new Callable<Void>() { 
					public Void call() throws Exception { 
						String localHost = InetAddress.getLocalHost().toString(); 
						System.out.println("Hi! I'm running on " + localHost); 
						return null; 
					} 
			}
		); // Console output is transfered asynchronously, // so it is better to wait few seconds for it to arrive 
		Thread.sleep(1000); 
	}	
}
