package fortscale.monitoring.metricAdaptor;

import fortscale.utils.kafka.AbstractKafkaTopicReader;
import kafka.api.FetchRequest;
import kafka.api.FetchRequestBuilder;
import kafka.javaapi.FetchResponse;
import kafka.javaapi.consumer.SimpleConsumer;
import kafka.message.MessageAndOffset;
import org.json.JSONObject;
import org.springframework.util.Assert;

import java.util.List;

/**
 * Created by baraks on 4/12/2016.
 */
public class StatsMonitoringMetricsReader extends AbstractKafkaTopicReader {

    /**
     * @param clientId  the client ID.
     * @param topic     the topic name.
     * @param partition the partition number of the topic.
     */
    public StatsMonitoringMetricsReader(String clientId, String topic, int partition) {
        super(clientId, topic, partition);
    }

    @Override
    protected void handleMessage(JSONObject message) {

    }
}
