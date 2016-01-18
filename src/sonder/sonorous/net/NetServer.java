package sonder.sonorous.net;

import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import sonder.sonorous.build.Sonorous;
import sonder.sonorous.resource.Log;

public class NetServer {
	
	private Server server;
	private Kryo kryo;
	
	public NetServer() throws Exception {
		Log.write("[SERVER] Starting server service...");
		server = new Server();
	    server.start();;
	    server.bind(Network.TCP_PORT);
	    kryo = server.getKryo();
	    kryo.register(Byte.class);
	    kryo.register(String.class);
	    kryo.register(AESKey.class);
	}
	
	public void stop() {
		Log.write("[SERVER] Halting server...");
		server.stop();
		Log.write("[SERVER] Server service stopped!");
	}
	
	public void listen() {
		Log.write("[SERVER] Server now listening!");
		server.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		          if (object instanceof String) {
		             Log.write("Comm@" + connection.getRemoteAddressTCP().getAddress().getHostAddress()
		            		 + ": " + (String)object);
		          }
		          
		          if(object instanceof Byte) {
		        	  if(((Byte)object) == 0x01) {
		        		  Byte r = 0x02;
		        		  server.sendToTCP(connection.getID(), r);
		        	  }
		        	  
		        	  //public key request
		        	  if(((Byte)object) == 0x11) {
		        		  String ip = connection.getRemoteAddressTCP().getAddress().getHostAddress();
		        		  Log.write(ip + " has requested your public key!");
		        		  Log.write("To send type 'y', type 'n' to decline...");
		        		  Sonorous.PUBLIC_PENDING = true;
		        	  }
		          }
		       }
		});
	}

}