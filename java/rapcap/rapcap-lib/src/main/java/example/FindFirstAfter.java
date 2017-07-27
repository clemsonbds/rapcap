package example;

import java.io.FileInputStream;
import java.io.IOException;

import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.pcap.PcapBoundaryDetector;

public class FindFirstAfter {

	public static void main(String[] args) throws IOException {
		
		FileInputStream fis = new FileInputStream(args[0]);
//		DataInputStream dis = new DataInputStream(bis);
		int to_find = new Integer(args[1]);

		RecordBoundaryDetector detector = new PcapBoundaryDetector(fis);

		long to_skip = to_find - 24;
		while (to_skip > 0) {
			long skipped = fis.skip(to_skip);
			to_skip -= skipped;
		}

		int index = detector.detect();
		System.out.printf("Started from index %d, found header at %d\n", to_find, to_find + index);

		
		
		/*		dis.mark(solution*3);

		byte byte_buf[] = new byte[16];
		int ts_sec, ts_usec, incl_len, orig_len;

		for (int i = 0; i < solution*2; ) {
			dis.readFully(byte_buf);
		
			ts_sec   = Util.readInt(byte_buf, 0, byteOrder);
			ts_usec  = Util.readInt(byte_buf, 4, byteOrder);
			incl_len = Util.readInt(byte_buf, 8, byteOrder);
			orig_len = Util.readInt(byte_buf, 12, byteOrder);
	
			System.out.printf("i=%d\tts_sec=%d\tts_usec=%d\tincl_len=%d\torig_len=%d\n",
					i+24, ts_sec, ts_usec, incl_len, orig_len);

			i += 16 + incl_len;

			to_skip = incl_len;
			while (to_skip > 0) {
				long skipped = dis.skip(to_skip);
				to_skip -= skipped;
			}
		}

		dis.reset();

		to_skip = solution;
		while (to_skip > 0) {
			long skipped = dis.skip(to_skip);
			to_skip -= skipped;
		}

		dis.readFully(byte_buf);
		
		ts_sec   = Util.readInt(byte_buf, 0, byteOrder);
		ts_usec  = Util.readInt(byte_buf, 4, byteOrder);
		incl_len = Util.readInt(byte_buf, 8, byteOrder);
		orig_len = Util.readInt(byte_buf, 12, byteOrder);

		System.out.printf("ts_sec=%d, ts_usec=%d, incl_len=%d, orig_len=%d\n",
				ts_sec, ts_usec, incl_len, orig_len);
		
		dis.close();
*/
	}

}
