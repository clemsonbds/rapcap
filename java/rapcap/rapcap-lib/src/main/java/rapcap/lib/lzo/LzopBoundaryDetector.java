package rapcap.lib.lzo;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import org.apache.commons.io.input.CountingInputStream;

import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.RecordFormat;

import com.hadoop.compression.lzo.LzopCodec;
import com.hadoop.compression.lzo.LzopDecompressor;

public class LzopBoundaryDetector extends RecordBoundaryDetector {
	private int snaplen;
	private long globalHeaderLength;

	private int readInt(InputStream in) throws IOException {
		byte[] buf = new byte[4];
		in.read(buf, 0, 4);
		return ByteBuffer.wrap(buf).getInt();
	}
/*
	private void readHeader(InputStream in) throws IOException {
		byte[] buf = new byte[9];
		in.read(buf, 0, 9); // magic num
		in.read(buf, 0, 2); // version
		in.read(buf, 0, 2); // library version
		in.read(buf, 0, 2); // extract version
		in.read(buf, 0, 1); // method
		in.read(buf, 0, 1); // level

		int flags = readInt(in); // flags
		boolean extrafield = 0 != (flags & 0x00000040);
		
		in.read(buf, 0, 4); // mode
		in.read(buf, 0, 4); // mtime
		in.read(buf, 0, 4); // gmtdiff

		in.read(buf, 0, 1); // filename len
		in.read(new byte[buf[0]]);

		in.read(buf, 0, 4); // csum

		if (extrafield) {
			int len = readInt(in);
			in.read(new byte[len], 0, len);
			in.read(buf, 0, 4); // csum
		}
	}
*/	
	public LzopBoundaryDetector(BufferedInputStream stream) throws IOException {
		int buffer_size = LzopCodec.DEFAULT_LZO_BUFFER_SIZE;
		LzopDecompressor decompressor = new LzopDecompressor(buffer_size);

		//stream.mark(LzopRecordFormat.HEADER_LEN);

		stream.mark(1000);

		// this reads the header from stream, advancing the pointer
		CountingInputStream counting_stream = new CountingInputStream(stream);
		PublicLzopInputStream lis = new PublicLzopInputStream(counting_stream, decompressor, buffer_size);
		this.globalHeaderLength = counting_stream.getCount();
		this.snaplen = readInt(stream);
		stream.reset();

		RecordFormat format = new LzopRecordFormat(snaplen, lis, counting_stream);
		initialize(stream, format);
	}

	@Override
	public long getRecordStartOffset() {
		return globalHeaderLength;
	}
}
