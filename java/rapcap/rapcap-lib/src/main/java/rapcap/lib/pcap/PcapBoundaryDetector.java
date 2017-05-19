package rapcap.lib.pcap;

import java.io.DataInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import net.ripe.hadoop.pcap.PcapReader;
import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.RecordFormat;

public class PcapBoundaryDetector extends RecordBoundaryDetector {
	public PcapBoundaryDetector(DataInputStream stream) throws IOException {
        PcapReader reader = new PcapReader(stream); // reads the first 24 bytes of the file, even remotely

        long snaplen;
        ByteOrder byteorder;

    	try {
    		snaplen = PcapReaderAccessor.getSnapLen(reader);
    		byteorder = PcapReaderAccessor.isReverseHeaderByteOrder(reader) ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN;
    	}
    	catch (IllegalAccessException e) {
    		throw new IOException("Unable to access private fields of PcapReader class.  JVM security may be the problem.");
    	}

		RecordFormat format = new PcapRecordFormat((int)snaplen, byteorder);
		initialize(stream, format);
	}
}
