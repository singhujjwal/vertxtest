package com.cisco.zookeeper.main;

import java.rmi.Remote;
import java.util.Set;

public interface TransactionProcessor extends Remote{
	
	public void transact(Integer account, Integer amount) throws Exception;
	
	public Set<Integer> getAccounts() throws Exception;
}

