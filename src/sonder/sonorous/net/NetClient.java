package sonder.sonorous.net;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import sonder.sonorous.resource.Log;

public class NetClient {
	
	private Client client;
	private Kryo kryo;
	
	public int PORT_IN_USE;
	
	public NetClient() {
		Log.write("[CLIENT] Starting client service...");
		client = new Client();
	    client.start();
	    kryo = client.getKryo();
	    kryo.register(Byte.class);
	    kryo.register(String.class);
	    kryo.register(AESKey.class);
	    kryo.register(PortNeg.class);
	    PORT_IN_USE = 0;
	}
	
	public void connect(String ip, int port) throws Exception {
		Log.write("[CLIENT] Connecting to " + ip);
		
		client.connect(5000, ip, Network.RESERVED_COMMS);
		Log.write("Opened reserved port negotiator");
		
		client.connect(5000, ip, port);
		
		if(client.isConnected()) {
			Network.markTaken(port);
			PORT_IN_USE = port;
			Log.write("[CLIENT] Connection successful!");
		} else {
			Log.write("[CLIENT] Connection failed!");
		}
	}
	
	public void disconnect() {
		client.close();
		Network.markAvalible(PORT_IN_USE);
		PORT_IN_USE = 0;
		Log.write("[CLIENT] Client disconnected!");
	}
	
	public void stop() {
		Log.write("[CLIENT] Halting client service...");
		client.stop();
		Log.write("[CLIENT] Client service stopped!");
	}
	
	public void listen() {
		Log.write("[CLIENT] Client started!");
		client.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		          if(object instanceof String) {
		        	  Log.write("String received: " + (String)object);
		          }
		          
		          if(object instanceof Byte) {
		        	  if(((Byte)object) == 0x02) {
		        		  Log.write(connection.getRemoteAddressTCP().getAddress().getHostAddress() + 
		        				  " has responded to your ping!");
		        	  }
		          }
		       }
		});
	}
	
	public void send(Object obj) {
		client.sendTCP(obj);
	}
	
	public boolean isConnected() {
		return client.isConnected();
	}

}
