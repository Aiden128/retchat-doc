import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by aiden on 2016/4/28.
 */
public class Consumer_Test {

    public static void main(String[] args){

        Properties props = new Properties();
        props.put("bootstrap.servers", "10.144.30.31:9092");
        props.put("group.id", "test");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("session.timeout.ms", "30000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        consumer.subscribe(Arrays.asList("mytopic"));

        HashMap<String, Integer> list = new HashMap<String, Integer>();

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {

                String value  = record.value();
                //System.out.printf("offset = %d, key = %s, value = %s \n", record.offset(), record.key(), value);

                String ipkey = value.split("-")[0];

                Integer i = -1;
                if(list.containsKey(ipkey)){
                    i = list.get(ipkey);
                }
                list.put(ipkey, ++i);

                for (String s:list.keySet()) {
                    System.out.println(String.format("IP:%s, Count=%d", s, list.get(s)));
                }
            }
        }
    }
}
