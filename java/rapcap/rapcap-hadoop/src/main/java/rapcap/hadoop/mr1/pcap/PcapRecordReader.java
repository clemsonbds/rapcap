package rapcap.hadoop.mr1.pcap;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.Reporter;

import net.ripe.hadoop.pcap.PcapReader;

public class PcapRecordReader extends net.ripe.hadoop.pcap.io.reader.PcapRecordReader {

	private int ID;
	private long next_start;
	private long tstart;

	public PcapRecordReader(PcapReader pcapReader, long start, long end, Seekable baseStream, DataInputStream stream,
			Reporter reporter) throws IOException {
		super(pcapReader, start, end, baseStream, stream, reporter);
		this.tstart = start;
		this.next_start = end;
		ID = new Random().nextInt(99);
	}

	@Override
	public boolean next(LongWritable key, ObjectWritable value) throws IOException {
		long pos = getPos();
		
		if (pos >= next_start)
			return false;

		System.out.printf("rapcap: reader %d reading header at position %d in range (%d,%d)\n",
				ID, pos, tstart, next_start);
		return super.next(key, value);
	}
}
