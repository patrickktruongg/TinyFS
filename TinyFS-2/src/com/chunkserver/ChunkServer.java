package com.chunkserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.interfaces.ChunkServerInterface;

/**
 * implementation of interfaces at the chunkserver side
 * 
 * @author Shahram Ghandeharizadeh
 *
 */

public class ChunkServer implements ChunkServerInterface {
	final static String filePath = "C:\\Users\\shahram\\Documents\\TinyFS-2\\csci485Disk\\"; // or C:\\newfile.txt
	final static String filePath_ = "C::\\Users\\patricktruong\\Documents\\CS485\\TinyFS\\TinyFS-2\\";
	public static long counter;
	
	
	/**
	 * Initialize the chunk server
	 */
	public ChunkServer() {
		File file = new File(filePath_  + "chunkserver.txt");
		
		//Create the file
		try {
			if (file.createNewFile()){
				//Creating the file
				//Write Content
				FileWriter writer = new FileWriter(file);
				counter = 0;
				writer.write("0");
				writer.close();
			} 
			else{
				//File already exists
				//Read file and set the counter to whatever is stored in it
	            FileReader fileReader = new FileReader(filePath_);
	            BufferedReader bufferedReader = new BufferedReader(fileReader);
	            String line = null;
	            if((line = bufferedReader.readLine()) != null) {
	                counter = Integer.parseInt(line);
	            }   
	            else {
	            		counter = 0;
	            		System.out.println("Error: File is empty, counter could not be set.");
	            }

	            bufferedReader.close();    
				
			}
		} catch(IOException e) {
			
		} 
		
	}

	/**
	 * Each chunk corresponds to a file. Return the chunk handle of the last chunk
	 * in the file.
	 */
	public String initializeChunk() {
		String handle = "Chunk" + counter;
		File file = new File(filePath_ + handle);
		try {
			file.createNewFile();
		} catch (IOException e) {
			System.out.println("Exception creating new file");
			e.printStackTrace();
		}
		counter++;
		return handle;
	}

	/**
	 * Write the byte array to the chunk at the specified offset The byte array size
	 * should be no greater than 4KB
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		try (FileOutputStream fos = new FileOutputStream(filePath_ + ChunkHandle)) {
			fos.write(payload);
			fos.close();
		} catch(IOException e) {
			System.out.println("Exception putting in chunk");
			e.printStackTrace();
		}
		
		return true;
	}

	/**
	 * read the chunk at the specific offset
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		Path path = Paths.get(filePath_ + ChunkHandle);
		
		try {
			byte[] rawData = Files.readAllBytes(path);
			byte[] temp = new byte[NumberOfBytes];
			for(int i = 0; i < NumberOfBytes; i++) {
				temp[i] = rawData[i+offset];
			}
			return temp;
		} catch (IOException e) {
			System.out.println("Exception getting chunk");
			e.printStackTrace();
		}
		
		return null;
	}

}
