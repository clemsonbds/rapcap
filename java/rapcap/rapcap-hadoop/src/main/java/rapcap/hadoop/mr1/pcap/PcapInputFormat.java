package rapcap.hadoop.mr1.pcap;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import net.ripe.hadoop.pcap.PcapReader;
import net.ripe.hadoop.pcap.io.reader.PcapRecordReader;
import rapcap.lib.RecordBoundaryDetector;
import rapcap.hadoop.mr1.RecordInputFormat;
import rapcap.lib.pcap.PcapBoundaryDetector;

public class PcapInputFormat extends RecordInputFormat<LongWritable, ObjectWritable> {

	@Override
	protected RecordBoundaryDetector createBoundaryDetector(DataInputStream stream) throws IOException {
		return new PcapBoundaryDetector(new BufferedInputStream(stream));
	}

	@Override
	protected RecordReader<LongWritable, ObjectWritable> createRecordReader(long start, long end, Seekable baseStream,
			DataInputStream stream, Reporter reporter) throws IOException {
		baseStream.seek(0);
        PcapReader reader = new PcapReader(stream); // reads the first 24 bytes of the file, even remotely
        baseStream.seek(start);
        return new PcapRecordReader(reader, start, end, baseStream, stream, reporter);
	}


}
