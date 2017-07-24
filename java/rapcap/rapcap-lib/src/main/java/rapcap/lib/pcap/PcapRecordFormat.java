package rapcap.lib.pcap;

import java.io.IOException;
import java.nio.ByteOrder;

import rapcap.lib.Record;
import rapcap.lib.RecordFormat;
import rapcap.lib.Util;

public class PcapRecordFormat extends RecordFormat {
	private final ByteOrder byteOrder;

	public PcapRecordFormat(int maxBodyLen, ByteOrder byteOrder) {
		this.MAX_HEADER_LEN = 16;
		this.MAX_BODY_LEN = maxBodyLen;
		this.byteOrder = byteOrder;
	}
	
	public boolean interpretRecordHeader(byte header_buf[], Record record) throws IOException {
		int incl_len = Util.readInt(header_buf, 8, byteOrder);
		int orig_len = Util.readInt(header_buf, 12, byteOrder);

		if (orig_len <= 0
		 || incl_len <= 0
		 || incl_len > Math.min(orig_len, MAX_BODY_LEN)) {
			return false;
		}

		record.header_len = MAX_HEADER_LEN;
		record.body_len = incl_len;
		return true;
	}
}
