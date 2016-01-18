package sonder.sonorous.resource;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Log {
	
	public static void write(String output) {
		System.out.println("[" + Log.getTimestamp() + "] " + output);
	}
	
	public static String getTimestamp() {
		return new String(new SimpleDateFormat("HH.mm.ss").format(new Date()));
	}
	
	public static String getFullTimestamp() {
		return new String(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
	}

}
