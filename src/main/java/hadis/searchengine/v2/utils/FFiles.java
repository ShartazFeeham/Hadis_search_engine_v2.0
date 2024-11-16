package hadis.searchengine.v2.utils;

import java.io.*;
import java.nio.file.*;
public class FFiles {

	/* @author: SzYr FEEHAM
	 * There are 3 types of methods with different operations:
	 * 	
	 * >>Basic
	 * 		createFolder() - call with path as parameter to create a folder.
	 * 		createFile() - call with path and format as parameter to create a file.
	 * 		ifExists() - check if a folder or files exists or not. 
	 * 		rename() - call with path and new name as parameter to rename a file or folder.
	 * 		delete() - call with path as parameter to delete a file or folder.
	 * 	
	 * >>CopyPaste
	 * 		copy() - copy a folder (it doesn't paste automatically, must call paste after it.)
	 * 		cut() - cut a folder (it doesn't paste automatically, must call paste after it.)
	 * 		paste() - call with path as parameter where you want to paste the copied or cutted file/folder.
	 * 
	 * >>Use
	 * 		read() - read String data from a file.
	 * 		changeData() - replace files' existing data with some new data.
	 * 		addTo() - add some data after files' already existing data.
	 * 		createAndWrite() - create a file and put some data into it in a single call.
	 */
	
	private static String f1;
	private static boolean del=false;
	
	///////////////////////////////////////////
	
	//Create folder
	public static boolean createFolder(String filePath) {
		if(new File(filePath).mkdir())
			return true;
		else return false;
	}
	
	//Create a file with any name and format.
	public static int create(String fileNameWithPath) {
		try 
		{
			boolean fvar = new File(fileNameWithPath).createNewFile();
			if (fvar) return 1; //returns 1, if file operation was successful.
			else return -1; //returns -1, if there was an existing file with that name.
		} 
		catch(IOException e) { return 0; /*returns 0, if there is an error.*/ }
	}
	
	//Check if a File or path exists or not.
	public static boolean ifExists(String Path) {
		if(new File(Path).exists())
			return true;
		else return false;
	}
	
	//Rename file
	public static boolean rename(String Path, String newName) {
		String Path2="";
		for(int i=Path.length()-1; i>0; i--) {
			if(Path.charAt(i)=='\\') {
				if(Path.substring(i+1,Path.length()).toLowerCase().equals(newName.toLowerCase()))
					return false;
				Path2 = Path.substring(0,i)+"\\"+newName;
				break;
			}
		}
		File f = new File(Path);
		if(!f.exists()) 
			return false;
		if(f.isFile()) 
			return f.renameTo(new File(Path2));
		else {
			File f2 = new File(Path2);
			f2.mkdir();
			copy(Path);
			paste(Path2);
			delete(Path);
		}
		return true;
	}

	//Delete a file.
	public static boolean delete(String filePath) {
		File F = new File(filePath);
		if(F.isFile()) {
			if(F.delete())
				return true;
			else return false;
		}
		else {
			myRecursiveDelete(F);
			return true;
		}
	}
	private static void myRecursiveDelete(File F) {
		String [] entries = F.list();
		for(String item: entries) {
			File toDelete = new File(F.getPath(),item);
			toDelete.delete();
			if(toDelete.exists()) {
				myRecursiveDelete(toDelete);
				toDelete.delete();
			}
		}
		F.delete();
	}
	
	///////////////////////////////////////////
	
	//Copy a file
	public static void copy(String sourcePath) {
		f1 = sourcePath;
	}
	
	//Cut a file.
	public static void cut(String sourcePath) {
		f1 = sourcePath;
		del=true;
	}
	
	//Paste the copied file.
	public static boolean paste(String destination) {
		String source = f1;
		File src = new File(source);
		File dest = new File(destination);
		if(!src.exists()) return false;
		   if(!src.isFile()){
		    	try {
		    		copyFolder(src, dest);
		    	} 
		    		catch(IOException e) { return false; }
		    	}
		    else {
		    	for(int i=source.length()-1; i>0; i--)    
		    		if(source.charAt(i)=='\\') {
		    			destination = destination + source.substring(i,source.length());
		    			break;
		    		}
		    	dest = new File(destination);
		    	try {
		    		InputStream in = new FileInputStream(src);
		        	OutputStream out = new FileOutputStream(dest);
		        	byte[] buffer = new byte[1024];
		        	int length;
		        	while ((length = in.read(buffer)) > 0)
		        	    out.write(buffer, 0, length);
		        	in.close();
		        	out.close();
		    	}
		    	catch(IOException e) { return false; }
		    }
		if(del){
			delete(f1);
			del=false;
		}
		return true;
	}
	private static void copyFolder(File src, File dest) throws IOException{
	    	if(src.isDirectory()){
	    		if(!dest.exists()){
	    		   dest.mkdir();
	    		}
	    		String files[] = src.list();
	    		for (String file : files) {
	    		   File srcFile = new File(src, file);
	    		   File destFile = new File(dest, file);
	    		   copyFolder(srcFile,destFile);
	    		}
	    	}
	    	else{
	    		InputStream in = new FileInputStream(src);
	    	    OutputStream out = new FileOutputStream(dest);
	    	    byte[] buffer = new byte[1024];
	    	    int length;
	    	    while ((length = in.read(buffer)) > 0)
	    	    	out.write(buffer, 0, length);
	    	    in.close();
	    	    out.close();
	    	}
	    }

	///////////////////////////////////////////
	
	//Read a file from any location.
	public static String read(String filePath) {
		try 
		{
			return new String(Files.readAllBytes(Paths.get(filePath)));
		}
		catch(IOException e) { return null; }
	}
	
	//Replace a files all data by saving new data.
	public static boolean changeData(String filePath, String data) {
		try 
		{
			Files.write(Paths.get(filePath), data.getBytes());
			return true;
		} 
		catch(IOException e) { return false; }
	}
	
	//Add some new data with files previously existing data.
	public static boolean addTo(String filePath, String data) {
		try 
		{
			Files.write(Paths.get(filePath), data.getBytes(), StandardOpenOption.APPEND);
			return true;
		} 
		catch(IOException e) { return false; }
	}
	
	//Crate a file and write data in it.
	public static boolean createAndWrite(String filePath, String data) {
		create(filePath);
		return changeData(filePath, data);
	}
}