package rapcap.hadoop.mr1;

import java.io.DataInputStream;
import java.io.IOException;

import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.Seekable;
import org.apache.hadoop.io.compress.CompressionCodec;
import org.apache.hadoop.io.compress.CompressionCodecFactory;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileSplit;
import org.apache.hadoop.mapred.InputSplit;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.RecordReader;
import org.apache.hadoop.mapred.Reporter;

import rapcap.lib.RecordBoundaryDetector;

public abstract class RecordInputFormat<K, V> extends FileInputFormat<K, V> {

	protected RecordBoundaryDetector boundaryDetector;
	private CompressionCodecFactory compressionCodecs;

	protected abstract RecordBoundaryDetector createBoundaryDetector(DataInputStream stream) throws IOException;
	protected abstract RecordReader<K, V> createRecordReader(long start, long end, Seekable baseStream, DataInputStream stream, Reporter reporter) throws IOException;

    public void configure(JobConf conf) {
    	compressionCodecs = new CompressionCodecFactory(conf);
    }

	public final RecordReader<K, V> getRecordReader(InputSplit split, JobConf conf, Reporter reporter) throws IOException {
        FileSplit fileSplit = (FileSplit)split;

        Path path = fileSplit.getPath();
        long first_byte = fileSplit.getStart();
        long next_first_byte = first_byte + fileSplit.getLength();
System.out.printf("rapcap: computing for split (%d,%d)\n", first_byte, next_first_byte);
        FSDataInputStream baseStream = path.getFileSystem(conf).open(path);
        DataInputStream stream = baseStream;

		CompressionCodecFactory factory = new CompressionCodecFactory(conf);
        final CompressionCodec codec = factory.getCodec(path);

        if (codec != null) {
            stream = new DataInputStream(codec.createInputStream(stream));
        }
        else {
        	baseStream.seek(0);
        	RecordBoundaryDetector boundaryDetector = createBoundaryDetector(stream);

        	if (first_byte == 0)
        		first_byte = boundaryDetector.getRecordStartOffset();
        	else {
            	// determine first unambiguous header index for split
    	        baseStream.seek(first_byte);
    	        long offset = boundaryDetector.detect();
System.out.printf("rapcap: adjusted first boundary (at %d) by %d bytes from %d to %d\n", baseStream.getPos(), offset, first_byte, first_byte + offset);
				first_byte += offset;
				//   	        first_byte += boundaryDetector.detect();

        	}

        	// is this the last split?
        	if (next_first_byte != path.getFileSystem(conf).getFileStatus(path).getLen()) {
        		// determine first unambiguous header index for NEXT split, remote read until that
        		baseStream.seek(next_first_byte);
        		next_first_byte += boundaryDetector.detect();
        	}
System.out.printf("rapcap: adjusted indices to (%d, %d)\n", first_byte, next_first_byte);
        }

        baseStream.seek(first_byte);
        return createRecordReader(first_byte, next_first_byte-1, baseStream, stream, reporter);
	}
	
	@Override
    protected boolean isSplitable(FileSystem fs, Path filename) {
		CompressionCodecFactory factory = new CompressionCodecFactory(fs.getConf());
		return factory.getCodec(filename) == null;
//		return compressionCodecs.getCodec(filename) == null;
    }
}
