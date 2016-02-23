package sonder.sonorous.network;

import java.io.IOException;

import com.esotericsoftware.kryonet.Client;

import sonder.sonorous.resource.Log;

public class FileClient {
	
	private Client client;
	
	public FileClient() {
		client = new Client();
		client.start();
		Log.write("Started client!");
	}
	
	public void connect(String ip) throws Exception {
		Log.write("Connecting to " + ip + "...");
		client.connect(5000, ip, Network.TCP_PORT);
		
		if(client.isConnected()) {
			Log.write("Successfully connected to " + ip + "!");
		}
	}
}
