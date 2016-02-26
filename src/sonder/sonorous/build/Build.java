package sonder.sonorous.build;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import sonder.sonorous.resource.Log;
import sonder.sonorous.resource.Util;

public class Build {
	
	public static String VERSION;
	public static int CLIENT_ID;
	public static String RUNNING_PATH, FILE_STORAGE;
	
	public static void init() throws Exception {
		/*FIX THIS*/
		CLIENT_ID = 124834;
		VERSION = "alpha 0.1";
		RUNNING_PATH = new File(Build.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
		FILE_STORAGE = RUNNING_PATH + "/files";
		
		Log.write("Running from: " + RUNNING_PATH);
		Log.write("Storing files in: " + FILE_STORAGE);
		File fs = new File(FILE_STORAGE);
		if(!fs.exists()) {
			Log.write("Could not detect file container, creating...");
			fs.mkdirs();
		} 
	}

}
