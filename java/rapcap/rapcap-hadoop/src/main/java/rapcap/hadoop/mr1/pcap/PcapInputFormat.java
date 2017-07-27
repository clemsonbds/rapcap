package rapcap.hadoop.mr1.pcap;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import net.ripe.hadoop.pcap.PcapReader;
//import net.ripe.hadoop.pcap.io.reader.PcapRecordReader;
import rapcap.lib.RecordBoundaryDetector;
import rapcap.hadoop.mr1.RecordInputFormat;
import rapcap.lib.pcap.PcapBoundaryDetector;

public class PcapInputFormat extends RecordInputFormat<LongWritable, ObjectWritable> {

	@Override
	protected RecordBoundaryDetector createBoundaryDetector(InputStream stream) throws IOException {
		return new PcapBoundaryDetector(stream);
	}

	@Override
	protected RecordReader<LongWritable, ObjectWritable> createRecordReader(long start, long end, Seekable baseStream,
			InputStream stream, Reporter reporter) throws IOException {
		DataInputStream dis = new DataInputStream(stream);
		baseStream.seek(0);

		return new PcapRecordReader(
    		new PcapReader(dis), // reads the first 24 bytes of the file, even remotely
    		start,
    		end,
    		baseStream,
    		dis,
    		reporter
        );
	}


}
