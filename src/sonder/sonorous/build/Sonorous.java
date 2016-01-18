package sonder.sonorous.build;

import java.util.Scanner;

import sonder.sonorous.net.NetClient;
import sonder.sonorous.net.NetServer;
import sonder.sonorous.net.Network;
import sonder.sonorous.net.RSAKey;
import sonder.sonorous.resource.Crypto;
import sonder.sonorous.resource.Log;

public class Sonorous {
	
	public boolean CLI_RUN = false;
	public boolean RSA_GENERATED = false;
	public String REMOTE, PUBLIC_KEY, PRIVATE_KEY;
	
	public static boolean PUBLIC_PENDING = false;
	
	public static void main(String[] args) throws Exception {
		Log.write("[SONOROUS FILE SERVICE]");
		Log.write("VERSION: " + Meta.VERSION);
		Log.write("- - - - - - - - - - - - - - -");
		Log.write("");
		
		Network.init();
		Sonorous application = new Sonorous();
		application.service();
	}
	
	public void service() throws Exception {
		
		CLI_RUN = true;
		//Start client service
		NetClient client = new NetClient();
		client.listen();
		
		//Start server service
		NetServer server = new NetServer();
		server.listen();
		
		Scanner in = new Scanner(System.in);
		
		while(CLI_RUN) {
			String input = in.nextLine();
			
			if(input.equalsIgnoreCase("exit")) {
				Log.write("Quitting application");
				client.disconnect();
				client.stop();
				server.stop();
				Log.write("[END SONOROUS CLI SESSION]");
				System.exit(0);
			}
			
			if(input.startsWith("connect")) {
				String[] reg = input.split(" ");
				String ip = reg[1];
				if(client.isConnected()) {
					Log.write("Client is already connected to " + REMOTE + "!");
					Log.write("Use the disconnect command then retry your previous command");
				} else if(server.connected.contains(ip)) {
					client.connect(ip, Network.TCP_PORT_SECONDARY);
				} else {
				
					client.connect(ip, Network.TCP_PORT);
				
					if(client.isConnected()) {
						REMOTE = ip;
					}
				}
			}
			
			if(input.equalsIgnoreCase("abort")) {
				Log.write("ABORTING");
				System.exit(-1);
			}
			
			if(input.equalsIgnoreCase("disconnect")) {
				client.disconnect();
			}
			
			if(input.startsWith("ping")) {
				if(!client.isConnected()) {
					Log.write("Client is not connected. hint: use connect (ip) to remote into another client");
				} else {
					Byte p = 0x01;
					Log.write("Sent ping to " + REMOTE + "!");
					client.send(p);
				}
			}
			
			if(input.startsWith("comm")) {
				if(!client.isConnected()) {
					Log.write("Client is not connected. hint: use connect (ip) to remote into another client");
				} else {
					String[] str = input.split(" ", 2);
					String msg = str[1];
					client.send(msg);
					Log.write("Sent comm to " + REMOTE);
				}
			}
			
			if(input.equalsIgnoreCase("keypair")) {
				if(RSA_GENERATED) {
					Log.write("A key pair has already been generated!");
				} else {
					Log.write("Generating key pair...");
					String[] keys = Crypto.generateRSA();
					this.PUBLIC_KEY = keys[0];
					this.PRIVATE_KEY = keys[1];
					RSA_GENERATED = true;
					Log.write("Generated RSA pair!");
				}
			}
			
			if(input.startsWith("request")) {
				String[] str = input.split(" ");
				if(str[1].equalsIgnoreCase("key")) {
					Log.write("Requested public key from remote client!");
					Byte r = 0x11;
					client.send(r);
				} else {
					Log.write("Object not recognized");
				}
			}
			
			if(input.equalsIgnoreCase("keys")) {
				Log.write("PUBLIC: " + PUBLIC_KEY);
				Log.write("PRIVATE: " + PRIVATE_KEY);
			}
			
			if(input.equalsIgnoreCase("y")) {
				if(PUBLIC_PENDING) {
					RSAKey key = new RSAKey();
					key.key = PUBLIC_KEY;
					client.send(key);
					Log.write("Sent public key!");
				}
			}
			
			if(input.equalsIgnoreCase("whoami")) {
				Log.write("You are " + Network.PUBLIC_IP);
			}
		}
	}

}
