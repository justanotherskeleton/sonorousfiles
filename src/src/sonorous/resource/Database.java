package src.sonorous.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import src.sonorous.build.Policy;
import src.sonorous.network.FileRequest;

public class Database {
	
	//array[0] = files, array[1] = hashes
	public static HashMap<Integer, String>[] getHashes(File folder) throws Exception {
		Log.write("Gathering hashes from '" + folder.getAbsolutePath() + "'");
		HashMap<Integer, String> _FILES = new HashMap<Integer, String>();
		HashMap<Integer, String> _HASHES = new HashMap<Integer, String>();
		ArrayList<File> files = new ArrayList<File>();
		Log.write("Indexing folder...");
		FileUtil.listf(folder.getAbsolutePath(), files);
		
		Log.write("Indexing complete, " + files.size() + " files found. Beginning hash calculation...");
		int count = 0;
		for(File f : files) {
			_FILES.put(count, f.getAbsolutePath());
			_HASHES.put(count, FileUtil.getHash(f));
			count++;
		}
		
		Log.write("Hashing operation has completed!");
		HashMap<Integer, String>[] returnValue = (HashMap<Integer, String>[]) new HashMap[2];
		returnValue[0] = _FILES;
		returnValue[1] = _HASHES;
		return returnValue;
	}
	
	public static void exportHashData(HashMap<Integer, String>[] data, File output) throws Exception {
		if(!output.exists()) {
			output.createNewFile();
		}
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(output));
		Log.write("Writing hash data to file supplied...");
		bw.write("::SONOROUS-HASH-DATABASE::");
		
		int c = data[0].size();
		for (int i = 0; i < c; i++) {
	        bw.write(data[0].get(i) + ":::" + data[1].get(i));
	    }
		
		bw.write("::END SONOROUS HASH DATABASE::");
		bw.close();
	}
	
	//str1: FILE PATH, str2: FILE HASH
	public static HashMap<String, String> importHashData(File db) throws Exception {
		Log.write("Importing hash data from file...");
		BufferedReader br = new BufferedReader(new FileReader(db));
		HashMap<String, String> map = new HashMap<String, String>();
		
		String line;
	    while ((line = br.readLine()) != null) {
	        String[] sub = line.split(":::");
	        map.put(sub[0], sub[1]);
	    }
	    
	    Log.write("Successfully imported " + map.size() + " lines of hash data!");
	    br.close();
	    return map;
	}
	
	public static LinkedList<FileRequest> updateFiles(HashMap<String, String> newData, HashMap<String, String> oldData) {
		int new_size = newData.size();
		int old_size = oldData.size();
		
		if(new_size > old_size) {
			Log.write("More files have been added since the last sync!");
		} else if(new_size < old_size) {
			Log.write("More files have been deleted since the last sync!");
		} else if(new_size == old_size) {
			Log.write("File index is the same size!");
		}
		
		LinkedList<FileRequest> outOfSyncFiles = new LinkedList<FileRequest>();
		
		Iterator it = newData.entrySet().iterator();
		while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        if(oldData.containsKey(pair.getKey())) {
	        	if(oldData.get(pair.getKey()).equals(pair.getValue())) {
	        		//same data, ignore file
	        	} else {
	        		//different data, sync new revision
	        		String new_hash_v = (String) pair.getValue();
	        		outOfSyncFiles.add(new FileRequest(new_hash_v));
	        		Log.write("File collision: '" + pair.getKey() + "', syncing new hash");
	        	}
	        } else {
	        	Log.write("New file detected: " + pair.getKey());
	        	outOfSyncFiles.add(new FileRequest((String) pair.getValue()));
	        }
	        it.remove();
	    }
		
		Iterator it2 = oldData.entrySet().iterator();
		while(it2.hasNext()) {
			Map.Entry pair = (Map.Entry)it2.next();
			if(newData.containsKey(pair.getKey())) {
				//new data contains an old file, ignore
			} else {
				Log.write("File not found in new data, '" + pair.getKey() + "'");
				
				if(Policy.DELETE_OLD_FILES) {
					new File((String) pair.getKey()).delete();
				}
			}
		}
		
		return outOfSyncFiles;
	}

}
