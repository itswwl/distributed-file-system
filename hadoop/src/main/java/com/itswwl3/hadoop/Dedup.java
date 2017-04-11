package com.itswwl3.hadoop;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;



/*
 * 在Eclipse中运行第一个MapReduce程序
 * http://sqcjy111.iteye.com/blog/1735203
 * 
 * eclipse新建DFS location设置
 * http://blog.csdn.net/melody_1314/article/details/7619605
 * 
 * 这个例子，hadoop并没有使用远程的hadoop，而是读取本项目下的路径，应该配置读取路径为hadoop下的
 * 
 * */

/**
 * @author Administrator
 *
 *
 * _MapReduce初级案例
 * http://www.cnblogs.com/wang3680/p/943a6ee448ec121d9f2650baec86a876.html
 * 
 */
public class Dedup {

	// map将输入中的value复制到输出数据的key上，并直接输出
	public static class Map extends Mapper<Object, Text, Text, Text> {
		private static Text line = new Text();// 每行数据

		// 实现map函数
		public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
			line = value;
			context.write(line, new Text(""));
		}

	}

	// reduce将输入中的key复制到输出数据的key上，并直接输出
	public static class Reduce extends Reducer<Text, Text, Text, Text> {
		// 实现reduce函数
		public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
			context.write(key, new Text(""));
		}

	}

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		// 这句话很关键  
		//不清楚这个配置中ip和端口究竟指的是什么？注释掉掉也能运行
//		conf.set("mapred.job.tracker", "localhost:9001");

		String[] ioArgs = new String[] { "dedup_in", "dedup_out" };
		String[] otherArgs = new GenericOptionsParser(conf, ioArgs).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println("Usage: Data Deduplication <in> <out>");
			System.exit(2);
		}

		Job job = new Job(conf, "Data Deduplication");
		job.setJarByClass(Dedup.class);

		// 设置Map、Combine和Reduce处理类
		job.setMapperClass(Map.class);
		job.setCombinerClass(Reduce.class);
		job.setReducerClass(Reduce.class);

		// 设置输出类型
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		// 设置输入和输出目录
		FileInputFormat.addInputPath(job, new Path(otherArgs[0]));
		FileOutputFormat.setOutputPath(job, new Path(otherArgs[1]));
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}
}
