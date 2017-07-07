package rapcap.lib.pcap;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import rapcap.lib.Record;
import rapcap.lib.Util;

public class PcapRecordFormat implements rapcap.lib.RecordFormat {
	final int HEADER_LEN = 16;
	byte[] byte_buf = new byte[HEADER_LEN];
	int snap_len;
	private ByteOrder byteOrder;

	public PcapRecordFormat(int snap_len, java.nio.ByteOrder byteOrder) {
		this.snap_len = snap_len;
		this.byteOrder = byteOrder;
	}
		
	public void interpretRecord(DataInputStream input, Record record) throws IOException {
		input.readFully(byte_buf);
		int incl_len = Util.readInt(byte_buf, 8, byteOrder);
		int orig_len = Util.readInt(byte_buf, 12, byteOrder);

//		if (orig_len > 0 && incl_len == Math.min(orig_len, snap_len))
		if (orig_len <= 0
		 || incl_len <= 0
		 || incl_len > Math.min(orig_len, snap_len)) {
			record.valid = false;
			return;
		}
			
		record.header_len = HEADER_LEN;
		record.body_len = incl_len;
		record.valid = true;
	}

	public int maxRecordBodyLen() {
		return snap_len;
	}

	public int maxRecordHeaderLen() {
		return HEADER_LEN;
	}

	public ByteOrder byteOrder() {
		return byteOrder;
	}
}
