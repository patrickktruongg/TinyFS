package com.client;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import com.chunkserver.ChunkServer;
import com.interfaces.ClientInterface;

import network.ChunkMessage;


/**
 * implementation of interfaces at the client side
 * @author Shahram Ghandeharizadeh
 *
 */
public class Client implements ClientInterface {
	private static ObjectInputStream ois;
	private static ObjectOutputStream oos;
	private static DataOutputStream dos;
	private static DataInputStream dis;
	static Socket s;
	
	/**
	 * Initialize the client
	 */
	public Client(int port, String hostname){
		s = null;
		try {
			s = new Socket(hostname, port);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			dos = new DataOutputStream(oos);
			dis = new DataInputStream(ois);
			System.out.println("Connected!");
			
		} catch (IOException ioe) {
			System.out.println("Unable to connect to server with provided fields");
		}
	}
	
	/**
	 * Create a chunk at the chunk server from the client side.
	 */
	public String initializeChunk() {
		String handle = "";
		try {
			dos.writeInt(-1);
			dos.flush();
			int length = dis.readInt();                  
			byte[] message = new byte[length];
			dis.readFully(message, 0, message.length);
			handle = new String(message);
			return handle;
		} catch (IOException e) {
			System.out.println("I/O Exception in initializeChunk");
			e.printStackTrace();
		}
		
		return handle;
	}
	
	/**
	 * Write a chunk at the chunk server from the client side.
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		if(offset + payload.length > ChunkServer.ChunkSize){
			System.out.println("The chunk write should be within the range of the file, invalide chunk write!");
			return false;
		}
		
		try {
			dos.writeInt(payload.length);
			dos.flush();
			dos.write(payload);
			dos.flush();
			byte[] chunk = ChunkHandle.getBytes();
			dos.writeInt(1);
			dos.flush();
			dos.writeInt(chunk.length);
			dos.flush();
			dos.write(chunk);
			dos.flush();
			
			dos.writeInt(offset);
			dos.flush();
			int success = dis.readInt();

			if(success == 1) {
				return true;
			}
			else {
				return false;
			}
		} catch (IOException e1) {
			return false;
		}
		
		
	}
	
	/**
	 * Read a chunk at the chunk server from the client side.
	 */
	public byte[] getChunk(String ChunkHandle, int offset, int NumberOfBytes) {
		if(NumberOfBytes + offset > ChunkServer.ChunkSize){
			System.out.println("The chunk read should be within the range of the file, invalide chunk read!");
			return null;
		}
		byte[] payload = null;
		try {
			dos.writeInt(0);
			dos.flush();
			dos.writeInt(2);
			byte[] chunk = ChunkHandle.getBytes();
			dos.writeInt(chunk.length);
			dos.flush();
			dos.write(chunk);
			dos.flush();
			
			dos.writeInt(offset);
			dos.flush();
			
			dos.writeInt(NumberOfBytes);
			dos.flush();
			
			int length = dis.readInt();
			payload = new byte[length];
			dis.readFully(payload, 0, payload.length);
			
			return payload;

		} catch (IOException e1) {
			return payload;
		}
		
	}
	
	public void closeSocket() {
		try {
			if (s != null) {
				s.close();
			}
		} catch (IOException ioe) {
			System.out.println("ioe: " + ioe.getMessage());
		}
	}

}
