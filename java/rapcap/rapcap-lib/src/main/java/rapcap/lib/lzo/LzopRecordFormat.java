package rapcap.lib.lzo;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import rapcap.lib.Record;

public class LzopRecordFormat implements rapcap.lib.RecordFormat {
	
	static final int HEADER_LEN = 4;
	final private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	int snap_len;
	private PublicLzopInputStream stream;
	byte input_buffer[];
	
	public LzopRecordFormat(int snaplen, PublicLzopInputStream stream) {
		snap_len = snaplen;
		this.stream = stream;
		input_buffer = new byte[snaplen];
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

	public void testRecordHeader(DataInputStream input, Record record) throws IOException {
		record.header_len = input.readInt();
		record.valid = (snap_len == record.header_len);
	}

	public void getRecordLength(DataInputStream input, Record record) throws IOException {
		if (!record.valid)
			return;

		// get the distance to the next block
		stream.resetState();
		int bytes_read = stream.decompress(input_buffer, 0, snap_len);
		System.out.println("got " + bytes_read + " bytes from decompress");

		// TODO: set valid = false on error in decompression
		
		record.body_len = bytes_read;
	}
}
