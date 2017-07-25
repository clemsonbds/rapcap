package example;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
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

import rapcap.hadoop.mr1.lzo.LzoInputFormat;


public class ParallelDecompress extends Configured implements Tool{

	public static class ParallelDecompressMapper extends MapReduceBase implements Mapper<LongWritable, ObjectWritable, LongWritable, ObjectWritable>{

		public void map(LongWritable index, ObjectWritable data, OutputCollector<LongWritable, ObjectWritable> output,
				Reporter reporter) throws IOException {
			output.collect(index, data);
		}
		
	}
	public static class ParallelDecompressReducer extends MapReduceBase implements Reducer<LongWritable, ObjectWritable, LongWritable, Text>{
		
		Text text = new Text();
		
		public void reduce(LongWritable keys, Iterator<ObjectWritable> values, OutputCollector<LongWritable, Text> output, Reporter reporter) throws IOException {
			text.set(values.toString());
			output.collect(null, text);
		}


	}
		
	
	public static void main(String[] args) throws Exception {

        int res = ToolRunner.run(new JobConf(), new ParallelDecompress(), args);

        System.exit(res);
	}

	public int run(String[] args) throws Exception {
		JobConf job = (JobConf)this.getConf();

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setJobName("parallel decompress");

		job.setJarByClass(CountPackets.class);
		job.setMapperClass(ParallelDecompressMapper.class);
		job.setReducerClass(ParallelDecompressReducer.class);
		
		job.setInputFormat(LzoInputFormat.class);
		job.setOutputKeyClass(LongWritable.class);
		job.setOutputValueClass(Text.class);

		job.setNumReduceTasks(1);
		JobClient.runJob(job);
		return 0;
		}

}
