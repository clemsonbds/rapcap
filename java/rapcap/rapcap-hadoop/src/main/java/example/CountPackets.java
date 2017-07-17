package example;

import java.io.IOException;
import java.util.Iterator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
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

import rapcap.hadoop.mr1.pcap.PcapInputFormat;


//import net.ripe.hadoop.pcap.io.PcapInputFormat;

public class CountPackets extends Configured implements Tool {

	public static class PacketCountMapper extends MapReduceBase implements Mapper<LongWritable, ObjectWritable, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text("packet");

		public void map(LongWritable key, ObjectWritable value, OutputCollector<Text, IntWritable> output,
				Reporter reporter) throws IOException {
			output.collect(word, one);
		}
	}

	public static class PacketCountReducer extends MapReduceBase implements Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterator<IntWritable> values, OutputCollector<Text, IntWritable> output,
				Reporter reporter) throws IOException {
			int sum = 0;
			while (values.hasNext())
				sum += values.next().get();

			result.set(sum);
			output.collect(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new CountPackets(), args);
        System.exit(res);
	}

	public int run(String[] args) throws Exception {
        JobConf job = (JobConf)this.getConf();

		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		job.setJobName("packet count");

		job.setJarByClass(CountPackets.class);
		job.setMapperClass(PacketCountMapper.class);
		job.setCombinerClass(PacketCountReducer.class);
		job.setReducerClass(PacketCountReducer.class);
		
		job.setInputFormat(PcapInputFormat.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);

		job.setNumReduceTasks(1);
		JobClient.runJob(job);
		return 0;
	}
}
