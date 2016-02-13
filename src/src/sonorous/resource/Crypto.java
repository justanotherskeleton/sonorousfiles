package src.sonorous.resource;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.jasypt.util.binary.BasicBinaryEncryptor;
import org.jasypt.util.text.BasicTextEncryptor;

public class Crypto {
	
	static MessageDigest md;
	static BasicTextEncryptor en;
	static BasicBinaryEncryptor eb;
	static BasicBinaryEncryptor eb_global;
	
	public static void init() throws Exception {
		md = MessageDigest.getInstance("SHA-256");
		en = new BasicTextEncryptor();
		eb = new BasicBinaryEncryptor();
		eb_global = new BasicBinaryEncryptor();
		Log.write("Initialized cryptography!");
	}
	
	public static String hash(String in) throws Exception {
		md.update(in.getBytes("UTF-8"));
		return new String(Base64.encodeBase64(md.digest()), Charset.forName("UTF-8"));
	}
	
	public static String encrypt_text(String in, String key) {
		en.setPassword(key);
		return en.encrypt(in);
	}
	
	public static String decrypt_text(String in, String key) {
		en.setPassword(key);
		return en.decrypt(in);
	}
	
	public static byte[] encrypt_data(byte[] in, String key) {
		eb.setPassword(key);
		return eb.encrypt(in);
	}
	
	public static byte[] decrypt_data(byte[] in, String key) {
		eb.setPassword(key);
		return eb.decrypt(in);
	}
	
	public static byte[] encrypt_data_global(byte[] in) {
		return eb_global.encrypt(in);
	}
	
	public static byte[] decrypt_data_global(byte[] in) {
		return eb_global.decrypt(in);
	}
	
	public static void setBE_global(String key) {
		eb_global.setPassword(key);
	}

}
