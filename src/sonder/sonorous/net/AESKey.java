package sonder.sonorous.net;

public class AESKey {
	
	/*
	 * Clients send each other their public keys
	 * This class is transfered using asymmetric encryption
	 * File transfer uses symmetric key in this class 
	 */
	
	public byte vCode;
	public String key;

}
