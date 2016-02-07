package src.sonorous.network;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;

import src.sonorous.build.Policy;
import src.sonorous.resource.FileUtil;
import src.sonorous.resource.Log;

public class FilePush {
	
	public byte EFFECTIVE_ID;
	public long sizeKB, segments;
	public String name, origin;
	
	public void signFor(File f) {
		byte[] eff_id = new byte[2];
		new Random().nextBytes(eff_id);
		this.EFFECTIVE_ID = eff_id[1];
		
		if(f.isDirectory()) {
			Log.write("You cannot sign a directory using this method! FilePush.signFor()");
			return;
		}
		
		this.sizeKB = f.length();
		double seg_math = this.sizeKB / Policy.FILE_CRYPTO_BUFFER;
		this.segments = (long) Math.ceil(seg_math);
		this.name = f.getName();
		this.origin = Network.PUBLIC_IP;
		Log.write("Signed pending file push with proper data");
	}
	
	public void spill() {
		Log.write("FILE PUSH: ID:" + EFFECTIVE_ID + ", SIZE (KILOBYTES):" + sizeKB + ", SEGMENTS:" + segments
				+ "NAME:" + name + ", ORIGIN:" + origin + "||");
	}

}
