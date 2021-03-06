package com.client;

import java.io.File;
import java.nio.ByteBuffer;

import com.client.ClientFS.FSReturnVals;
import com.master.Master;

public class ClientRec {

	/**
	 * Appends a record to the open file as specified by ofh Returns BadHandle
	 * if ofh is invalid Returns BadRecID if the specified RID is not null
	 * Returns RecordTooLong if the size of payload exceeds chunksize RID is
	 * null if AppendRecord fails
	 *
	 * Example usage: AppendRecord(FH1, obama, RecID1)
	 */
	public FSReturnVals AppendRecord(FileHandle ofh, byte[] payload, RID RecordID) {
		
		if(ofh == null) {
			return FSReturnVals.BadHandle;
		}
		
		if(RecordID == null) {
			return FSReturnVals.BadRecID;
		}
		
		if(ofh.appendRecord(payload, RecordID)) {
			return FSReturnVals.Success;
		}
		
		return null;
	}

	/**
	 * Deletes the specified record by RecordID from the open file specified by
	 * ofh Returns BadHandle if ofh is invalid Returns BadRecID if the specified
	 * RID is not valid Returns RecDoesNotExist if the record specified by
	 * RecordID does not exist.
	 *
	 * Example usage: DeleteRecord(FH1, RecID1)
	 */
	public FSReturnVals DeleteRecord(FileHandle ofh, RID RecordID) {
		
		if(ofh == null) {
			return FSReturnVals.BadHandle;
		}
		
		boolean success = ofh.deleteRecord(RecordID);
		if(success) {
			return FSReturnVals.Success;
		}
		
		return null;
	}

	/**
	 * Reads the first record of the file specified by ofh into payload Returns
	 * BadHandle if ofh is invalid Returns RecDoesNotExist if the file is empty
	 *
	 * Example usage: ReadFirstRecord(FH1, tinyRec)
	 */
	public FSReturnVals ReadFirstRecord(FileHandle ofh, TinyRec rec){
		
		if(ofh == null) {
			System.out.println("Null filehandle");
			return FSReturnVals.BadHandle;
		}
		
		TinyRec record = ofh.readFirstRecord();
		
		if(record == null) {
			System.out.println("Null record");
			return FSReturnVals.RecDoesNotExist;
		}
		

		
		rec.setRID(record.getRID());
		rec.setPayload(record.getPayload());
	
		//System.out.println("Success! Record id is: " + rec.getRID().getID() + " and handle: " + rec.getRID().getChunkHandle());
		
		return FSReturnVals.Success;
	}

	/**
	 * Reads the last record of the file specified by ofh into payload Returns
	 * BadHandle if ofh is invalid Returns RecDoesNotExist if the file is empty
	 *
	 * Example usage: ReadLastRecord(FH1, tinyRec)
	 */
	public FSReturnVals ReadLastRecord(FileHandle ofh, TinyRec rec){
		
		if(ofh == null) {
			System.out.println("Null filehandle");
			return FSReturnVals.BadHandle;
		}
		
		TinyRec record = ofh.readLastRecord();
		
		if(record == null) {
			System.out.println("Null record");
			return FSReturnVals.RecDoesNotExist;
		}
		

		
		rec.setRID(record.getRID());
		rec.setPayload(record.getPayload());
	
		return FSReturnVals.Success;
		
	}

	/**
	 * Reads the next record after the specified pivot of the file specified by
	 * ofh into payload Returns BadHandle if ofh is invalid Returns
	 * RecDoesNotExist if the file is empty or pivot is invalid
	 *
	 * Example usage: 1. ReadFirstRecord(FH1, tinyRec1) 2. ReadNextRecord(FH1,
	 * rec1, tinyRec2) 3. ReadNextRecord(FH1, rec2, tinyRec3)
	 */
	public FSReturnVals ReadNextRecord(FileHandle ofh, RID pivot, TinyRec rec){
		if(ofh == null) {
			return FSReturnVals.BadHandle;
		}
		
		TinyRec record = ofh.readNextRecord(pivot);
		if(record == null) {
			return FSReturnVals.RecDoesNotExist;
		}
		
		rec.setRID(record.getRID());
		rec.setPayload(record.getPayload());
		
		return FSReturnVals.Success;

	}

	/**
	 * Reads the previous record after the specified pivot of the file specified
	 * by ofh into payload Returns BadHandle if ofh is invalid Returns
	 * RecDoesNotExist if the file is empty or pivot is invalid
	 *
	 * Example usage: 1. ReadLastRecord(FH1, tinyRec1) 2. ReadPrevRecord(FH1,
	 * recn-1, tinyRec2) 3. ReadPrevRecord(FH1, recn-2, tinyRec3)
	 */
	public FSReturnVals ReadPrevRecord(FileHandle ofh, RID pivot, TinyRec rec){
		if(ofh == null) {
			return FSReturnVals.BadHandle;
		}
		
		TinyRec record = ofh.readPrevRecord(pivot);
		if(record == null) {
			return FSReturnVals.RecDoesNotExist;
		}
		
		rec.setRID(record.getRID());
		rec.setPayload(record.getPayload());
		
		return FSReturnVals.Success;
	}

}
