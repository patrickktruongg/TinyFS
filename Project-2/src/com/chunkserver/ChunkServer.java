package com.chunkserver;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Vector;

import com.interfaces.ChunkServerInterface;

import network.ChunkMessage;
import network.ServerThread;

/**
 * implementation of interfaces at the chunkserver side
 * @author Shahram Ghandeharizadeh
 *
 */



public class ChunkServer implements ChunkServerInterface {
	final static String filePath = "csci485//";	//or C:\\newfile.txt
	public static long counter;
	private static ServerSocket ss;
	
	/**
	 * Initialize the chunk server
	 */
	public ChunkServer(int port){
		File dir = new File(filePath);
		File[] fs = dir.listFiles();

		if(fs.length == 0){
			counter = 0;
		}else{
			long[] cntrs = new long[fs.length];
			for (int j=0; j < cntrs.length; j++)
				cntrs[j] = Long.valueOf( fs[j].getName() ); 
			
			Arrays.sort(cntrs);
			counter = cntrs[cntrs.length - 1];
		}
		
		ss = null;
		try {
			ss = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Port exception");
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Each chunk is corresponding to a file.
	 * Return the chunk handle of the last chunk in the file.
	 */
	public String initializeChunk() {
		counter++;
		return String.valueOf(counter);
	}
	
	/**
	 * Write the byte array to the chunk at the offset
	 * The byte array size should be no greater than 4KB
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		try {
			//If the file corresponding to ChunkHandle does not exist then create it before writing into it
			RandomAccessFile raf = new RandomAccessFile(filePath + ChunkHandle, "rw");
			raf.seek(offset);
			raf.write(payload, 0, payload.length);
			raf.close();
			return true;
		} catch (IOException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	/**
	 * read the chunk at the specific offset
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		try {
			//If the file for the chunk does not exist the return null
			boolean exists = (new File(filePath + ChunkHandle)).exists();
			if (exists == false) return null;
			
			//File for the chunk exists then go ahead and read it
			byte[] data = new byte[NumberOfBytes];
			RandomAccessFile raf = new RandomAccessFile(filePath + ChunkHandle, "rw");
			raf.seek(offset);
			raf.read(data, 0, NumberOfBytes);
			raf.close();
			return data;
		} catch (IOException ex){
			ex.printStackTrace();
			return null;
		}
	}
	
	public static void main(String [] args) {
		ChunkServer cs = new ChunkServer(5656);
		System.out.println("Server started!");
		while (true) {
			Socket s = null;
			ObjectOutputStream oos = null;
			ObjectInputStream ois = null;
			try {
				System.out.println("waiting for connection...");
				s = ss.accept();
				System.out.println("connection from " + s.getInetAddress());
				oos = new ObjectOutputStream(s.getOutputStream());
				ois = new ObjectInputStream(s.getInputStream());
			} catch (IOException e) {
				//e.printStackTrace();
			}		

			while(!s.isClosed()) {
				DataOutputStream dos = new DataOutputStream(oos);
				DataInputStream dis = new DataInputStream(ois);
				
				try {
					int length = dis.readInt();
					if(length>-1) {
						byte[] payload = null;
						if(length > 0) {
							payload = new byte[length];
						    dis.readFully(payload, 0, payload.length); // read the message
						}    
					    int code = dis.readInt();
					    if(code == 1) {
					    		int handleLength = dis.readInt();
					    		byte[] handle = new byte[handleLength];
					    		dis.readFully(handle, 0, handle.length);
					    		String chunkHandle = new String(handle);
					    		int offset = dis.readInt();
					    		boolean success = cs.putChunk(chunkHandle, payload, offset);
					    		if(success) {
					    			dos.writeInt(1);
					    			dos.flush();
					    		}
					    		else {
					    			dos.writeInt(0);
					    			dos.flush();
					    		}
					    }
					    else if(code == 2) {
					    		//String ChunkHandle, int offset, int NumberOfBytes
					    		int handleLength = dis.readInt();
					    		byte[] handle = new byte[handleLength];
					    		dis.readFully(handle, 0, handle.length);
					    		String chunkHandle = new String(handle);
					    		int offset = dis.readInt();
					    		int numberOfBytes = dis.readInt();
					    		byte[] value = cs.getChunk(chunkHandle, offset, numberOfBytes);
					    		dos.writeInt(value.length);
					    		dos.flush();
					    		dos.write(value);
					    		dos.flush();
					    }
					}
					else {
						String chunk = cs.initializeChunk();
						byte[] b = chunk.getBytes();
						dos.writeInt(b.length); // write length of the payload
						dos.write(b);           // write the payload
						dos.flush();
					}
					
				} catch (IOException e1) {
					//e1.printStackTrace();
					break;
				}
				
			}
		}
	}

}
