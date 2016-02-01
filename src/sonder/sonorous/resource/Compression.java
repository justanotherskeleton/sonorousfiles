package sonder.sonorous.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Compression {
	
	//credit to http://www.mkyong.com/java/how-to-compress-files-in-zip-format/ for the java.util.zip portion
	
	List<String> fileList;
	private String SOURCE_FOLDER = "";
	@SuppressWarnings("unused")
	private String OUTPUT_ZIP_FILE = "";
	
	public Compression(File input, File output) {
		Log.write("Starting compression task for '" + input + "'");
		this.SOURCE_FOLDER = input.getAbsolutePath();
		this.OUTPUT_ZIP_FILE = output.getAbsolutePath();
		generateFileList(input);
		this.ZIP(output);
	}
	
	public void ZIP(File output) {
		byte[] buffer = new byte[1024];
    	
	     try{
	    		
	    	FileOutputStream fos = new FileOutputStream(output);
	    	ZipOutputStream zos = new ZipOutputStream(fos);
	    		
	    		
	    	for(String file : this.fileList){
	    			
	    		ZipEntry ze= new ZipEntry(file);
	        	zos.putNextEntry(ze);
	               
	        	FileInputStream in = 
	                       new FileInputStream(SOURCE_FOLDER + File.separator + file);
	       	   
	        	int len;
	        	while ((len = in.read(buffer)) > 0) {
	        		zos.write(buffer, 0, len);
	        	}
	               
	        	in.close();
	    	}
	    		
	    	zos.closeEntry();
	    	zos.close();
	          
	    } catch(IOException ex){
	       ex.printStackTrace();   
	    }
	}
	
	private String generateZipEntry(String file){
    	return file.substring(SOURCE_FOLDER.length()+1, file.length());
    }
	
	public void generateFileList(File node){

		if(node.isFile()){
			fileList.add(generateZipEntry(node.getAbsoluteFile().toString()));
		}
		
		if(node.isDirectory()){
			String[] subNote = node.list();
			for(String filename : subNote) {
				generateFileList(new File(node, filename));
			}
		}
	}

}
