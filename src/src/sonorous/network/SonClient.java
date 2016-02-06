package src.sonorous.network;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import src.sonorous.build.Policy;
import src.sonorous.event.CTInvoker;
import src.sonorous.event.CTListener;
import src.sonorous.resource.Log;

public class SonClient {
	
	public String connectedTo;
	
	private Client client;
	private Kryo kryo;
	
	public SonClient() {
		Log.write("[CLIENT] Starting client service...");
		client = new Client();
	    client.start();
	    kryo = client.getKryo();
	    
	}
	
	public void connect(String ip) throws Exception {
		Log.write("Connecting to " + ip + ":" + Network.TCP_PORT + "...");
		client.connect(5000, ip, Network.TCP_PORT);
		
		if(client.isConnected()) {
			connectedTo = ip;
			Log.write("Connected to " + ip + ":" + Network.TCP_PORT + "!");
		} else {
			Log.write("Connection failed! Retrying...");
			client.connect(5000, ip, Network.TCP_PORT);
			
			if(client.isConnected()) {
				connectedTo = ip;
				Log.write("Connected to " + ip + ":" + Network.TCP_PORT + "!");
			} else {
				Log.write("Connection failed the second time! Aborted task.");
			}
		}
	}
	
	public void listen() {
		client.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		          
		       }
		});
	}
	
	public void sendFile(File encrypted) throws Exception {
		TransferThread tt = new TransferThread(encrypted, this);
		tt.run();
	}
	
	public void send(Object obj) {
		client.sendTCP(obj);
	}

}
