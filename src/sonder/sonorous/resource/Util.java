package sonder.sonorous.resource;

import java.util.LinkedList;

public class Util {
	
	public static Object findBestCollision(LinkedList<?> l1, LinkedList<?> l2) {
		for(Object o : l1) {
			if(l2.contains(o)) {
				return o;
			}
		}
		
		for(Object o2 : l2) {
			if(l1.contains(o2)) {
				return o2;
			}
		}
		
		return null;
	}

}
