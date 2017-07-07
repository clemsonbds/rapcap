package example;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import old.RecordBoundaryDetector;
import rapcap.lib.pcap.PcapBoundaryDetector;

public class FindFirstAfter {

	public static void main(String[] args) throws IOException {
		FileInputStream fis = new FileInputStream(args[1]);
		DataInputStream dis = new DataInputStream(fis);

		RecordBoundaryDetector detector = new PcapBoundaryDetector(dis);
	}

}
