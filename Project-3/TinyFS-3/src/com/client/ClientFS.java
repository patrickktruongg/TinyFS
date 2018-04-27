package com.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.chunkserver.ChunkServer;
import com.master.Master;

public class ClientFS {

	public enum FSReturnVals {
		DirExists, // Returned by CreateDir when directory exists
		DirNotEmpty, //Returned when a non-empty directory is deleted
		SrcDirNotExistent, // Returned when source directory does not exist
		DestDirExists, // Returned when a destination directory exists
		FileExists, // Returned when a file exists
		FileDoesNotExist, // Returns when a file does not exist
		BadHandle, // Returned when the handle for an open file is not valid
		RecordTooLong, // Returned when a record size is larger than chunk size
		BadRecID, // The specified RID is not valid, used by DeleteRecord
		RecDoesNotExist, // The specified record does not exist, used by DeleteRecord
		NotImplemented, // Specific to CSCI 485 and its unit tests
		Success, //Returned when a method succeeds
		Fail //Returned when a method fails
	}

	/**
	 * Creates the specified dirname in the src directory Returns
	 * SrcDirNotExistent if the src directory does not exist Returns
	 * DestDirExists if the specified dirname exists
	 *
	 * Example usage: CreateDir("/", "Shahram"), CreateDir("/Shahram/",
	 * "CSCI485"), CreateDir("/Shahram/CSCI485/", "Lecture1")
	 */
	public FSReturnVals CreateDir(String src, String dirname) {
		
		//Check if the directory src exists
		File dir = new File(src);
		if(!dir.exists()) {
			return FSReturnVals.SrcDirNotExistent;
		}
		
		//Check if the dirname exists and try to make one if not
		File file = new File(src + dirname);
		boolean success = file.mkdirs();
		if(!success) {
			return FSReturnVals.DestDirExists;
		}
		
		return FSReturnVals.Success;
	}

	/**
	 * Deletes the specified dirname in the src directory Returns
	 * SrcDirNotExistent if the src directory does not exist Returns
	 * DestDirExists if the specified dirname exists
	 *
	 * Example usage: DeleteDir("/Shahram/CSCI485/", "Lecture1")
	 */
	public FSReturnVals DeleteDir(String src, String dirname) {
		
		//Check if the directory src exists
		File srcFile = new File(src);
		if(!srcFile.exists()) {
			return FSReturnVals.SrcDirNotExistent;
		}
		
		//Check if dirname is empty, and if so delete otherwise through an error
		File dir = new File(src + dirname);		
		File[] fs = dir.listFiles();
		if(fs.length != 0) {
			return FSReturnVals.DirNotEmpty;
		}
		else {
			dir.delete();
		}
		
		return FSReturnVals.Success;
	}

	/**
	 * Renames the specified src directory in the specified path to NewName
	 * Returns SrcDirNotExistent if the src directory does not exist Returns
	 * DestDirExists if a directory with NewName exists in the specified path
	 *
	 * Example usage: RenameDir("/Shahram/CSCI485", "/Shahram/CSCI550") changes
	 * "/Shahram/CSCI485" to "/Shahram/CSCI550"
	 */
	public FSReturnVals RenameDir(String src, String NewName) {
		
		//Check if the directory src exists
		File srcFile = new File(src);
		if(!srcFile.exists()) {
			return FSReturnVals.SrcDirNotExistent;
		}
		else {
			//Check to see if NewName already exists in the parent directory as a file
			File parent = srcFile.getParentFile();
			File[] fs = parent.listFiles();
			for(int i = 0; i < fs.length; i++) {
				if(fs[i].getName().equals(NewName)) {
					return FSReturnVals.DestDirExists;
				}
			}
			
			//Rename file
			File newPath = new File(parent.getPath() + NewName);
			srcFile.renameTo(newPath);
		}
		
		return FSReturnVals.Success;
	}

	/**
	 * Lists the content of the target directory Returns SrcDirNotExistent if
	 * the target directory does not exist Returns null if the target directory
	 * is empty
	 *
	 * Example usage: ListDir("/Shahram/CSCI485")
	 */
	public String[] ListDir(String tgt) {
		
		File dir = new File(tgt);
		File[] fs = dir.listFiles();
		List<String> contents = new ArrayList<String>();
		if(!dir.exists() || fs.length == 0){
			return null;
		}else{
			//Read the contents of fs and then recursively read the children
			for (int i=0; i < fs.length; i++) {
				String temp = tgt + "/" + fs[i].getName();
				contents.add(temp); 
				String[] children = ListDir(temp);
				if(children != null) {
					for(int j = 0; j < children.length; j++) {
						contents.add(children[j]);
					}
				}
			}
			
			return contents.toArray(new String[contents.size()]);
		}
		
	}

	/**
	 * Creates the specified filename in the target directory Returns
	 * SrcDirNotExistent if the target directory does not exist Returns
	 * FileExists if the specified filename exists in the specified directory
	 *
	 * Example usage: Createfile("/Shahram/CSCI485/Lecture1/", "Intro.pptx")
	 */
	public FSReturnVals CreateFile(String tgtdir, String filename) {
		
		Master master = Master.getInstance();
		//Check if the directory src exists
		File dir = new File(tgtdir);
		if(!dir.exists()) {
			return FSReturnVals.SrcDirNotExistent;
		}
		else {
			/*
			File[] fs = dir.listFiles();
			for(int i = 0; i < fs.length; i++) {
				if(fs[i].getName().equals(filename)) {
					return FSReturnVals.FileExists;
				}
			}
			*/
			//Create file
			File file = new File(tgtdir + filename);
			try {
				if(file.createNewFile()) {
					master.addFile(tgtdir + filename);
					return FSReturnVals.Success;
				} else {
					return FSReturnVals.FileExists; 
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return FSReturnVals.FileExists;
			}
		}

	}

	/**
	 * Deletes the specified filename from the tgtdir Returns SrcDirNotExistent
	 * if the target directory does not exist Returns FileDoesNotExist if the
	 * specified filename is not-existent
	 *
	 * Example usage: DeleteFile("/Shahram/CSCI485/Lecture1/", "Intro.pptx")
	 */
	public FSReturnVals DeleteFile(String tgtdir, String filename) {
		//Check if the directory src exists
		File dir = new File(tgtdir);
		if(!dir.exists()) {
			return FSReturnVals.SrcDirNotExistent;
		}
		else {
			//Check if the file exists or not
			File[] fs = dir.listFiles();
			boolean exists = false;
			for(int i = 0; i < fs.length; i++) {
				if(fs[i].getName().equals(filename)) {
					exists = true;
				}
			}
			if(!exists) {
				return FSReturnVals.FileDoesNotExist;
			}
			
			//Try to delete the file
			File file = new File(tgtdir + "/" + filename);
			if(file.delete()) {
				return FSReturnVals.Success;
			} else {
				return FSReturnVals.Fail; 
			}
		}
	}

	/**
	 * Opens the file specified by the FilePath and populates the FileHandle
	 * Returns FileDoesNotExist if the specified filename by FilePath is
	 * not-existent
	 *
	 * Example usage: OpenFile("/Shahram/CSCI485/Lecture1/Intro.pptx", FH1)
	 */
	public FSReturnVals OpenFile(String FilePath, FileHandle ofh) {
		
		Master master = Master.getInstance();
		if(!master.fileExists(FilePath)) {
			return FSReturnVals.FileDoesNotExist;
		}
		int numChunks = master.getNumChunks(FilePath);
		ofh.setFile(FilePath);
		if(numChunks == 0) {
			return FSReturnVals.Success;
		}

		
		List<Chunk> chunks = new ArrayList<Chunk>();
		int offset = 0;
		
		try {
			
			RandomAccessFile f = new RandomAccessFile(FilePath, "rw");
			
			for(int i = 0; i < numChunks; i++) {
				Chunk chunk = new Chunk(FilePath + i);
				
				//Parse the header
				int numRecords = f.readInt();
				int numBytesFree = f.readInt();
				int freeOffset = f.readInt();
				
				chunk.setNumBytesFree(numBytesFree);
				chunk.setFreeOffset(freeOffset);
				
				offset += 12; //move forward to data
				
				//Parse the records
				for(int j = 0; j < numRecords; j++) {
					int size = f.readInt();
					offset += 4;
					
					byte[] b = new byte[size];
					f.read(b, offset, size);
					TinyRec record = new TinyRec();
					record.setPayload(b);
					
					RID rid = new RID();
					rid.setID(chunk.getChunkHandle() + (chunk.getNumRecords() + 1));
					rid.setChunkHandle(chunk.getChunkHandle());
					record.setRID(rid);
					
					chunk.addRecord(record, size);
				}
				
				chunks.add(chunk);
				
			}
			
			f.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ofh.setChunks(chunks);
		
		return FSReturnVals.Success;
		

	}

	/**
	 * Closes the specified file handle Returns BadHandle if ofh is invalid
	 *
	 * Example usage: CloseFile(FH1)
	 */
	public FSReturnVals CloseFile(FileHandle ofh) {
		if(ofh == null) {
			return FSReturnVals.BadHandle;
		}
		
		return FSReturnVals.Success;
	}

}
