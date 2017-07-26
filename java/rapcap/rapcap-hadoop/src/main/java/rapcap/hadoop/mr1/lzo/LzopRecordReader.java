package rapcap.hadoop.mr1.lzo;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import com.hadoop.compression.lzo.LzopCodec;
import com.hadoop.compression.lzo.LzopDecompressor;

public class LzopRecordReader implements RecordReader<LongWritable, ObjectWritable> {
	
	Seekable baseStream;
	DataInputStream stream;
	Reporter reporter;

	long start, next_start;
	public PublicLzopInputStream decompressor_stream;
	byte decompressor_buffer[];
	
	public LzopRecordReader(long start, long next_start, Seekable baseStream, DataInputStream stream, Reporter reporter) throws IOException {
		this.baseStream = baseStream;
		this.stream = stream;
		this.start = start;
		this.next_start = next_start;
		this.reporter = reporter;
		
		int buffer_size = LzopCodec.DEFAULT_LZO_BUFFER_SIZE;
		LzopDecompressor decompressor = new LzopDecompressor(buffer_size);
		
		baseStream.seek(0);
		decompressor_stream = new PublicLzopInputStream(stream, decompressor, buffer_size);
		
	}

	public void close() throws IOException {
		stream.close();
		decompressor_stream.close();
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
		if (start == next_start)
			return 1;
		
		return Math.min(1.0f, (getPos() - start) / (float)(next_start - start));
	}


	public boolean next(LongWritable key, ObjectWritable value) throws IOException {
		if (start >= next_start)
			return false;

		long start_pos = baseStream.getPos();
		int decompressed_size = stream.readInt();
		baseStream.seek(start_pos);
		
		if (decompressor_buffer == null
		 || decompressor_buffer.length < decompressed_size) {
			decompressor_buffer = new byte[decompressed_size];
		}
		
		decompressor_stream.seek(start_pos);
		decompressor_stream.decompress(decompressor_buffer, 0, decompressor_buffer.length);

		key.set(baseStream.getPos());
		value.set(decompressor_buffer);

		reporter.setStatus("Read " + getPos() + " of " + next_start + " bytes");
		reporter.progress();

		return true;
	}
}

