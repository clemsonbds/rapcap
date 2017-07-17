package example;

import java.io.IOException;
import java.util.Iterator;

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
import org.apache.hadoop.mapred.TextOutputFormat;

import rapcap.hadoop.mr1.pcap.PcapInputFormat;


//import net.ripe.hadoop.pcap.io.PcapInputFormat;

public class CountPackets {

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
		JobConf conf = new JobConf(CountPackets.class);
		conf.setJobName("count packets");
		
		conf.setOutputKeyClass(Text.class);
		conf.setOutputValueClass(IntWritable.class);
		
		conf.setMapperClass(PacketCountMapper.class);
		conf.setCombinerClass(PacketCountReducer.class);
		conf.setReducerClass(PacketCountReducer.class);
		
		conf.setInputFormat(PcapInputFormat.class);
		conf.setOutputFormat(TextOutputFormat.class);
		
		FileInputFormat.setInputPaths(conf, new Path(args[0]));
		FileOutputFormat.setOutputPath(conf, new Path(args[1]));
		JobClient.runJob(conf);
	}
}
