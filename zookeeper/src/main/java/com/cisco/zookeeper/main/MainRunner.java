package com.cisco.zookeeper.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class MainRunner {
	
	public static void main(String[] args) throws Exception {
		System.out.println("Starting the transaction server...");
		TransactionProcessorImpl impl = new TransactionProcessorImpl();
		TransactionProcessor txpStub = (TransactionProcessor) UnicastRemoteObject
				.exportObject(impl, 0);
		Registry registry = LocateRegistry.getRegistry();
		registry.rebind(args[0], txpStub);
		System.out.println("txProcessor bound with: " + args[0]);
	}

}
