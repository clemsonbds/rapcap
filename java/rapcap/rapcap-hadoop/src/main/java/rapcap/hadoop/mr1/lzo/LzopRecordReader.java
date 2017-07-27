package rapcap.hadoop.mr1.lzo;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.BytesWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import com.hadoop.compression.lzo.LzopCodec;
import com.hadoop.compression.lzo.LzopDecompressor;

public class LzopRecordReader implements RecordReader<LongWritable, BytesWritable> {
	
	Seekable baseStream;
	DataInputStream stream;
	Reporter reporter;

	long start, next_start;
	public PublicLzopInputStream decompressor_stream;
	
	public LzopRecordReader(long start, long next_start, Seekable baseStream, InputStream stream, Reporter reporter) throws IOException {
		this.baseStream = baseStream;
		this.stream = new DataInputStream(stream);
		this.start = start;
		this.next_start = next_start;
		this.reporter = reporter;
		
		int buffer_size = LzopCodec.DEFAULT_LZO_BUFFER_SIZE;
		LzopDecompressor decompressor = new LzopDecompressor(buffer_size);
		
		baseStream.seek(0);

		decompressor_stream = new PublicLzopInputStream(stream, decompressor, buffer_size);

		baseStream.seek(start);
		decompressor_stream.resetState();
	}

	public void close() throws IOException {
		stream.close();
		decompressor_stream.close();
	}

	public LongWritable createKey() {
		return new LongWritable();
	}

	public BytesWritable createValue() {
		return new BytesWritable();
	}

	public long getPos() throws IOException {
		return baseStream.getPos();
	}

	public float getProgress() throws IOException {
		if (getPos() >= next_start)
			return 1;
		
		return Math.min(1.0f, (getPos() - start) / (float)(next_start - start));
	}

	public boolean next(LongWritable key, BytesWritable value) throws IOException {
		if (getPos() >= next_start)
			return false;

		long start_pos = getPos();
		int decompressed_size = stream.readInt();

		baseStream.seek(start_pos);
		decompressor_stream.resetState();

/*
		if (decompressor_buffer == null
		 || decompressor_buffer.length < decompressed_size) {
			decompressor_buffer = new byte[decompressed_size];
		}
*/		
		value.setSize(decompressed_size);
		long actual;

		try {
		actual = decompressor_stream.decompress(value.getBytes(), 0, decompressed_size);
		}
		catch (Exception e) {
			throw new IOException("start=" + start + ", pos=" + start_pos + ", size=" + decompressed_size + ", " + e.getMessage());
		}

		System.out.printf("rapcap: decompressing attempted=%d, actual=%d, bufferlen=%d bytes\n", decompressed_size, actual, value.getLength());
		key.set(getPos());

		reporter.setStatus("Read " + getPos() + " of " + next_start + " bytes");
		reporter.progress();

		return true;
	}
}

