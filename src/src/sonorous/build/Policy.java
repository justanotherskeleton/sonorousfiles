package src.sonorous.build;

import java.io.File;

import com.esotericsoftware.kryo.Kryo;
import src.sonorous.network.*;
import src.sonorous.resource.Log;

public class Policy {
	
	public static boolean DELETE_OLD_FILES = false;
	public static int FILE_CRYPTO_BUFFER = 8192;
	
	public static final byte CONTINUE_TRANSFER = 0x00000A;
	public static final byte UNAUTHORIZED_CLIENT = 0x000001;
	public static final byte FP_MISMATCH = 0x00002A;
	public static final byte TRANSFER_END_CONFIRM = 0x00002B;
	public static final byte FP_RECEIVED_BEGIN = 0x00002B;
	
	public static final String VERSION = "alpha0.1";
	public static String TRANSFER_LOCATION;
	public static String WORKING_DIRECTORY;
	
	public static void init() throws Exception {
		WORKING_DIRECTORY = new File(Policy.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
		TRANSFER_LOCATION = WORKING_DIRECTORY + "/stor/transfers/";
		Log.write("Policy initialized!");
	}
	
	public static void initSharedKryo(Kryo passed) {
		passed.register(CTContinue.class);
		passed.register(CTEnd.class);
		passed.register(FilePush.class);
		passed.register(FileRequest.class);
		passed.register(FileSegment.class);
		passed.register(ServerReflect.class);
	}

}
