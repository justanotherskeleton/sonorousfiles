package src.sonorous.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import src.sonorous.build.Policy;
import src.sonorous.resource.FileUtil;
import src.sonorous.resource.Log;
import src.sonorous.resource.TransferInfo;

public class DecentralizedServer {
	
	private Server server;
	private Kryo kryo;
	
	public LinkedList<String> connected;
	
	public DecentralizedServer() throws Exception {
		Log.write("Starting decentralized server...");
		server = new Server();
		server.start();
	    server.bind(Network.TCP_PORT);
	    kryo = server.getKryo();
	    connected = new LinkedList<String>();
	    listen();
	}
	
	public void listen() {
		server.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		          if(object instanceof ServerReflect) {
		        	  server.sendToAllExceptTCP(connection.getID(), object);
		          }
		       }
		    });
	}
	
	public Kryo getKryo() {
		return server.getKryo();
	}	

}
