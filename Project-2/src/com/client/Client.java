package com.client;

import java.io.BufferedReader;
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
	//public static ChunkServer cs;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private BufferedReader br;
	Socket s;
	
	/**
	 * Initialize the client
	 */
	public Client(int port, String hostname){
		//cs = new ChunkServer(port);
		//System.out.println("Starting client");
		s = null;
		try {
			s = new Socket(hostname, port);
			oos = new ObjectOutputStream(s.getOutputStream());
			ois = new ObjectInputStream(s.getInputStream());
			System.out.println("Connected!");
			
		} catch (IOException ioe) {
			System.out.println("Unable to connect to server with provided fields");
		}
	}
	
	/**
	 * Create a chunk at the chunk server from the client side.
	 */
	public String initializeChunk() {
		ChunkMessage message = new ChunkMessage("initializeChunk", "", 0);
		try {
			oos.writeObject(message);
			oos.flush();
			message = (ChunkMessage)ois.readObject();
		} catch (IOException e) {
			System.out.println("I/O Exception in initializeChunk");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
			e.printStackTrace();
		}
		return message.getChunkHandle();
	}
	
	/**
	 * Write a chunk at the chunk server from the client side.
	 */
	public boolean putChunk(String ChunkHandle, byte[] payload, int offset) {
		if(offset + payload.length > ChunkServer.ChunkSize){
			System.out.println("The chunk write should be within the range of the file, invalide chunk write!");
			return false;
		}
		ChunkMessage message = new ChunkMessage("putChunk", ChunkHandle, offset);
		message.setPayload(payload);
		try {
			oos.writeObject(message);
			oos.flush();
			message = (ChunkMessage)ois.readObject();
			if(message.getName().equals("true")) {
				return true;
			}
			else {
				return false;
			}
		} catch (IOException e) {
			System.out.println("I/O Exception in putChunk");
			e.printStackTrace();
			return false;
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
			e.printStackTrace();
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
		ChunkMessage message = new ChunkMessage("getChunk", ChunkHandle, offset);
		message.setNumberOfBytes(NumberOfBytes);
		try {
			oos.writeObject(message);
			oos.flush();
			message = (ChunkMessage)ois.readObject();
			
		} catch (IOException e) {
			System.out.println("I/O Exception in getChunk");
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			System.out.println("Class Not Found Exception");
			e.printStackTrace();
		}
		return message.getPayload();
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
