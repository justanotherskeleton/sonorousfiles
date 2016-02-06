package src.sonorous.network;

import java.io.File;
import java.io.FileInputStream;
import java.util.Random;

import src.sonorous.build.Policy;
import src.sonorous.event.CTListener;
import src.sonorous.resource.Log;

public class TransferThread implements Runnable, CTListener {
	
	public int id;
	public int progress;
	private File encrypted;
	private SonClient c;
	private FileInputStream fis;
	private FilePush fp;
	
	public TransferThread(File encrypted, SonClient c) {
		this.id = new Random().nextInt((255 - 1) + 1) + 1;
		Log.write("Created transfer thread id:" + id);
		this.encrypted = encrypted;
		this.c = c;
	}
	
	@Override
	public void run() {
		try {
		FilePush fp = new FilePush();
		fp.signFor(encrypted);
		Log.write("Starting transfer of '" + encrypted.getAbsolutePath() + "' to " + c.connectedTo);
		Log.write(fp.sizeKB + "KB, " + fp.segments + " segments long, ID:" + fp.EFFECTIVE_ID);
		Log.write("..........");
		this.fp = fp;
		c.send(fp);
		fis = new FileInputStream(encrypted);
		} catch (Exception e) {
			Log.write("Error occured in transfer thread");
			e.printStackTrace();
		}
	}
	
	@Override
	public void segmentReceived() {
		try {
			byte[] buffer = new byte[Policy.FILE_CRYPTO_BUFFER];
			int in = fis.read(buffer);
			FileSegment fs = new FileSegment();
			fs.id = fp.EFFECTIVE_ID;
			fs.data = buffer;
			c.send(fs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		progress++;
		if(progress >= fp.segments) {
			Log.write("File transfer completed id:" + id);
			Thread.currentThread().interrupt();
			return;
		}
	}

}
