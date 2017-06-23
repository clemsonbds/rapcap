package rapcap.lib.lzo;

import java.nio.ByteOrder;

import rapcap.lib.Record;
import rapcap.lib.RecordFormat;
import rapcap.lib.Util;
import com.hadoop.compression.lzo.*;

@SuppressWarnings("unused")
public class LzopRecordFormat implements rapcap.lib.RecordFormat {
	
	final int HEADER_LEN = 4;
	final private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	int snap_len;
	
	public LzopRecordFormat(int snaplen) {
		snap_len = snaplen;
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

	public Record interpretRecord(byte[] bytes, int offset) {
		if (snap_len == Util.readInt(bytes, offset, byteOrder)){
			return new Record(HEADER_LEN, snap_len);
		}
		
		
		return null;
	}

}
