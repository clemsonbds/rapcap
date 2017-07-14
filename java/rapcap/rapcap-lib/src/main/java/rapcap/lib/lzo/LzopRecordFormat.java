package rapcap.lib.lzo;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import org.apache.commons.io.input.CountingInputStream;

import rapcap.lib.Record;

public class LzopRecordFormat implements rapcap.lib.RecordFormat {
	
	static final int HEADER_LEN = 4;
	final private ByteOrder byteOrder = ByteOrder.BIG_ENDIAN;
	int snap_len;
	private CountingInputStream counter;
	private PublicLzopInputStream stream;
	byte input_buffer[];
	
	public LzopRecordFormat(int snaplen, PublicLzopInputStream stream, CountingInputStream counter) {
		snap_len = snaplen;
		this.stream = stream;
		this.counter = counter;
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
		int uncompressed_len = input.readInt();
		record.valid = (snap_len == uncompressed_len);
	}

	public void getRecordLength(DataInputStream input, Record record) throws IOException {
		if (!record.valid)
			return;

		// get the distance to the next block
		stream.resetState();
		counter.resetCount();
		int decompressed_bytes_read = stream.decompress(input_buffer, 0, input_buffer.length);
		int compressed_bytes_read = counter.getCount();

		System.out.println("read " + compressed_bytes_read + " compressed bytes, got " + decompressed_bytes_read + " uncompressed bytes");
		System.out.printf("%c %c %c ... %c %c %c\n",
			input_buffer[0],
			input_buffer[1],
			input_buffer[2],
			input_buffer[decompressed_bytes_read - 3],
			input_buffer[decompressed_bytes_read - 2],
			input_buffer[decompressed_bytes_read - 1]
		);
		
		// TODO: set valid = false on error in decompression
		
		record.header_len = HEADER_LEN;
		record.body_len = compressed_bytes_read - HEADER_LEN;
	}
}
