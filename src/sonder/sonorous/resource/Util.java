package sonder.sonorous.resource;

import java.util.Random;

public class Util {
	
	public static int generateID() {
		return 0 + new Random().nextInt(999999 - 0 + 1);
	}

}
