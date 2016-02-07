package src.sonorous.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;
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
		File zip_file = new File(String.valueOf(code) + ".son");
		zip_file.createNewFile();
		ZipFile zf = new ZipFile(zip_file);
		ZipParameters parameters = new ZipParameters();
		parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
		parameters.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
		
		for(File f : files) {
			zf.addFile(f, parameters);
		}

		if(!zip_file.exists()) {
			Log.write("Error occured while writing zip file");
			return;
		}
		
		Log.write("Files compressed, encrypting zip!");
		FileInputStream fis = new FileInputStream(zip_file);
		File en_file = new File(zip_file.getName() + "-en.son");
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
		zip_file.delete();
		Log.write("Encryption done, ecrypted zip has been deleted!");
		Log.write("Final compressed size: " + en_file.length());
		Log.write("Encryption operation complete!");
	}
	
	public void decryptFiles(File son, File output, String key) throws Exception {
		File de_file = new File(son.getName() + "-de.son");
		de_file.createNewFile();
		FileInputStream fis = new FileInputStream(son);
		FileOutputStream fos = new FileOutputStream(de_file);
		Crypto.setBE_global(key);
		
		Log.write("Starting decrypt operation on '" + son.getName() + "'");
		int buffer = Policy.FILE_CRYPTO_BUFFER;
		byte[] in = new byte[buffer];
		int read;
		while ((read = fis.read(in)) != -1) {
			byte[] out = Crypto.decrypt_data_global(in);
			if (out != null)
				fos.write(out);
		}
		
		Log.write("Files decrypted, extracting to '" + output.getName() + "'");
		ZipFile zf = new ZipFile(de_file);
		zf.extractAll(output.getAbsolutePath());
		
		Log.write("Extracted all decrypted files!"); 
	}
	
	public static void writeToFile(byte[] data, FileOutputStream fos) {
		try {
			fos.write(data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
