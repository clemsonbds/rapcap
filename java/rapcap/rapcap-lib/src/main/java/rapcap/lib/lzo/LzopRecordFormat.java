package rapcap.lib.lzo;

import java.io.IOException;
import java.nio.ByteOrder;

import rapcap.lib.Record;
import rapcap.lib.RecordFormat;
import rapcap.lib.Util;

public class LzopRecordFormat extends RecordFormat {
	
	public LzopRecordFormat(int maxBodyLen) {
		this.MAX_HEADER_LEN = 8;
		this.MAX_BODY_LEN = maxBodyLen;
	}

	public boolean interpretRecordHeader(byte[] header_buf, Record record) throws IOException {
		int uncompressed_len = Util.readInt(header_buf, 0, ByteOrder.BIG_ENDIAN);
		int compressed_len = Util.readInt(header_buf, 4, ByteOrder.BIG_ENDIAN);

		if (uncompressed_len != MAX_BODY_LEN
		 || compressed_len == 0
		 || compressed_len > uncompressed_len)
			return false;

		record.header_len = MAX_HEADER_LEN;
		record.body_len = compressed_len;
		return true;
	}
}
