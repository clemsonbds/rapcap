package rapcap.lib.pcap;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import net.ripe.hadoop.pcap.PcapReader;
import rapcap.lib.RecordBoundaryDetector;

public class PcapBoundaryDetector extends RecordBoundaryDetector {
	public long snaplen;
	public ByteOrder byteorder;

	public PcapBoundaryDetector(BufferedInputStream stream) throws IOException {
		PcapReader reader = new PcapReader(new DataInputStream(stream)); // reads the first 24 bytes of the file

    	try {
    		snaplen = PcapReaderAccessor.getSnapLen(reader);
    		byteorder = PcapReaderAccessor.isReverseHeaderByteOrder(reader) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    	}
    	catch (IllegalAccessException e) {
    		throw new IOException("Unable to access private fields of PcapReader class.  JVM security may be the problem.");
    	}

		initialize(stream, new PcapRecordFormat((int) snaplen, byteorder));
	}

	@Override
	public long getRecordStartOffset() {
		return 24;
	}
}
