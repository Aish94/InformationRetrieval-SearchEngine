import java.io.IOException;
import java.util.*;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCount{
  
  //Mapper Class
  public static class WordCountMapper extends Mapper<LongWritable,Text,Text,LongWritable>
  {
    private Text word = new Text();
    public void map(LongWritable key,Text value,Context context)throws IOException,InterruptedException
    {
      String line = value.toString(); //read line
      key.set(Long.parseLong(line.substring(0,line.indexOf('\t')))); //First word befor the /t - Document ID
      StringTokenizer tokenizer = new StringTokenizer(line.substring(line.indexOf('\t') + 1)); //tokenize the rest of the document

      //For each word
      while(tokenizer.hasMoreTokens())
      {
        word.set(tokenizer.nextToken());
        context.write(word, key); //Word, DocID
      }
    }
  }

  //Reducer Class
  public static class WordCountReducer extends Reducer<Text, LongWritable, Text, Text>
  {
    //Per word
    public void reduce(Text key, Iterable<LongWritable> values, Context context)throws IOException, InterruptedException
    {
      HashMap<Long,Integer> docMap = new HashMap<Long,Integer>(); //Doc Frequency HashMap
      //Per document
      for(LongWritable value : values)
      {
        int sum = 1;
        if(docMap.containsKey(value.get()))
          sum = docMap.get(value.get()) + 1;  //inc frequency
        docMap.put(value.get(),sum);
      }
      //convert Hashmap to String
      Set set = docMap.entrySet();
      Iterator i = set.iterator();
      StringBuilder docs = new StringBuilder();
      while(i.hasNext())
      {
        Map.Entry me = (Map.Entry) i.next();
        docs.append(" " + me.getKey() + ":" + me.getValue());
      }
      context.write(key, new Text(docs.toString()));  //word, doc:freq mapping
    }
  }

  //Main Class
  public static void main(String args[])throws IOException, ClassNotFoundException, InterruptedException{
    if(args.length != 2)
    {
      System.err.println("Usage: Word Count <input path> <output path>");
      System.exit(-1);
    }
    Job job = new Job();
    job.setJarByClass(WordCount.class);
    job.setJobName("Word Count");
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    job.setMapperClass(WordCountMapper.class);
    job.setReducerClass(WordCountReducer.class);

    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(Text.class);

    job.setMapOutputKeyClass(Text.class);
    job.setMapOutputValueClass(LongWritable.class);

      job.waitForCompletion(true);
  }
}
