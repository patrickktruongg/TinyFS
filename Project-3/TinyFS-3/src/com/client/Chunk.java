package com.client;

import java.util.ArrayList;
import java.util.List;

import com.chunkserver.ChunkServer;

public class Chunk {
	
	List<TinyRec> mRecords;
	int mNumRecords;
	int mNumBytesFree;
	int mFreeOffset;
	String mChunkHandle;
	
	public Chunk(String mChunkHandle) {
		this.mChunkHandle = mChunkHandle;
		mRecords = new ArrayList<TinyRec>();
		mNumRecords = 0;
		mNumBytesFree = ChunkServer.ChunkSize - 12;
		mFreeOffset = 12;
	}
	
	public void addRecord(TinyRec record, int size) {
		mRecords.add(record);
		mNumRecords++;
		mNumBytesFree -= size;
		mFreeOffset += size;
	}
	
	public List<TinyRec> getRecords(){
		return mRecords;
	}
	
	public int getNumRecords() {
		return mNumRecords;
	}
	
	public int getNumBytesFree() {
		return mNumBytesFree;
	}
	
	public int getFreeOffset() {
		return mFreeOffset;
	}
	
	public String getChunkHandle() {
		return mChunkHandle;
	}
	
	public void setNumRecords(int mNumRecords) {
		this.mNumRecords = mNumRecords;
	}
	
	public void setNumBytesFree(int mNumBytesFree) {
		this.mNumBytesFree = mNumBytesFree;
	}
	
	public void setFreeOffset(int mFreeOffset) {
		this.mFreeOffset = mFreeOffset;
	}
	
}
