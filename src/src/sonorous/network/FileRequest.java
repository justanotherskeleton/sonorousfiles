package src.sonorous.network;

public class FileRequest implements ServerReflect {
	
	public String FILE_HASH;
	
	public FileRequest(String hash) {
		this.FILE_HASH = hash;
	}

}
