package fortscale.streaming.service.aggregation.feature.event;

import fortscale.aggregation.feature.event.AggrEventTopologyService;
import fortscale.aggregation.feature.event.AggrFeatureEventBuilderService;
import fortscale.streaming.service.aggregation.feature.event.metrics.AggrKafkaEventTopologyServiceAggrFeatureMetrics;
import fortscale.streaming.service.aggregation.feature.event.metrics.AggrKafkaEventTopologyServiceMetrics;
import fortscale.utils.logging.Logger;
import fortscale.utils.monitoring.stats.StatsService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Assert;

import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by amira on 13/07/2015.
 */
public class AggrKafkaEventTopologyService implements AggrEventTopologyService, InitializingBean {
    private static final Logger logger = Logger.getLogger(AggrKafkaEventTopologyService.class);
    private static final String EVENT_TOPOLOGY_JSON_NODE_NAME = "event_topology";
    private static final String EVENT_FIELD_EVENT_TYPE = "aggregated_feature_type";
    private static final String WARN_MSG_NO_OUTPUT_TOPIC_FOR_EVENT_TYPE = "No output topic for event type: %s";
    private static final String ERROR_MSG_FAILED_TO_SEND_EVENT = "Failed to send event: %s";
    private static final String ERROR_MSG_NULL_EVENT = "Got a null event to send";

    private Map<String, String> eventType2kafkaQueueNameMap;

    @Value("${fortscale.aggregation.event_topology_service.event_topology_json:}")
    String eventTopologyJsonFileName;
    private MessageCollector messageCollector;
    private AggregationMetricsService aggregationMetricsService;

    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

    private Map<Pair<String, String>, AggrKafkaEventTopologyServiceAggrFeatureMetrics> aggrFeatureTypeAndNameToMetrics = new HashMap<>();
    private AggrKafkaEventTopologyServiceMetrics metrics;

    @Autowired
    private StatsService statsService;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.isNotBlank(eventTopologyJsonFileName)) {
            loadEventTopologyFromFile(eventTopologyJsonFileName);
        }
        metrics = new AggrKafkaEventTopologyServiceMetrics(statsService);
    }

    @SuppressWarnings("unchecked")
	private void loadEventTopologyFromFile(String fileName) throws IllegalArgumentException {
        try {
            JSONObject jsonObj = (JSONObject) JSONValue.parseWithException(new FileReader(fileName));
            eventType2kafkaQueueNameMap = (Map<String, String>)jsonObj.get(EVENT_TOPOLOGY_JSON_NODE_NAME);
        } catch (Exception e) {
            String errorMsg = String.format("Failed to read json conf file %s", fileName);
            logger.error(errorMsg, e);
            throw new IllegalArgumentException(errorMsg, e);
        }
    }

    public boolean sendEvent(JSONObject event) {
        Assert.notNull(messageCollector);

        if(event==null) {
            logger.error(ERROR_MSG_NULL_EVENT);
            metrics.nullEvents++;
            return false;
        }

        String eventType = (String)event.get(EVENT_FIELD_EVENT_TYPE);
        String outputTopic = eventType2kafkaQueueNameMap.get(eventType);

        if (outputTopic!=null && StringUtils.isNotEmpty(outputTopic)){
            try{
                messageCollector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), event.toJSONString()));
                getAggrFeatureMetrics(event).sent++;
                aggregationMetricsService.sentEvent(event);
            } catch(Exception exception){
                String errMsg = String.format(ERROR_MSG_FAILED_TO_SEND_EVENT, event.toString());
                logger.error(errMsg, exception);
                metrics.failedToSend++;
                //TODO: should we throw an exception here?
                return false;
            }
        } else {
            logger.warn(String.format(WARN_MSG_NO_OUTPUT_TOPIC_FOR_EVENT_TYPE, eventType));
            metrics.noOutpuTopic++;
            return false;
        }

        metrics.sent++;
        return true;
    }

    private AggrKafkaEventTopologyServiceAggrFeatureMetrics getAggrFeatureMetrics(JSONObject event) {
        ImmutablePair<String, String> typeAndName = new ImmutablePair<>(
                aggrFeatureEventBuilderService.getAggregatedFeatureType(event),
                aggrFeatureEventBuilderService.getAggregatedFeatureName(event)
        );
        if (!aggrFeatureTypeAndNameToMetrics.containsKey(typeAndName)) {
            aggrFeatureTypeAndNameToMetrics.put(typeAndName, new AggrKafkaEventTopologyServiceAggrFeatureMetrics(statsService,
                    typeAndName.getLeft(), typeAndName.getRight()));
        }
        return aggrFeatureTypeAndNameToMetrics.get(typeAndName);
    }

    public String getTopicForEventType(String eventType) {
        return eventType==null?null:eventType2kafkaQueueNameMap.get(eventType);
    }

    public void setMessageCollector(MessageCollector messageCollector) {
        this.messageCollector = messageCollector;
    }

	public void setAggregationMetricsService(AggregationMetricsService aggregationMetricsService) {
		this.aggregationMetricsService = aggregationMetricsService;
	}
}
