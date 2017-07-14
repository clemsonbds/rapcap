package example;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.Channels;

import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.lzo.LzopBoundaryDetector;

public class TestLzo {

	public static void main(String[] args) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(args[0], "r");
		InputStream is = Channels.newInputStream(raf.getChannel());
		BufferedInputStream bis = new BufferedInputStream(is);
System.out.println(is.available());
		RecordBoundaryDetector detector = new LzopBoundaryDetector(bis);
System.out.println(is.available());

		int index = detector.detect();
		System.out.println("Found solution at "+index);
		raf.close();
	}

}
