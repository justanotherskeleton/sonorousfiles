package sonder.sonorous.resource;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;

public class Crypto {
	
	public static String sha256(String input) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(input.getBytes("UTF-8"));
		byte[] hash = digest.digest();
		return Base64.encodeBase64String(hash);
	}
	
	public void encrypt(String input) {
		
	}
	
	public void decrypt(String input) {
		
	}

}
