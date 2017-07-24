package example;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Iterator;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobClient;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.MapReduceBase;
import org.apache.hadoop.mapred.Mapper;
import org.apache.hadoop.mapred.OutputCollector;
import org.apache.hadoop.mapred.Reducer;
import org.apache.hadoop.mapred.Reporter;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.hadoop.fs.FSDataInputStream;

import rapcap.hadoop.mr1.lzo.LzoInputFormat;
import rapcap.hadoop.mr1.lzo.PublicLzopInputStream;
import rapcap.lib.lzo.LzopBoundaryDetector;
import com.hadoop.compression.lzo.LzopDecompressor;
	

public class ParallelDecompress extends Configured implements Tool{

	public static String fileName;
	public static FSDataInputStream stream;

	public static class ParallelDecompressMapper extends MapReduceBase implements Mapper<LongWritable, LongWritable, LongWritable, ObjectWritable>{

		private LzopBoundaryDetector detect;
		private LzopDecompressor lz;
		private static final LongWritable point = new LongWritable(2);
		private long pointerLong;

		private byte bytebuff[] = new byte[(256*1024)];
		private ObjectWritable outputbuff = new ObjectWritable(bytebuff);
		

		private PublicLzopInputStream dstream;
		private long offset(){
			return detect.getRecordStartOffset();
		}

		int incl_len;
		ParallelDecompressMapper() throws IOException{
			dstream = new PublicLzopInputStream(stream, lz, (256*1024));
		}
		
		public void map(LongWritable index, LongWritable pointer, OutputCollector<LongWritable, ObjectWritable> output,
				Reporter reporter) throws IOException {
			pointerLong = pointer.get();
			
			stream.seek((offset() + 4));
			incl_len = stream.readInt();
			
			dstream.seek(pointerLong);
			dstream.decompress(bytebuff, 0, incl_len);
			
			outputbuff.set(bytebuff);
			
			output.collect(point, outputbuff);
		}
		
	}
	public static class ParallelDecompressReducer extends MapReduceBase implements Reducer<LongWritable, ObjectWritable, IntWritable, ObjectWritable>{

		public void reduce(LongWritable keys, Iterator<ObjectWritable> values,
				OutputCollector<IntWritable, ObjectWritable> output, Reporter reporter) throws IOException {
			File file = new File(fileName);
			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
			BufferedWriter bw = new BufferedWriter(fw);
			while (values.hasNext()){
				bw.write(values.toString());
				values.next();
			}
			bw.close();
		}
		
	}
	
	public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new JobConf(), new ParallelDecompress(), args);

        System.exit(res);
	}

	public int run(String[] args) throws Exception {
		fileName = args[2];
		Path path = new Path(args[0]);
		stream = new FSDataInputStream(path.getFileSystem(getConf()).open(path));
		JobConf job = (JobConf)this.getConf();

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setJobName("parallel decompress");

		job.setJarByClass(CountPackets.class);
		job.setMapperClass(ParallelDecompressMapper.class);
		job.setReducerClass(ParallelDecompressReducer.class);
		
		job.setInputFormat(LzoInputFormat.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(ObjectWritable.class);

		job.setNumReduceTasks(1);
		JobClient.runJob(job);
		return 0;
		}

}
