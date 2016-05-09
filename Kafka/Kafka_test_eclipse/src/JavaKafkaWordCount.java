import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FlatMapFunction;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.examples.streaming.StreamingExamples;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairReceiverInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import scala.Tuple2;

/**
 * Consumes messages from one or more topics in Kafka and does wordcount.
 *
 * Usage: JavaKafkaWordCount
 * <zkQuorum> <group> <topics> <numThreads> <zkQuorum> is a list of one or more
 * zookeeper servers that make quorum <group> is the name of kafka consumer
 * group <topics> is a list of one or more kafka topics to consume from
 * <numThreads> is the number of threads the kafka consumer should use
 *
 * To run this example: `$ bin/run-example
 * org.apache.spark.examples.streaming.JavaKafkaWordCount zoo01,zoo02, \ zoo03
 * my-consumer-group topic1,topic2 1`
 */

public final class JavaKafkaWordCount {
	private static final Pattern SPACE = Pattern.compile(" ");

	private JavaKafkaWordCount() {
	}

	public static void main(String[] args) throws Exception {

		String SPARK_MASTER = "local[2]";
		/**
		 * Details:http://spark.apache.org/docs/latest/submitting-applications.
		 * html#master-urls
		 */		
		String ZOOKEEPER_SERVER = "10.144.30.31:2181";
		/**
		 * use your own zookeeper instead.
		 */
		
		String TOPICS = "top1,top2"; // could be customized
		String GROUPID = "group1";   // could be customized
		String APPNAME = "JavaKafkaWordCount"; // could be customized
		

		StreamingExamples.setStreamingLogLevels();
		SparkConf sparkConf = new SparkConf().setAppName(APPNAME).setMaster(SPARK_MASTER);
		// Create the context with 2 seconds batch size
		JavaStreamingContext jssc = new JavaStreamingContext(sparkConf, new Duration(2000));

		int numThreads = 1;
		/*
		 * Topic partitions in Kafka does not correlate to partitions of RDDs generated in Spark Streaming. 
		 * So increasing the number of topic-specific partitions in the KafkaUtils.createStream() only increases 
		 * the number of threads using which topics that are consumed within a single receiver. 
		 * It does not increase the parallelism of Spark in processing the data. 
		 * Refer to the main document for more information on that.
		 * http://spark.apache.org/docs/latest/streaming-kafka-integration.html
		 */
		
		Map<String, Integer> topicMap = new HashMap<>();
		String[] topics = (TOPICS).split(",");
		for (String topic : topics) {
			topicMap.put(topic, numThreads);
		}

		JavaPairReceiverInputDStream<String, String> messages = KafkaUtils.createStream(jssc, ZOOKEEPER_SERVER,
				GROUPID, topicMap);

		JavaDStream<String> lines = messages.map(new Function<Tuple2<String, String>, String>() {
			@Override
			public String call(Tuple2<String, String> tuple2) {
				return tuple2._2();
			}
		});

		
		// example on the website is wrong, just remind.
		JavaDStream<String> words = lines.flatMap(new FlatMapFunction<String, String>() {
			@Override
			public Iterable<String> call(String x) {
				return Arrays.asList(SPACE.split(x));
			}
		});

		JavaPairDStream<String, Integer> wordCounts = words.mapToPair(new PairFunction<String, String, Integer>() {
			@Override
			public Tuple2<String, Integer> call(String s) {
				return new Tuple2<>(s, 1);
			}
		}).reduceByKey(new Function2<Integer, Integer, Integer>() {
			@Override
			public Integer call(Integer i1, Integer i2) {
				return i1 + i2;
			}
		});

		wordCounts.print();
		jssc.start();
		jssc.awaitTermination();
	}
}