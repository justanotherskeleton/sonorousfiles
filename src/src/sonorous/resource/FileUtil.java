package src.sonorous.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import src.sonorous.build.Policy;

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
	
	public static long getSizeOfFiles(ArrayList<File> files) {
		long size = 0;
		for(File f : files) {
			size += f.length();
		}
		
		return size;
	}
	
	//Zips files then encrypts zip
	public static void encryptFiles(File folder, String key) throws Exception {
		if(!folder.exists()) {
			Log.write("Encrypt operation failed, file does not exist!");
			return;
		}
		
		if(!folder.isDirectory()) {
			Log.write("Directory supplied was not a folder!");
			return;
		}
		
		Crypto.setBE_global(key);
		int buffer = Policy.FILE_CRYPTO_BUFFER;
		Log.write("Starting encryption task on '" + folder.getAbsolutePath() + "', buffer:" + buffer + ".");
		
		Log.write("Indexing files...");
		ArrayList<File> files = new ArrayList<File>();
		listf(folder.getAbsolutePath(), files);
		long sizeKB = getSizeOfFiles(files);
		Log.write("Found " + files.size() + " files, " + sizeKB + " kilobytes");
		
		Log.write("Compressing files...");
		int code = new Random().nextInt((10000 - 100) + 1) + 100;
		String zip_file = String.valueOf(code) + ".zip";
		FileOutputStream fos1 = new FileOutputStream(zip_file);
		ZipOutputStream zos = new ZipOutputStream(fos1);
		
		for(File f : files) {
			addToZipFile(f, zos);
		}
		
		File zip = new File(zip_file);
		if(!zip.exists()) {
			Log.write("Error occured while writing zip file");
			return;
		}
		
		zos.close();
		fos1.close();
		Log.write("Files compressed, encrypting zip!");
		FileInputStream fis = new FileInputStream(zip);
		File en_file = new File(zip_file + "-en.zip");
		en_file.createNewFile();
		FileOutputStream fos = new FileOutputStream(en_file);
		
		byte[] in = new byte[buffer];
		int read;
		while ((read = fis.read(in)) != -1) {
			byte[] output = Crypto.encrypt_data_global(in);
			if (output != null)
				fos.write(output);
		}
		
		fis.close();
		fos.close();
		zip.delete();
		Log.write("Encryption done, ecrypted zip has been deleted!");
		Log.write("Final compressed size: " + en_file.length());
		Log.write("Encryption operation complete!");
	}
	
	//function credit to avajava
	public static void addToZipFile(File file, ZipOutputStream zos) throws Exception {

		Log.write("Writing '" + file.getName() + "' to zip file");

		FileInputStream fis = new FileInputStream(file);
		ZipEntry zipEntry = new ZipEntry(file.getAbsolutePath());
		zos.putNextEntry(zipEntry);

		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0) {
			zos.write(bytes, 0, length);
		}

		zos.closeEntry();
		fis.close();
	}

}
