package com.client;

public class TinyRec {
	private byte[] payload = null;
	private RID ID = null;
	
	public byte[] getPayload() {
		return payload;
	}
	public void setPayload(byte[] p) {
		this.payload = p;
	}
	
	public RID getRID() {
		return ID;
	}
	public void setRID(RID inputID) {
		ID = inputID;
	}
	
	public boolean isDeleted() {
		if(payload[0] == 0) {
			return true;
		}
		return false;
	}
	
	public void delete() {
		payload[0] = 0;
	}
	
	public void undoDelete() {
		payload[0] = 1;
	}
}
