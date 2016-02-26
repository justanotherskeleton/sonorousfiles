package sonder.sonorous.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import sonder.sonorous.build.Build;
import sonder.sonorous.build.Policy;
import sonder.sonorous.network.data.*;
import sonder.sonorous.network.data.Error;
import sonder.sonorous.resource.FileUtil;
import sonder.sonorous.resource.Log;

public class FileServer {
	
	private Server server;
	
	private BufferedReader database_reader;
	
	public HashMap<Integer, Integer> transfer_ids = new HashMap<Integer, Integer>();
	public HashMap<Integer, FileOutputStream> transfer_in_streams = new HashMap<Integer, FileOutputStream>();
	public HashMap<Integer, FileInputStream> transfer_out_streams = new HashMap<Integer, FileInputStream>();
	public HashMap<Integer, Transfer> transfers = new HashMap<Integer, Transfer>();
	
	public FileServer() throws Exception {
		server = new Server();
		server.start();
		server.bind(Network.TCP_PORT);
		init();
		Log.write("Started server!");
		startListen();
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
		        		 File loc = new File(Build.RUNNING_PATH + "/client/" + ft.clientid + "/files/" + ft.name);
		        		 FileOutputStream fos = null;
		        		 try {
		        			 loc.createNewFile();
		        			 fos = new FileOutputStream(loc);
		        		 } catch (Exception e) {
		        			 e.printStackTrace();
		        		 }
		        		 transfer_in_streams.put(ft.id, fos);
		        		 server.sendToTCP(connection.getID(), Status.FT_RECEIVED);
		        		 ConfirmDownload ct = new ConfirmDownload();
		        		 ct.data = ft.sizeKB;
		        		 ct.id = ft.id;
		        		 ct.path = ft.name;
		        		 server.sendToTCP(connection.getID(), ct);
		        		 Log.write("Created new reception transfer stream! " + ft.id + ":" + ft.clientid);
		        	 }
		          }
		          
		          if(object instanceof FileRequest) {
		        	  FileRequest fr = (FileRequest) object;
		        	  if(transfer_ids.containsKey(fr.id)) {
		        		  server.sendToTCP(connection.getID(), Error.TRANSFER_ID_TAKEN);
		        		  Log.write("ERROR: Transfer ID collision with client " + fr.clientid + ". Informed client!");
		        	  } else {
		        		  File request = new File(Build.RUNNING_PATH + "/client/" + fr.clientid + "/files/" + fr.name);
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
		        			  ConfirmDownload ct = new ConfirmDownload();
		        			  ct.data = (FileUtil.fileSize(request) / 1024);
		        			  ct.id = fr.id;
		        			  server.sendToTCP(connection.getID(), ct);
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
		          
		          if(object instanceof Transfer) {
		        	  Transfer t = (Transfer) object;
		        	  Log.write("Received transfer object from " + t.clientid + ", awaiting ready status...");
		        	  transfers.put(t.id, t);
		          }
		          
		          if(object instanceof TransferReady) {
		        	  TransferReady tr = (TransferReady) object;
		        	  Log.write("Received transfer confirmation for transfer" + tr.id);
		        	  try {
							initialSegment(connection, tr.id);
						} catch (Exception e) {
							e.printStackTrace();
						}
		          }
		          
		          if(object instanceof ContinueTransfer) {
		        	  ContinueTransfer ct = (ContinueTransfer) object;
		        	  try {
						nextSegment(connection, ct.id);
					} catch (Exception e) {
						e.printStackTrace();
					}
		          }
		          
		          if(object instanceof IDRequest) {
		        	  
		          }
		       }
		    });
	}
	
	public void initialSegment(Connection c, int id) throws Exception {
		byte[] data = new byte[Policy.FILE_CRYPTO_BUFFER];
		FileInputStream fis = transfer_out_streams.get(id);
		fis.read(data, 0, Policy.FILE_CRYPTO_BUFFER);
		server.sendToTCP(c.getID(), new FileSegment(id, data));
		transfers.get(id).completed_blocks++;
	}
	
	public void nextSegment(Connection c, int id) throws Exception {
		byte[] data = new byte[Policy.FILE_CRYPTO_BUFFER];
		FileInputStream fis = transfer_out_streams.get(id);
		fis.skip(Policy.FILE_CRYPTO_BUFFER);
		fis.read(data, 0, Policy.FILE_CRYPTO_BUFFER);
		server.sendToTCP(c.getID(), new FileSegment(id, data));
		transfers.get(id).completed_blocks++;
	}
	
	public void init() throws Exception {
		File client_f = new File(Build.RUNNING_PATH + "/client");
		File client_db = new File(Build.RUNNING_PATH + "/client/clients.db");
		if(!client_f.exists()) {
			Log.write("Did not detect client folder, creating...");
			client_f.mkdirs();
			client_db.createNewFile();
		} else if(client_f.exists() && (!client_db.exists())) {
			Log.write("Did not find clients.db, creating one but this is not normal...");
			client_db.createNewFile();
		} else if(client_f.exists() && client_db.exists()) {
			Log.write("Found clients and client database, scanning...");
		}
		
		database_reader = new BufferedReader(new FileReader(client_db));
		String line;
		int users = 0;
		while ((line = database_reader.readLine()) != null) {
			users++;
		}
		
		Log.write("Found " + users + " entries in client database!");
	}
	
}
