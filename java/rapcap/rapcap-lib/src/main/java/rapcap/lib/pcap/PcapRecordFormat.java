package rapcap.lib.pcap;

import java.nio.ByteOrder;

import rapcap.lib.Record;
import rapcap.lib.Util;

public class PcapRecordFormat implements rapcap.lib.RecordFormat {
	final int HEADER_LEN = 16;
	int snap_len;
	private ByteOrder byteOrder;

	public PcapRecordFormat(int snap_len, java.nio.ByteOrder byteOrder) {
		this.snap_len = snap_len;
		this.byteOrder = byteOrder;
	}
		
	public Record interpretRecord(byte[] bytes, int offset) {
		int incl_len = Util.readInt(bytes, offset + 8, byteOrder);
		int orig_len = Util.readInt(bytes, offset + 12, byteOrder);

//		if (orig_len > 0 && incl_len == Math.min(orig_len, snap_len))
		if (orig_len > 0 && incl_len > 0 && incl_len <= Math.min(orig_len, snap_len))
			return new Record(HEADER_LEN, incl_len);

		return null;
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
