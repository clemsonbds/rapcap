package rapcap.hadoop.mr1.lzo;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import rapcap.hadoop.mr1.RecordInputFormat;
import rapcap.lib.RecordBoundaryDetector;
import rapcap.lib.lzo.*;

import com.hadoop.compression.lzo.*;
import com.hadoop.mapreduce.*;

public class LzoInputFormat extends RecordInputFormat<LongWritable, ObjectWritable> {

	@Override
	protected RecordBoundaryDetector createBoundaryDetector(DataInputStream stream) throws IOException {
		return new LzopBoundaryDetector(stream);
	}

	@Override
	protected RecordReader<LongWritable, ObjectWritable> createRecordReader(long start, long end, Seekable baseStream, 
			DataInputStream stream, Reporter reporter) throws IOException { //None of this works yet
		// TODO Fill this with decompressor
		LzoSplitRecordReader reader = new LzoSplitRecordReader();
		
		return null;
	}
}