package sonder.sonorous.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import sonder.sonorous.build.Build;
import sonder.sonorous.network.data.*;
import sonder.sonorous.network.data.Error;
import sonder.sonorous.resource.Log;

public class FileServer {
	
	private Server server;
	
	public HashMap<Integer, Integer> transfer_ids = new HashMap<Integer, Integer>();
	public HashMap<Integer, FileOutputStream> transfer_in_streams = new HashMap<Integer, FileOutputStream>();
	public HashMap<Integer, FileInputStream> transfer_out_streams = new HashMap<Integer, FileInputStream>();
	
	public FileServer() throws Exception {
		server = new Server();
		server.start();
		server.bind(Network.TCP_PORT);
		Log.write("Started server!");
	}
	
	public void startListen() {
		server.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		          if (object instanceof FileTransfer) {
		        	 FileTransfer ft = (FileTransfer)object;
		        	 if(transfer_ids.containsKey(ft.id)) {
		        		 server.sendToTCP(connection.getID(), Error.TRANSFER_ID_TAKEN);
		        		 Log.write("ERROR: Transfer ID collision with client " + ft.clientid + ". Informed client!");
		        	 } else {
		        		 transfer_ids.put(ft.id, ft.clientid);
		        		 File loc = new File(Build.RUNNING_PATH + "/" + ft.clientid + "/files/" + ft.name);
		        		 FileOutputStream fos = null;
		        		 try {
		        			 loc.createNewFile();
		        			 fos = new FileOutputStream(loc);
		        		 } catch (Exception e) {
		        			 e.printStackTrace();
		        		 }
		        		 transfer_in_streams.put(ft.id, fos);
		        		 server.sendToTCP(connection.getID(), Status.FT_RECEIVED);
		        		 Log.write("Created new reception transfer stream! " + ft.id + ":" + ft.clientid);
		        	 }
		          }
		          
		          if(object instanceof FileRequest) {
		        	  FileRequest fr = (FileRequest) object;
		        	  if(transfer_ids.containsKey(fr.id)) {
		        		  server.sendToTCP(connection.getID(), Error.TRANSFER_ID_TAKEN);
		        		  Log.write("ERROR: Transfer ID collision with client " + fr.clientid + ". Informed client!");
		        	  } else {
		        		  File request = new File(Build.RUNNING_PATH + "/" + fr.clientid + "/files/" + fr.name);
		        		  if(!request.exists()) {
		        			  server.sendToTCP(connection.getID(), Error.REQUESTED_FILE_404);
		        			  Log.write("ERROR: File requested by client:" + fr.clientid + " does not exisit. Informed client!");
		        		  } else {
		        			  transfer_ids.put(fr.id, fr.clientid);
		        			  FileInputStream fis = null;
		        			  try {
		        				  fis = new FileInputStream(request);
		        			  } catch (Exception e) {
		        				  e.printStackTrace();
		        			  }
		        			  transfer_out_streams.put(fr.id, fis);
		        			  server.sendToTCP(connection.getID(), Status.FR_RECEIVED);
		        			  Log.write("Created new file transmission stream! " + fr.id + ":" + fr.clientid);
		        		  }
		        	  }
		          }
		          
		          if(object instanceof FileSegment) {
		        	  FileSegment fs = (FileSegment) object;
		        	  if(transfer_ids.containsKey(fs.id)) {
		        		  FileOutputStream fos = transfer_in_streams.get(fs.id);
		        		  try {
		        			  fos.write(fs.data);
		        		  	} catch (IOException e) {
								e.printStackTrace();
							}
		        		  
		        		  server.sendToTCP(connection.getID(), Status.TRANSFER_CONTINUE);
		        	  } else {
		        		  server.sendToTCP(connection.getID(), Error.INVALID_TRANSFER_ID);
		        		  Log.write("ERROR: File segment contained invalid ID. Informed client!");
		        	  }
		          }
		       }
		    });
	}
	
}
