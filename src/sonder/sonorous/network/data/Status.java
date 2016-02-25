package sonder.sonorous.network.data;

public class Status {
	
	public static Status FT_RECEIVED = new Status((byte) 0x00001);
	public static Status FR_RECEIVED = new Status((byte) 0x00002);
	public static Status TRANSFER_CONTINUE = new Status((byte) 0x00003);
	
	public byte id;
	
	public Status(byte id) {
		this.id = id;
	}

}
