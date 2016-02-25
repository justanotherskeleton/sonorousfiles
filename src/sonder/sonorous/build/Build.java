package sonder.sonorous.build;

import java.io.File;

public class Build {
	
	public static final String VERSION = "alpha 0.1";
	public static String CLIENT_ID;
	public static String RUNNING_PATH;
	
	public static void init() throws Exception {
		CLIENT_ID = "alpha 0.1";
		RUNNING_PATH = new File(Build.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getParent();
	}

}
