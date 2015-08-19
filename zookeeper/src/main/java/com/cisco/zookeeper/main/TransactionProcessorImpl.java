package com.cisco.zookeeper.main;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class TransactionProcessorImpl extends ReceiverAdapter implements
		TransactionProcessor {
	JChannel channel;
	ConcurrentHashMap<Integer, Address> accountStorage = new ConcurrentHashMap<Integer, Address>();
	ConcurrentHashMap<Integer, List<Integer>> transactions = new ConcurrentHashMap<Integer, List<Integer>>();

	public TransactionProcessorImpl() throws Exception {
		System.setProperty("java.net.preferIPv4Stack","true");
		System.out.println(System.getProperty("jgroups.bind_addr"));
		channel = new JChannel(); // use the default config, udp.xml
		channel.connect("ServiceNodesCluster-Ujjwal");
		channel.setReceiver(this);
	}

	public synchronized void transact(Integer account, Integer amount)
			throws Exception {
		if (accountStorage.get(account) == null) {
			System.out.println("New account creation: " + account);
			Map<Integer, Address> accountLocation = new HashMap();
			accountLocation.put(account, channel.getAddress());
			Message msg = new Message(null, null, accountLocation);
			channel.send(msg);
			accountStorage.put(account, channel.getAddress());
			// Now store it locally
			if (transactions.get(account) == null) {
				transactions.put(account, new ArrayList());
			}
			transactions.get(account).add(amount);
			return;
		}
		if (accountStorage.get(account).equals(channel.getAddressAsString())) {
			System.out.println("Account is here...");
			// Account stored on this host record transaction
			if (transactions.get(account) == null) {
				transactions.put(account, new ArrayList());
			}
			transactions.get(account).add(amount);
		} else {
			// Account is else where.. send the transaction there
			System.out.println("Account stored on: "
					+ accountStorage.get(account));
			Map<Integer, Integer> transaction = new HashMap<Integer, Integer>();
			transaction.put(account, amount);
			System.out.println(channel.getAddress().getClass().getName());
			Message msg = new Message(accountStorage.get(account), null,
					transaction);
			channel.send(msg);
		}

	}

	public void receive(Message msg) {
		System.out.println(channel.getAddressAsString() + " Received message: "
				+ msg.getObject());
		if (channel.getAddress().equals(msg.getSrc())) {
			// Do not process self messages
			return;
		}
		if (!(msg.getObject() instanceof Map)) {
			return;
		}
		Map data = (Map) msg.getObject();
		Integer account = (Integer) data.keySet().iterator().next();
		if (data.get(account) instanceof Address) {
			accountStorage
					.putIfAbsent(account, (Address) data.get(account));
		} else {
			synchronized (transactions) {
				if (transactions.get(account) == null) {
					transactions.put(account, new ArrayList());
				}
			}
			transactions.get(account).add((Integer) data.get(account));
		}
	}

	public Set<Integer> getAccounts() throws Exception {
		Set<Integer> retVal = new HashSet();
		retVal.addAll(transactions.keySet());
		return retVal;
	}

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