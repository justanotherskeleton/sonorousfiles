package sonder.sonorous.resource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

import org.apache.commons.codec.binary.Base64;
import org.jasypt.util.text.BasicTextEncryptor;

public class Crypto {
	
	public static String sha256(String input) throws Exception {
		MessageDigest digest = MessageDigest.getInstance("SHA-256");
		digest.update(input.getBytes("UTF-8"));
		byte[] hash = digest.digest();
		return Base64.encodeBase64String(hash);
	}
	
	//base64 output
	public static String encrypt(String input, String key) {
		BasicTextEncryptor enc = new BasicTextEncryptor();
		enc.setPassword(key);
		return enc.encrypt(input);
	}
	
	//plain text output
	public static String decrypt(String input, String key) {
		BasicTextEncryptor enc = new BasicTextEncryptor();
		enc.setPassword(key);
		return enc.decrypt(input);
	}
	
	public static byte[] encryptRSA(byte[] inpBytes, PublicKey key,
		  String xform) throws Exception {
		    Cipher cipher = Cipher.getInstance(xform);
		    cipher.init(Cipher.ENCRYPT_MODE, key);
		    return cipher.doFinal(inpBytes);
	}
	public static byte[] decryptRSA(byte[] inpBytes, PrivateKey key,
		  String xform) throws Exception{
		Cipher cipher = Cipher.getInstance(xform);
		cipher.init(Cipher.DECRYPT_MODE, key);
		return cipher.doFinal(inpBytes);
	}
	
	public static String[] generateRSA() throws Exception {
		KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
	    kpg.initialize(512); // 512 is the keysize.
	    KeyPair kp = kpg.generateKeyPair();
	    PublicKey pubk = kp.getPublic();
	    PrivateKey prvk = kp.getPrivate();
	    String publicKey = new String(Base64.encodeBase64(pubk.getEncoded()));
	    String privateKey = Base64.encodeBase64String(prvk.getEncoded());
	    return new String[] { publicKey, privateKey };
	}

}
