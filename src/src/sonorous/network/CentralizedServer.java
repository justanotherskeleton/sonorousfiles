package src.sonorous.network;

import java.util.LinkedList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import src.sonorous.resource.Log;

public class CentralizedServer {
	
	private Server server;
	private Kryo kryo;
	
	public LinkedList<String> connected;
	
	public CentralizedServer() throws Exception {
		Log.write("Starting centralized server...");
		server = new Server();
		server.start();
	    server.bind(Network.TCP_PORT);
	    kryo = server.getKryo();
	}
	
	public void listen() {
		Log.write("Server listening!");
		server.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		          if(object instanceof FileSegment) {
		        	  
		          }
		       }
		    });
	}

}
