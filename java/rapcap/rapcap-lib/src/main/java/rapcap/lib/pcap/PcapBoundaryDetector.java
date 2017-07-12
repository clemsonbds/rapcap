package rapcap.lib.pcap;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import net.ripe.hadoop.pcap.PcapReader;
import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.RecordFormat;

public class PcapBoundaryDetector extends RecordBoundaryDetector {
	public long snaplen;
	public ByteOrder byteorder;

	public PcapBoundaryDetector(BufferedInputStream stream) throws IOException {
        DataInputStream dstream = new DataInputStream(stream);
		PcapReader reader = new PcapReader(dstream); // reads the first 24 bytes of the file, even remotely

    	try {
    		snaplen = PcapReaderAccessor.getSnapLen(reader);
    		byteorder = PcapReaderAccessor.isReverseHeaderByteOrder(reader) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    	}
    	catch (IllegalAccessException e) {
    		throw new IOException("Unable to access private fields of PcapReader class.  JVM security may be the problem.");
    	}

		RecordFormat format = new PcapRecordFormat((int)snaplen, byteorder);
		initialize((BufferedInputStream)stream, format);
	}
}
