package rapcap.hadoop.mr1.lzo;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

public class LzopRecordReader implements RecordReader<LongWritable, ObjectWritable> {

	
	Seekable baseStream;
	DataInputStream stream;
	Reporter reporter;

	long blockCount = 0;
	long start, end;
	public PublicLzopInputStream dstream = (PublicLzopInputStream)baseStream;
	byte input_buffer[];
	
	public LzopRecordReader(long start, long end, Seekable baseStream, DataInputStream stream, Reporter reporter) throws IOException {
		this.baseStream = baseStream;
		this.stream = stream;
		this.start = start;
		this.end = end;
		this.reporter = reporter;
	}

	public void close() throws IOException {
		stream.close();
	}

	public LongWritable createKey() {
		return new LongWritable();
	}

	public ObjectWritable createValue() {
		return new ObjectWritable();
	}

	public long getPos() throws IOException {
		return baseStream.getPos();
	}

	public float getProgress() throws IOException {
		if (start == end)
			return 0;
		return Math.min(1.0f, (getPos() - start) / (float)(end - start));
	}


	public boolean next(LongWritable key, ObjectWritable value) throws IOException {
		if (start == end)
			return false;
		
		input_buffer = new byte[(int)(end - baseStream.getPos())];
		
		key.set(++blockCount);
		dstream.decompress(input_buffer, 0, (int)(end-start));
		value.set(input_buffer);
		reporter.setStatus("Read " + getPos() + " of " + end + " bytes");
		reporter.progress();

		return true;
	}
}

