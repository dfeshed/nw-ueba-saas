package fortscale.utils.kafka.kafkaTopicSyncReader.config;

import java.util.Properties;

/**
 * Created by baraks on 5/1/2016.
 */
public class KafkaTopicSyncReaderProperties {
    public static Properties getProperties()
    {
        Properties properties = new Properties();
        properties.put("fortscale.kafka.so.timeout",10000);
        properties.put("fortscale.kafka.buffer.size",100000);
        properties.put("fortscale.kafka.fetch.size",100000);
        return properties;
    }
}
