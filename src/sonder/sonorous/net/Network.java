package sonder.sonorous.net;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.LinkedList;

import sonder.sonorous.resource.Log;

public class Network {
	
	public static final int TCP_PORT = 59565;
	public static final int TCP_PORT_SECONDARY = 59566;
	public static final int TCP_PORT_BACKUP = 59567;
	public static final int TCP_PORT_4 = 59568;
	public static final int TCP_PORT_5 = 59569;
	public static final int TCP_PORT_6 = 59570;
	public static final int RESERVED_COMMS = 59500;
	public static String PUBLIC_IP = null;
	public static boolean TCP_1_AVALIBLE = false;
	public static boolean TCP_2_AVALIBLE = false;
	public static boolean TCP_3_AVALIBLE = false;
	public static boolean TCP_4_AVALIBLE = false;
	public static boolean TCP_5_AVALIBLE = false;
	public static boolean TCP_6_AVALIBLE = false;
	
	public static void init() throws Exception {
		URL whatismyip = new URL("http://checkip.amazonaws.com");
		BufferedReader in = new BufferedReader(new InputStreamReader(
		                whatismyip.openStream()));

		PUBLIC_IP = in.readLine();
	}
	
	public static int nextAvaliblePort() {
		if(TCP_1_AVALIBLE) {
			return TCP_PORT;
		} else if(TCP_2_AVALIBLE) {
			return TCP_PORT_SECONDARY;
		} else if(TCP_3_AVALIBLE) {
			return TCP_PORT_BACKUP;
		} else if(TCP_4_AVALIBLE) {
			return TCP_PORT_4;
		} else if(TCP_5_AVALIBLE) {
			return TCP_PORT_5;
		} else if(TCP_6_AVALIBLE) {
			return TCP_PORT_6;
		} else {
			Log.write("NO PORTS AVALIBLE!");
			return 0;
		}
	}
	
	public static void markTaken(int port) {
		if(port == TCP_PORT) {
			TCP_1_AVALIBLE = false;
		} else if(port == TCP_PORT_SECONDARY) {
			TCP_2_AVALIBLE = false;
		} else if(port == TCP_PORT_BACKUP) {
			TCP_3_AVALIBLE = false;
		} else if(port == TCP_PORT_4) {
			TCP_4_AVALIBLE = false;
		} else if(port == TCP_PORT_5) {
			TCP_5_AVALIBLE = false;
		} else if(port == TCP_PORT_6) {
			TCP_6_AVALIBLE = false;
		} else {
			Log.write("Cannot mark specified port");
		}
	}
	
	public static void markAvalible(int port) {
		if(port == TCP_PORT) {
			TCP_1_AVALIBLE = true;
		} else if(port == TCP_PORT_SECONDARY) {
			TCP_2_AVALIBLE = true;
		} else if(port == TCP_PORT_BACKUP) {
			TCP_3_AVALIBLE = true;
		} else if(port == TCP_PORT_4) {
			TCP_4_AVALIBLE = true;
		} else if(port == TCP_PORT_5) {
			TCP_5_AVALIBLE = true;
		} else if(port == TCP_PORT_6) {
			TCP_6_AVALIBLE = true;
		} else {
			Log.write("Cannot mark specified port");
		}
	}
	
	public static LinkedList<Integer> allPorts() {
		LinkedList<Integer> li = new LinkedList<Integer>();
		li.add(TCP_PORT);
		li.add(TCP_PORT_SECONDARY);
		li.add(TCP_PORT_BACKUP);
		li.add(TCP_PORT_4);
		li.add(TCP_PORT_5);
		li.add(TCP_PORT_6);
		return li;
	}
	
	public static LinkedList<Integer> allAvaliblePorts() {
		LinkedList<Integer> li = new LinkedList<Integer>();
		if(TCP_1_AVALIBLE) {
			li.add(TCP_PORT);
		}
		
		if(TCP_2_AVALIBLE) {
			li.add(TCP_PORT_SECONDARY);
		}
		
		if(TCP_3_AVALIBLE) {
			li.add(TCP_PORT_BACKUP);
		}
		
		if(TCP_4_AVALIBLE) {
			li.add(TCP_PORT_4);
		}
		
		if(TCP_5_AVALIBLE) {
			li.add(TCP_PORT_5);
		}
		
		if(TCP_6_AVALIBLE) {
			li.add(TCP_PORT_6);
		}
		
		return li;
	}

}
