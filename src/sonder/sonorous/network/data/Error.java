package sonder.sonorous.network.data;

public class Error {
	
	public static Error TRANSFER_ID_TAKEN = new Error((byte) 0x00001);
	public static Error REQUESTED_FILE_404 = new Error((byte) 0x00002);
	public static Error INVALID_TRANSFER_ID = new Error((byte) 0x00003);
	
	public byte id;
	
	public Error(byte id) {
		this.id = id;
	}

}
