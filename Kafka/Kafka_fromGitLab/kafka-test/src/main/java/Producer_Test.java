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
            producer.send(new ProducerRecord<String, String>("topic-2", UUID.randomUUID().toString(), UUID.randomUUID().toString()));
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


//
//        KafkaProducer<String, String> producer;
//        try (InputStream props = Resources.getResource("producer.props").openStream()) {
//            Properties properties = new Properties();
//            properties.load(props);
//            producer = new KafkaProducer<>(properties);
//        }
//
//        for(int i=0;i<100;i++)
//            producer.send(new ProducerRecord<String, String>("topic1", String.format("This is the %d message", i)));

//        try {
//            for (int i = 0; i < 1000000; i++) {
//                // send lots of messages
//                producer.send(new ProducerRecord<String, String>(
//                        "fast-messages",
//                        String.format("{\"type\":\"test\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
//
//                // every so often send to a different topic
//                if (i % 1000 == 0) {
//                    producer.send(new ProducerRecord<String, String>(
//                            "fast-messages",
//                            String.format("{\"type\":\"marker\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
//                    producer.send(new ProducerRecord<String, String>(
//                            "summary-markers",
//                            String.format("{\"type\":\"other\", \"t\":%.3f, \"k\":%d}", System.nanoTime() * 1e-9, i)));
//                    producer.flush();
//                    System.out.println("Sent msg number " + i);
//                }
//            }
//        } catch (Throwable throwable) {
//            System.out.printf("%s", throwable.getStackTrace());
//        } finally {
//            producer.close();
//        }

    }
}
