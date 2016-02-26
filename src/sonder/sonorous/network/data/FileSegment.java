package sonder.sonorous.network.data;

public class FileSegment {
	
	public int id;
	public byte[] data;
	
	public FileSegment(int id, byte[] data) {
		this.id = id;
		this.data = data;
	}

}
