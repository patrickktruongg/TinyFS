package com.master;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Master {
	
	private static Master master = null;
	
	private HashMap<String, List<String>> mFileToChunks; 
	
	private Master() {
		mFileToChunks = new HashMap<>();
	}
	
	public static Master getInstance(){
        if(master == null){
            master = new Master();
        }
        return master;
    }
	
	public void addFile(String filename) {
		//Adds file to the namespace
		List<String> chunks = new ArrayList<String>();
		mFileToChunks.put(filename, chunks);
	}
	
	public void addChunk(String filename, String chunkhandle) {
		List<String> chunks = mFileToChunks.get(filename);
		chunks.add(chunkhandle);
		mFileToChunks.put(filename, chunks);
	}
	
	public List<String> getChunks(String filename) {
		return mFileToChunks.get(filename);
	}
	
	public String getChunkHandle(String filename, int chunkIndex) {
		List<String> chunks = mFileToChunks.get(filename);
		return chunks.get(chunkIndex);
	}
	
	public int getNumChunks(String filename) {
		return mFileToChunks.get(filename).size();
	}
	
	public boolean fileExists(String filename) {
		return mFileToChunks.containsKey(filename);
	}
	
	public int getNumFiles() {
		return mFileToChunks.size();
	}
}
