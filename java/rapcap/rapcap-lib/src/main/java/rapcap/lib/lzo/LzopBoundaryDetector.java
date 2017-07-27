package rapcap.lib.lzo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import rapcap.lib.RecordBoundaryDetector;

public class LzopBoundaryDetector extends RecordBoundaryDetector {
	public int snaplen;
	public long globalHeaderLength;

	private void readGlobalHeader(DataInputStream in) throws IOException {
		globalHeaderLength = 0;
		byte[] buf = new byte[9];

		in.read(buf, 0, 9); // magic num
		in.read(buf, 0, 2); // version
		in.read(buf, 0, 2); // library version
		in.read(buf, 0, 2); // extract version
		in.read(buf, 0, 1); // method
		in.read(buf, 0, 1); // level
		globalHeaderLength += 17;
		
		int flags = in.readInt(); // flags
		boolean extrafield = 0 != (flags & 0x00000040);
		globalHeaderLength += 4;
		
		in.read(buf, 0, 4); // mode
		in.read(buf, 0, 4); // mtime
		in.read(buf, 0, 4); // gmtdiff
		globalHeaderLength += 12;

		in.read(buf, 0, 1); // filename len
		in.read(new byte[buf[0]]);
		globalHeaderLength += buf[0] + 1;
		
		in.read(buf, 0, 4); // csum
		globalHeaderLength += 4;

		if (extrafield) {
			int len = in.readInt();
			in.read(new byte[len], 0, len);
			in.read(buf, 0, 4); // csum
			globalHeaderLength += len + 8;
		}
	}
	
	public LzopBoundaryDetector(BufferedInputStream stream) throws IOException {
		DataInputStream dis = new DataInputStream(stream);

		// reading the header will advance the stream to the first byte of the first compressed block header
		readGlobalHeader(dis);

		// read the uncompressed length, this should be the same for all records
		dis.mark(4);
		this.snaplen = dis.readInt();
		dis.reset();

		// initializes with a stream already advanced past the global header
		initialize(stream, new LzopRecordFormat(snaplen));
	}

	@Override
	public long getRecordStartOffset() {
		return globalHeaderLength;
	}
}
