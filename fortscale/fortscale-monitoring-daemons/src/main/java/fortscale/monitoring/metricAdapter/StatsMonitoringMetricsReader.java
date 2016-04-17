package fortscale.monitoring.metricAdapter;

import fortscale.utils.kafka.AbstractKafkaTopicReader;
import org.json.JSONObject;

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
