package sonder.sonorous.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class Network {
	
	public static final int TCP_PORT = 59565;
	public static final int TCP_PORT_SECONDARY = 59566;
	public static final int TCP_PORT_BACKUP = 59567;
	public static String PUBLIC_IP = null;
	
	public static void init() throws Exception {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		PUBLIC_IP = in.readLine();
	}

}
