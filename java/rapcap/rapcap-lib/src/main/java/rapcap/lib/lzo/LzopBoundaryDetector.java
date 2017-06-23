package rapcap.lib.lzo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.RecordFormat;


public class LzopBoundaryDetector extends RecordBoundaryDetector {
	private int snaplen;

	private int readInt(InputStream in) throws IOException {
		byte[] buf = new byte[4];
		in.read(buf, 0, 4);
		return ByteBuffer.wrap(buf).getInt();
	}
	
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
	
	public LzopBoundaryDetector(DataInputStream stream) throws IOException {
		readHeader(stream);
		snaplen = readInt(stream);
		RecordFormat format = new LzopRecordFormat(snaplen);
		
		initialize(stream, format);
	}
}
