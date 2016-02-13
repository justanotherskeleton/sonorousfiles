package src.sonorous.network;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import src.sonorous.build.Policy;
import src.sonorous.build.Sonorous;
import src.sonorous.event.CTInvoker;
import src.sonorous.event.CTListener;
import src.sonorous.resource.FileUtil;
import src.sonorous.resource.Log;
import src.sonorous.resource.TransferInfo;

public class SonClient {
	
	public String connectedTo;
	
	private Client client;
	private Kryo kryo;
	
	public HashMap<Byte, TransferInfo> fileTransfersIn;
	public HashMap<Byte, TransferOutInfo> fileTransfersOut;
	
	public SonClient() {
		Log.write("[CLIENT] Starting client service...");
		client = new Client();
	    client.start();
	    kryo = client.getKryo();
	    fileTransfersIn = new HashMap<Byte, TransferInfo>();
	    fileTransfersOut = new HashMap<Byte, TransferOutInfo>();
	    listen();
	}
	
	public void connect(String ip) throws Exception {
		Log.write("Connecting to " + ip + ":" + Network.TCP_PORT + "...");
		client.connect(5000, ip, Network.TCP_PORT);
		
		if(client.isConnected()) {
			connectedTo = ip;
			Log.write("Connected to " + ip + ":" + Network.TCP_PORT + "!");
		} else {
			Log.write("Connection failed! Retrying...");
			client.connect(5000, ip, Network.TCP_PORT);
			
			if(client.isConnected()) {
				connectedTo = ip;
				Log.write("Connected to " + ip + ":" + Network.TCP_PORT + "!");
			} else {
				Log.write("Connection failed the second time! Aborted task.");
			}
		}
	}
	
	public void listen() {
		client.addListener(new Listener() {
		       public void received (Connection connection, Object object) {
		    	   if(object instanceof FilePush) {
			        	  FilePush fp = (FilePush)object;
			        	  try {
							newFOSStream(fp);
						} catch (Exception e) {
							e.printStackTrace();
						}
			        	  
			        	  Log.write("New file incoming, " + fp.name + " id:" + fp.EFFECTIVE_ID);
			        	  send(Policy.FP_RECEIVED_BEGIN);
			          }
			          
			          if(object instanceof FileSegment) {
			        	  FileSegment fs = (FileSegment)object;
			        	  TransferInfo ti = fileTransfersIn.get(fs.id);
			        	  FileUtil.writeToFile(fs.data, ti.fos);
			        	  ti.completedSegments++;
			        	  ti.progress = ti.completedSegments / ti.segments;
			        	  send(new CTContinue(fs.id));
			          }
			          
			          if(object instanceof CTEnd) {
			        	  byte id = ((CTEnd)object).id;
			        	  TransferInfo ti = fileTransfersIn.get(id);
			        	  ti.completedSegments = ti.segments;
			        	  ti.progress = 100;
			        	  try {ti.fos.close();} catch (IOException e) {e.printStackTrace();}
			        	  Log.write("File transfer of " + ti.name + " complete id:" + id);
			        	  send(Policy.TRANSFER_END_CONFIRM);
			          }
			          
			          if(object instanceof CTContinue) {
			        	  CTContinue ctc = (CTContinue)object;
			        	  TransferOutInfo used = fileTransfersOut.get(ctc.id);
			        	  used.completedSegments++;
			        	  used.progress = used.completedSegments / used.segments;
			        	  FileSegment nextSegment = FileUtil.nextSegment(ctc.id, used.completedSegments, used.fis);
			        	  send(nextSegment);
			          }
		       }
		});
	}
	
	public void send(Object obj) {
		client.sendTCP(obj);
	}
	
	public void newFOSStream(FilePush fp) throws Exception {
		   File new_file = new File(Policy.TRANSFER_LOCATION + "ACTIVE" + fp.EFFECTIVE_ID + ".son");
	  	   new_file.createNewFile();
	  	   FileOutputStream new_fos = new FileOutputStream(new_file);
	  	   TransferInfo ti = new TransferInfo();
	  	   ti.fos = new_fos;
	  	   ti.progress = 0.00D;
	  	   ti.segments = 0;
	  	   ti.completedSegments = 0;
	  	   ti.name = fp.name;
	  	   ti.origin = fp.origin;
	  	   fileTransfersIn.put(fp.EFFECTIVE_ID, ti);
	}
	
	public Kryo getKryo() {
		return client.getKryo();
	}

}
