package rapcap.hadoop.mr1.pcap;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.Reporter;

import net.ripe.hadoop.pcap.PcapReader;

public class PcapRecordReader extends net.ripe.hadoop.pcap.io.reader.PcapRecordReader {

	public PcapRecordReader(PcapReader pcapReader, long start, long end, Seekable baseStream, DataInputStream stream,
			Reporter reporter) throws IOException {
		super(pcapReader, start, end, baseStream, stream, reporter);
	}

	@Override
	public boolean next(LongWritable key, ObjectWritable value) throws IOException {
		System.out.println("rapcap: reading header at position " + getPos());
		return super.next(key, value);
	}

}
