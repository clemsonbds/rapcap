package rapcap.hadoop.mr1.lzo;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import rapcap.lib.RecordBoundaryDetector;
import rapcap.hadoop.mr1.RecordInputFormat;
import rapcap.lib.lzo.LzopBoundaryDetector;

public class LzoInputFormat extends RecordInputFormat<LongWritable, BytesWritable> {
	
	@Override
	protected RecordBoundaryDetector createBoundaryDetector(DataInputStream stream) throws IOException {
		return new LzopBoundaryDetector(new BufferedInputStream(stream));
	}

	@Override
	protected RecordReader<LongWritable, BytesWritable> createRecordReader(long start, long end, Seekable baseStream, 
			DataInputStream stream, Reporter reporter) throws IOException { 
		return new LzopRecordReader(start, end, baseStream, stream, reporter);
	}
}