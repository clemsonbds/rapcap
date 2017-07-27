package example;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import rapcap.lib.lzo.LzopBoundaryDetector;

public class LzoPrintIndices {

	public static void main(String[] args) throws IOException {
		
		FileInputStream fis = new FileInputStream(args[0]);
		BufferedInputStream bis = new BufferedInputStream(fis);

		LzopBoundaryDetector detector = new LzopBoundaryDetector(bis);
		long global_len = detector.globalHeaderLength;
		long snap_len = detector.snaplen;
		bis.close();

		
		RandomAccessFile file = new RandomAccessFile(args[0], "r");
		System.out.printf("global header len = %d, snaplen = %d, filesize = %d\n",
				global_len, snap_len, file.length());

		long index = global_len;

		while (index < file.length()) {
			file.seek(index);

			long uncompressed_len = file.readInt();
//			file.seek(index+4);
			
			long compressed_len = file.readInt();

			System.out.printf("%7d  %7d  %7d\n", index, uncompressed_len, compressed_len);
			
			index += compressed_len + 12;
		}

		file.close();
	}

}
