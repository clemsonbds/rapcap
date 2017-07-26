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

	public PcapRecordReader(PcapReader pcapReader, long start_byte, long next_start, Seekable baseStream, DataInputStream stream,
			Reporter reporter) throws IOException {
		super(pcapReader, start_byte, next_start-1, baseStream, stream, reporter);
		this.tstart = start_byte;
		this.next_start = next_start;
		ID = new Random().nextInt(99);
	}

	@Override
	public boolean next(LongWritable key, ObjectWritable value) throws IOException {
		long pos = getPos();
		
		System.out.printf("rapcap: reader %d at position %d in range (%d,%d)\n", ID, pos, tstart, next_start);
		
		if (pos >= next_start)
			return false;

		boolean ret = super.next(key, value);
		
		System.out.printf("rapcap: reader %d at position %d returning %s\n",
				ID, pos, ret);
		return ret;
	}
}
