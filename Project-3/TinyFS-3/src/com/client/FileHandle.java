package com.client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FileHandle {
	/* 
	 * A file handle contains a sequence of chunk handles and their respective chunkserver addresses 
	 * A file handle consists of a list of chunk handles. 
	 * */
	
	/* List of chunk handles */
	List<Chunk> mChunks;
	HashMap<String, List<String>> mChunkAddresses;

	String mFile;
	
	public FileHandle() {
		mChunks = new ArrayList<Chunk>();	
		mChunkAddresses = new HashMap<>();
	}
	
	public void setFile(String mFile) {
		this.mFile = mFile;
	}
	
	public String getFile() {
		return mFile;
	}
	
	public void setChunks(List<Chunk> mChunks) {
		this.mChunks = mChunks;
	}
	
	public void addChunk() {
		Chunk chunk = new Chunk(mFile + (mChunks.size()));
		mChunks.add(chunk);
		try {
			RandomAccessFile f = new RandomAccessFile(mFile, "rw");
			f.writeInt(chunk.getNumRecords());
			f.writeInt(chunk.getNumBytesFree());
			f.writeInt(chunk.getFreeOffset());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public TinyRec readFirstRecord() {
		if(mChunks.size() == 0) {
			return null;
		}
		boolean flag = false;
		int counter = 0;
		while(!flag) {
			if(counter == mChunks.get(0).getRecords().size()) {
				return null;
			}
			if(mChunks.get(0).getRecords().get(counter).isDeleted()) {
				counter++;
			}
			else {
				flag = true;
			}
		}
		
		return mChunks.get(0).getRecords().get(counter);
	}
	
	public TinyRec readNextRecord(RID pivot) {
		if(mChunks.size() == 0) {
			return null;
		}
		if(pivot.getChunkHandle() == null) {
			return null;
		}
		
		List<TinyRec> records = mChunks.get(0).getRecords();
		String rid = pivot.getID();
		int index = Integer.valueOf(rid.substring(pivot.getChunkHandle().length(), rid.length()));
		
		if(records.indexOf(index) == (records.size()-1)) {
			return null;
		}
		
		boolean flag = false;
		int counter = index + 1;
		while(!flag) {
			if(counter == mChunks.get(0).getRecords().size()) {
				return null;
			}
			if(mChunks.get(0).getRecords().get(counter).isDeleted()) {
				counter++;
			}
			else {
				flag = true;
			}
		}
		
		return records.get(counter);
	}
	
	public boolean appendRecord(byte[] payload, RID RecordID) {
		
		if(mChunks.size() == 0) {
			addChunk();
		}
		Chunk chunk = mChunks.get(mChunks.size() - 1);
		
		//Check if we can fit the record in this chunk
		int freeBytes = chunk.getNumBytesFree();
		if(payload.length > freeBytes) {
			//implement later lol
		}
		
		TinyRec record = new TinyRec();
		
		byte[] revisedPayload = new byte[payload.length + 1];
		revisedPayload[0] = 1; //an alive record
		//revisedPayload[1] = 1; //not a sub record
		for(int i = 0; i < payload.length; i++) {
			revisedPayload[i+1] = payload[i];
		}
		
		RecordID.setID(chunk.getChunkHandle() + (chunk.getNumRecords() + 1));
		RecordID.setID(chunk.getChunkHandle());
		record.setPayload(revisedPayload);
		record.setRID(RecordID);
		
		chunk.addRecord(record, revisedPayload.length);
		
		try {
			RandomAccessFile f = new RandomAccessFile(mFile, "rw");
			f.writeInt(revisedPayload.length);
			f.write(revisedPayload);
			f.close();
		} catch (IOException e) {
			return false;
		}
		
		return true;
	}
	
	public boolean deleteRecord(RID RecordID) {
		if(mChunks.size() == 0) {
			return false;
		}
		
		if(RecordID.getChunkHandle() == null) {
			return false;
		}
		
		List<TinyRec> records = mChunks.get(0).getRecords();
		String rid = RecordID.getID();
		int index = Integer.valueOf(rid.substring(RecordID.getChunkHandle().length(), rid.length()));
		
		TinyRec record = records.get(index);
		record.delete();
		
		return true;
	}
	
	
}
