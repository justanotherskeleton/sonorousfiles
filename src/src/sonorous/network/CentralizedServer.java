package src.sonorous.network;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import src.sonorous.build.Policy;
import src.sonorous.resource.FileUtil;
import src.sonorous.resource.Log;

public class CentralizedServer {
	
	private Server server;
	private Kryo kryo;
	
	public LinkedList<Integer> connected;
	public LinkedList<FilePush> transfers;
	public ArrayList<Byte> activeTransfer_IDs;
	public HashMap<Byte, FileOutputStream> transfer_fos;
	
	public CentralizedServer() throws Exception {
		Log.write("Starting centralized server...");
		server = new Server();
		server.start();
	    server.bind(Network.TCP_PORT);
	    kryo = server.getKryo();
	    connected = new LinkedList<Integer>();
	    transfers = new LinkedList<FilePush>();
	    activeTransfer_IDs = new ArrayList<Byte>();
	    transfer_fos = new HashMap<Byte, FileOutputStream>();
	    listen();
	}
	
	public void listen() {
		Log.write("Server listening!");
		server.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		    	   
		    	  if(!connected.contains(connection.getID())) {
		    		  server.sendToTCP(connection.getID(), Policy.UNAUTHORIZED_CLIENT);
		    	  }
		    	  
		    	  if(connected.contains(connection.getID())) {
		    		  
		    	  if(object instanceof FilePush) {
		    		  
		    		  FilePush fp = (FilePush)object;
		    		  Log.write("Received file push from '" + fp.origin + "'");
		    		  if(fp.origin.equalsIgnoreCase(connection.getRemoteAddressTCP().getAddress().getHostAddress())) {
		    			  transfers.add(fp);
			    		  fp.spill();
			    		  File new_fos_out = new File(Policy.TRANSFER_LOCATION + fp.name + "-" + connection.getID() + ".transfer");
			    		  FileOutputStream new_fos;
			    		  try {
			    			new_fos_out.createNewFile();
							new_fos = new FileOutputStream(new_fos_out);
						} catch (Exception e) {
							e.printStackTrace();
						}
		    		  } else {
		    			  server.sendToTCP(connection.getID(), Policy.FP_MISMATCH);
		    		  }
		    		  
		    	  }
		    	  
		          if(object instanceof FileSegment) {
		        	  FileSegment fs = (FileSegment)object;
		        	  if(activeTransfer_IDs.contains(fs.id)) {
		        		  FileUtil.writeToFile(fs.data, transfer_fos.get(fs.id));
		        	  }
		          }
		       }
		       }
		    });
	}

}
