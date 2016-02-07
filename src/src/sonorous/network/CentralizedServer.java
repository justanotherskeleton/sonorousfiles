package src.sonorous.network;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import src.sonorous.resource.FileUtil;
import src.sonorous.resource.Log;

public class CentralizedServer {
	
	private Server server;
	private Kryo kryo;
	
	public LinkedList<String> connected;
	public LinkedList<FilePush> transfers;
	public ArrayList<Byte> activeTransfer_IDs;
	public HashMap<Byte, FileOutputStream> transfer_fos;
	
	public CentralizedServer() throws Exception {
		Log.write("Starting centralized server...");
		server = new Server();
		server.start();
	    server.bind(Network.TCP_PORT);
	    kryo = server.getKryo();
	    connected = new LinkedList<String>();
	    transfers = new LinkedList<FilePush>();
	    activeTransfer_IDs = new ArrayList<Byte>();
	    transfer_fos = new HashMap<Byte, FileOutputStream>();
	}
	
	public void listen() {
		Log.write("Server listening!");
		server.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		    	  if(object instanceof FilePush) {
		    		  FilePush fp = (FilePush)object;
		    		  Log.write("Received file push from '" + fp.origin + "'");
		    		  transfers.add(fp);
		    		  fp.spill();
		    	  }
		    	  
		          if(object instanceof FileSegment) {
		        	  FileSegment fs = (FileSegment)object;
		        	  if(activeTransfer_IDs.contains(fs.id)) {
		        		  FileUtil.writeToFile(fs.data, transfer_fos.get(fs.id));
		        	  }
		          }
		       }
		    });
	}

}
