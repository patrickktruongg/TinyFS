package network;
import java.io.Serializable;

public class ChunkMessage implements Serializable {
	public static final long serialVersionUID = 1;
	
	private String name;
	private String chunkHandle;
	private int offset;
	private int NumberOfBytes;
	private byte[] payload;
	public ChunkMessage(String name, String chunkHandle, int offset) {
		this.name = name;
		this.chunkHandle = chunkHandle;
		this.offset = offset;
		NumberOfBytes = -1;
		payload = null;
	}
	
	public String getName() {
		return name;
	}
	
	public String getChunkHandle() {
		return chunkHandle;
	}
	
	public int getOffset() {
		return offset;
	}
	
	public int getNumberOfBytes() {
		return NumberOfBytes;
	}
	
	public byte[] getPayload() {
		return payload;
	}
	
	public void setChunkHandle(String chunkHandle) {
		this.chunkHandle = chunkHandle;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	public void setNumberOfBytes(int NumberOfBytes) {
		this.NumberOfBytes = NumberOfBytes;
	}
	
	public void setPayload(byte[] payload) {
		this.payload = payload;
	}
}