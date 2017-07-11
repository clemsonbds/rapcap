package example;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;


import rapcap.lib.pcap.PcapBoundaryDetector;

public class FindFirstAfter {

	public static void main(String[] args) throws IOException {
		
		FileInputStream fis = new FileInputStream(args[0]);
		BufferedInputStream dis = new BufferedInputStream(fis);
		System.out.println(dis.available());
		PcapBoundaryDetector detector = new PcapBoundaryDetector(dis);
		System.out.println(dis.available());
		int solution = detector.detect();
		System.out.println("The Solution is byte " + solution);
	}

}
