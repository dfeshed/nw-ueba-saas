package fortscale.utils.kafka;

import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;

import java.nio.charset.Charset;

public class TopicReader {

    public static void method() {
        SimpleConsumer consumer = new SimpleConsumer("localhost", 9092, 10000, 1024000, "clientId");
        long offset = 0;
        while (true) {
            FetchRequest fetchRequest = new FetchRequestBuilder()
                    .clientId("clientName")
                    .addFetch("metrics", 0, offset, 100000)
                    .build();
            FetchResponse messages = consumer.fetch(fetchRequest);
            for (MessageAndOffset msg : messages.messageSet("metrics", 1)) {
                System.out.println("consumed: " + String.valueOf(msg.offset()) + ": " +
                        new String(msg.message().payload().array(), Charset.forName("UTF-8")));
                // advance the offset after consuming each message
                offset = msg.offset();
            }
        }
    }

}