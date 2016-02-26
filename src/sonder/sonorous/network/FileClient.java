package sonder.sonorous.network;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import sonder.sonorous.build.Build;
import sonder.sonorous.build.Policy;
import sonder.sonorous.network.data.*;
import sonder.sonorous.network.data.Error;
import sonder.sonorous.resource.Log;

public class FileClient {
	
	private Client client;
	
	public HashMap<Integer, FileOutputStream> transfer_in_streams = new HashMap<Integer, FileOutputStream>();
	public HashMap<Integer, FileInputStream> transfer_out_streams = new HashMap<Integer, FileInputStream>();
	public HashMap<Integer, Transfer> transfer_info = new HashMap<Integer, Transfer>();
	
	public FileClient() {
		client = new Client();
		client.start();
		Log.write("Started client!");
		startListen();
	}
	
	public void connect(String ip) throws Exception {
		Log.write("Connecting to " + ip + "...");
		client.connect(5000, ip, Network.TCP_PORT);
		
		if(client.isConnected()) {
			Log.write("Successfully connected to " + ip + "!");
		}
	}
	
	public void startListen() {
		client.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		          if(object instanceof ConfirmDownload) {
		        	  ConfirmDownload ct = (ConfirmDownload) object;
		        	  Log.write("Received transfer confirmation for " + ct.id + ", size(kb):" + ct.data);
		        	  File w = new File(Build.RUNNING_PATH + "/files/" + ct.path);
		        	  if(w.exists()) {
		        		  w.delete();
		        		  try {w.createNewFile(); } catch (Exception e) { e.printStackTrace(); }
		        	  } else if(!w.exists()) {
		        		  try {w.createNewFile(); } catch (Exception e) { e.printStackTrace(); }
		        	  }
		        	  
		        	  FileOutputStream fos = null;
		        	  try { fos = new FileOutputStream(w); } catch (Exception e) { e.printStackTrace(); }
		        	  transfer_in_streams.put(ct.id, fos);
		        	  Transfer t = new Transfer();
		        	  t.clientid = Build.CLIENT_ID;
		        	  t.id = ct.id;
		        	  t.path = ct.path;
		        	  t.size = ct.data;
		        	  t.completed_blocks = 0;
		        	  t.total_blocks = (long) Math.ceil(t.size / Policy.FILE_CRYPTO_BUFFER);
		        	  client.sendTCP(t);
		        	  client.sendTCP(new TransferReady(ct.id));
		          }
		          
		          if(object instanceof FileSegment) {
		        	  FileSegment fs = (FileSegment) object;
		        	  if(!transfer_in_streams.containsKey(fs.id)) {
		        		  client.sendTCP(Error.REQUESTED_FILE_404);
		        		  Log.write("ERROR: Server has sent data marked with an invalid ID. Server has been informed!");
		        	  } else {
		        		  try {
							transfer_in_streams.get(fs.id).write(fs.data);
							transfer_info.get(fs.id).completed_blocks++;
							client.sendTCP(new ContinueTransfer(fs.id));
						} catch (IOException e) {
							e.printStackTrace();
						}
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
 		       }
		    });
	}
	
	public void initialSegment(Connection c, int id) throws Exception {
		byte[] data = new byte[Policy.FILE_CRYPTO_BUFFER];
		FileInputStream fis = transfer_out_streams.get(id);
		fis.read(data, 0, Policy.FILE_CRYPTO_BUFFER);
		client.sendTCP(new FileSegment(id, data));
		transfer_info.get(id).completed_blocks++;
	}
	
	public void nextSegment(Connection c, int id) throws Exception {
		byte[] data = new byte[Policy.FILE_CRYPTO_BUFFER];
		FileInputStream fis = transfer_out_streams.get(id);
		fis.skip(Policy.FILE_CRYPTO_BUFFER);
		fis.read(data, 0, Policy.FILE_CRYPTO_BUFFER);
		client.sendTCP(new FileSegment(id, data));
		transfer_info.get(id).completed_blocks++;
	}
	
	public void init() {
		File fs = new File(Build.FILE_STORAGE);
		if(!fs.exists()) {
			Log.write(".../files doesn't exist, creating...");
			fs.mkdirs();
		}
	}
}
