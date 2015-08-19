package com.cisco.zookeeper.main;

import javax.net.ssl.HostnameVerifier;

import org.jgroups.JChannel;
import org.jgroups.Message;
import org.jgroups.ReceiverAdapter;

public class ClusterNode extends ReceiverAdapter {
	JChannel channel;
	public ClusterNode() throws Exception {
		// TODO Auto-generated constructor stub
		System.setProperty("jgroups.bind_addr","10.127.143.196");
		System.setProperty("java.net.preferIPv4Stack","true");
		channel = new JChannel(); // use the default config, udp.xml
		channel.connect("ServiceNodesCluster-Ujjwal");
		channel.setReceiver(this);
	}

	public void sendMessage(String message) throws Exception{
		Message m = new Message(null, null, message);
		channel.send(m);
	
	}
	
	public void receive(Message msg) {
		System.out.println(channel.getAddressAsString() + " Received message: "
				+ msg.getObject()+ " from host: "+msg.getSrc());
	}
	
	public static void main(String [] argv){
		System.out.println("The program for zookeeping started");
		try{
		ClusterNode  c = new ClusterNode();
		Thread.sleep(10000);
		c.sendMessage("Hello I am Ujjwal");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
