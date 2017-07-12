package example;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import rapcap.lib.Util;
import rapcap.lib.pcap.PcapBoundaryDetector;

public class FindFirstAfter {

	public static void main(String[] args) throws IOException {
		
		FileInputStream fis = new FileInputStream(args[0]);
		BufferedInputStream bis = new BufferedInputStream(fis);
		System.out.println(bis.available());
		PcapBoundaryDetector detector = new PcapBoundaryDetector(bis);

		ByteOrder byteOrder = detector.byteorder;
		long snap_len = detector.snaplen;
		
		System.out.printf("File has snap_len=%d, %s byte order\n", snap_len, byteOrder);
		
		System.out.println(bis.available());
		int solution = detector.detect();
		System.out.println("The Solution is index 24 + " + solution);

		bis.close();
		
		fis = new FileInputStream(args[0]);
		DataInputStream dis = new DataInputStream(new BufferedInputStream(fis));
		byte byte_buf[] = new byte[16];
		int ts_sec, ts_usec, incl_len, orig_len;

		long to_skip = 24;
		while (to_skip > 0) {
			long skipped = dis.skip(to_skip);
			to_skip -= skipped;
		}

		dis.mark(solution*3);
		
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
	}

}
