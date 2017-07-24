package example;

/*import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.ObjectWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

//import net.ripe.hadoop.pcap.io.PcapInputFormat;
import rapcap.hadoop.mr1.pcap.PcapInputFormat;

//import net.ripe.hadoop.pcap.io.PcapInputFormat;

public class SplitCountPacketsLocal extends Configured implements Tool {

	public static class PacketCountMapper extends Mapper<LongWritable, ObjectWritable, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text("packet");

		public void map(LongWritable key, ObjectWritable value, Context context)
				throws IOException, InterruptedException {
			context.write(word, one);
		}
	}

	public static class PacketCountReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values, Context context)
				throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new SplitCountPacketsLocal(), args);
        System.exit(res);
	}

	public int run(String[] args) throws Exception {
		Job job = Job.getInstance(this.getConf(), "packet count");
		job.setInputFormatClass(PcapInputFormat.class);
		job.setJarByClass(SplitCountPacketsLocal.class);
		job.setMapperClass(PacketCountMapper.class);
		job.setCombinerClass(PacketCountReducer.class);
		job.setReducerClass(PacketCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));

		return job.waitForCompletion(true) ? 0 : 1;
	}
}
*/