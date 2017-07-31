package rapcap.hadoop.mr1.lzo;

import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import rapcap.lib.RecordBoundaryDetector;
import rapcap.hadoop.mr1.RecordInputFormat;
import rapcap.lib.lzo.LzoBoundaryDetector;

public class LzoFileInputFormat extends RecordInputFormat<LongWritable, BytesWritable> {
	
	@Override
	protected RecordBoundaryDetector createBoundaryDetector(InputStream stream) throws IOException {
		return new LzoBoundaryDetector(stream);
	}

	@Override
	protected RecordReader<LongWritable, BytesWritable> createRecordReader(long start, long end, Seekable baseStream, 
			InputStream stream, Reporter reporter) throws IOException { 
		return new LzoRecordReader(start, end, baseStream, stream, reporter);
	}
}