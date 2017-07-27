package example;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import rapcap.lib.lzo.LzopBoundaryDetector;

public class TestLzo {

	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(args[0]);
		BufferedInputStream bis = new BufferedInputStream(fis);

		LzopBoundaryDetector detector = new LzopBoundaryDetector(bis);
		
		long to_find = new Long(args[1]);
		long to_skip = to_find - detector.globalHeaderLength;
		
		while (to_skip > 0) {
			long skipped = bis.skip(to_skip);
			to_skip -= skipped;
		}

		int index = detector.detect();
		System.out.printf("Started from index %d, found header at %d\n", to_find, to_find + index);
		bis.close();

	}

}
