package example;

import java.io.FileInputStream;
import java.io.IOException;

import rapcap.lib.lzo.LzoBoundaryDetector;

public class TestLzo {

	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(args[0]);

		LzoBoundaryDetector detector = new LzoBoundaryDetector(fis);
		
		long to_find = new Long(args[1]);
		long to_skip = to_find - detector.globalHeaderLength;
		
		while (to_skip > 0) {
			long skipped = fis.skip(to_skip);
			to_skip -= skipped;
		}

		int index = detector.detect();
		System.out.printf("Started from index %d, found header at %d\n", to_find, to_find + index);
		fis.close();

	}

}
