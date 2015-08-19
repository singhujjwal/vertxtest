package com.cisco.zookeeper.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class Transactor implements Runnable{
	TransactionProcessor proc;
	Random rand;
	public Transactor(TransactionProcessor tp) {
		proc = tp;
		rand = new Random();
	}
	public void run() {
		try {
			for(int i=0; i< 10; i++){
				proc.transact(Math.abs(rand.nextInt() % 500), 100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



public class LoaderClient {
	
	public static void main(String[] argv) throws Exception {
		Registry registry = LocateRegistry.getRegistry("10.127.143.196", 1099);

		Executor exec = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 100; i++) {
			TransactionProcessor proc = (TransactionProcessor) registry.lookup("txp" + (i % 5));
			exec.execute(new Transactor(proc));
		}
		Thread.sleep(10000);
		Set<Integer> allAccounts = new HashSet<>();
		for (int i = 0; i < 5; i++) {
			TransactionProcessor proc = (TransactionProcessor) registry.lookup("txp" + i);
			Set<Integer> accounts = proc.getAccounts();
			System.out.println(accounts);
			for (Integer account : accounts) {
				if (!allAccounts.add(account)) {
					System.out.println("Account duplicated: " + account);
				}
			}
		}
		System.out.println("Done");
	}

}
