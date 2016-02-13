package src.sonorous.build;

import java.io.File;

import src.sonorous.event.*;
import src.sonorous.network.DecentralizedServer;
import src.sonorous.network.Network;
import src.sonorous.network.SonClient;
import src.sonorous.resource.Crypto;
import src.sonorous.resource.FileUtil;
import src.sonorous.resource.Log;

public class Sonorous {
	
	//event handling
	public SonClient client;
	public DecentralizedServer server;
	
	public static void main(String[] args) throws Exception {
		Crypto.init();
		Network.init();
		Policy.init();
		
		Sonorous me = new Sonorous();
		me.start();
	}
	
	public void start() throws Exception {
		client = new SonClient();
		server = new DecentralizedServer();
		Policy.initSharedKryo(client.getKryo());
		Policy.initSharedKryo(server.getKryo());
		Log.write("Kryo initialized!");
		
	}

}
