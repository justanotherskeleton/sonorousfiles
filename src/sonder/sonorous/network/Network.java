package sonder.sonorous.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import sonder.sonorous.resource.Log;

public class Network {
	
	public static final int TCP_PORT = 59565;
	public static String PUBLIC_IP = null;
	
	public static void init() throws Exception {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		PUBLIC_IP = in.readLine();
		Log.write("Initialized network!");
	}

}
