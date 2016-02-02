package src.sonorous.resource;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.ArrayList;

public class FileUtil {
	
	private static MessageDigest md;
	
	public static void init() throws Exception {
		md = MessageDigest.getInstance("MD5");
	}
	
	public static String getHash(File in) throws Exception {
		FileInputStream fis = new FileInputStream(in);
		String md5 = org.apache.commons.codec.digest.DigestUtils.md5Hex(fis);
		fis.close();
		return md5;
	}
	
	public static void listf(String directoryName, ArrayList<File> files) {
	    File directory = new File(directoryName);

	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	            listf(file.getAbsolutePath(), files);
	        }
	    }
	}

}
