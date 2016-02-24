package sonder.sonorous.resource;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import sonder.sonorous.build.Build;
import sonder.sonorous.build.Policy;

public class Database {
	
	//Build the database from folder sources or individual files
	public static boolean build(File[] sources, File output) throws Exception {
		if(output.exists()) {
			if(Policy.ALLOW_OVERWRITE) {
				Log.write("Output already exists! Overwritting...");
				output.delete();
				output.createNewFile();
			} else if(!Policy.ALLOW_OVERWRITE) {
				Log.write("Output already exists, overwrite is disabled via policy. Aborting operation!");
				return false;
			}
		} else {
			output.createNewFile();
			Log.write("Created output file.");
		}
		
		ArrayList<File> all_files = new ArrayList<File>();
		
		Log.write("Gathering all files...");
		for(File f1 : sources) {
			FileUtil.listf(f1.getAbsolutePath(), all_files);
		}
		
		long sizeKB = FileUtil.getSizeOfFiles(all_files) / 1000;
		Log.write("Found a total of " + all_files.size() + " files throughout all " + sources.length + " sources!");
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		Log.write("Writing database...");
		
		bw.write("SONOROUS DATABASE[>" + Build.VERSION);
		bw.write("SIZE[>" + sizeKB);
		bw.write("DATE[>" + Log.getFullTimestamp());
		bw.write("CLIENTID[>" + Build.CLIENT_ID);
		bw.write("BEGIN DATA[>");
		for(File f : all_files) {
			String h = Crypto.encodeB64(FileUtil.getHash(f));
			String d = Crypto.encodeB64(f.getAbsolutePath());
			bw.write(d + ":::" + h);
		}
		
		bw.close();
		Log.write("Wrote all data to " + output.getAbsolutePath() + "!");
		return true;
	}

}
