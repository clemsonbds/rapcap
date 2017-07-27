package example;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import rapcap.lib.lzo.LzopBoundaryDetector;

public class LzoPrintIndices {

	public static void main(String[] args) throws IOException {
		
		FileInputStream fis = new FileInputStream(args[0]);
		BufferedInputStream bis = new BufferedInputStream(fis);

		LzopBoundaryDetector detector = new LzopBoundaryDetector(bis);
		long global_len = detector.globalHeaderLength;
		long snap_len = detector.snaplen;
		bis.close();

		
		File file = new File(args[0]);
		System.out.printf("global header len = %d, snaplen = %d, filesize = %d\n",
				global_len, snap_len, file.length());

		fis = new FileInputStream(file);
		DataInputStream dis = new DataInputStream(fis);
		
		long index = detector.globalHeaderLength;
		long pos = 0;

		while (index < file.length()) {
			while (pos < index) {
				long skipped = dis.skip(index - pos);
				pos += skipped;
			}

			long uncompressed_len = dis.readInt();
			long compressed_len = dis.readInt();

			System.out.printf("%7d  %7d  %7d\n", index, uncompressed_len, compressed_len);
			
			index += compressed_len + 4;
		}

		dis.close();
	}

}
