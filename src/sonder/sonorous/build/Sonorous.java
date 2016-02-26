package sonder.sonorous.build;

import java.io.File;
import java.util.Scanner;

import sonder.sonorous.network.FileClient;
import sonder.sonorous.network.FileServer;
import sonder.sonorous.network.Network;

public class Sonorous {
	
	public boolean RUN = true;
	public FileClient client;
	public FileServer server;
	
	public static void main(String[] args) throws Exception {
		Network.init();
		Build.init();
		System.out.println(Build.RUNNING_PATH);
		//Sonorous main = new Sonorous();
		//main.cli();
	}
	
	public void cli() throws Exception {
		Scanner scan = new Scanner(System.in);
		while(RUN) {
			String s = scan.nextLine();
			String[] split = s.split(" ");
			
			if(s.startsWith("start")) {
				if(split[1].equalsIgnoreCase("client")) {
					client = new FileClient();
				} else if(split[1].equalsIgnoreCase("server")) {
					server = new FileServer();
				}
			}
			
			if(s.startsWith("connect")) {
				String ip = split[1];
				client.connect(ip);
			}
			
			if(s.startsWith("upload")) {
				String dir = split[1];
				File ul = new File(dir);
				
			}
		}
		scan.close();
	}

}
