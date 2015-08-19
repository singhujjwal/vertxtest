package com.cisco.zookeeper.main;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;

class ProcessMonitor extends Thread {
	Process p;

	public ProcessMonitor(Process p) {
		
		this.p = p;
	}

	public void run() {
		try {
			InputStream is = p.getErrorStream();
			InputStreamReader isr = new InputStreamReader(is);
			LineNumberReader lnr = new LineNumberReader(isr);
			String line = "";
			while ((line = lnr.readLine()) != null) {
				System.out.println(line);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



public class ProcessController {
	public static void main(String[] args) throws Exception {
		Process[] processArr = new Process[5];
		for (int i = 0; i < 5; i++) {
			ProcessBuilder pb = new ProcessBuilder("java", 
					"-Djgroups.bind_addr=10.127.143.196", 
					"-Djava.net.preferIPv4Stack=true", "-cp",
					"/Users/ujjsingh/Desktop/txprocessor.jar",
					"com.cisco.zookeeper.main.TransactionProcessorImpl", "txp" + i);
			processArr[i] = pb.start();
			Thread.sleep(2000);
			new ProcessMonitor(processArr[i]).start();
		}
		System.out
				.println("Launched the processes. Press any key to terminate the cluster");
		System.in.read();
		for (Process process : processArr) {
			process.destroy();
		}
		System.out.println("Done");
	}
}