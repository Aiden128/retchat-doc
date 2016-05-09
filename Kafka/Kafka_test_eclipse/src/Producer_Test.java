/**
 * Created by aiden on 2016/4/28.
 */

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.SystemTime;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;


public class Producer_Test extends Thread{

    public void run(){

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

        System.out.println("Start from:" + timeStamp);


        Properties props = new Properties();
        props.put("bootstrap.servers", "10.144.30.31:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        Producer<String, String> producer = new KafkaProducer<>(props);
        for(int i = 0; i < 10000; i++) {
            producer.send(new ProducerRecord<String, String>("top1", UUID.randomUUID().toString(), UUID.randomUUID().toString()));
        }

        producer.close();

        long time2 = System.currentTimeMillis();

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        System.out.println("End from:" + timeStamp);

    }

    public static void main(String[] args) throws IOException{

        Producer_Test[] threadpoo =  new Producer_Test[500];

        for(int i=0;i<500;i++){

            threadpoo[i] = new Producer_Test();
            threadpoo[i].start();
        }
    }
}
