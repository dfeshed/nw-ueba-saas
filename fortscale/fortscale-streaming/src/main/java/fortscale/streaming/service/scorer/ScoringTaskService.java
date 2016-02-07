package fortscale.streaming.service.scorer;

import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.FeatureScore;
import fortscale.ml.scorer.ScorersService;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.event.EventPersistencyHandler;
import fortscale.streaming.service.event.EventPersistencyHandlerFactory;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.List;

@Configurable(preConstruction = true)
public class ScoringTaskService {
    private static final Logger logger = Logger.getLogger(ScoringTaskService.class);

    private static final String OUTPUT_TOPIC_PROPERTY_KEY = "fortscale.output.topic";


    private String outputTopic;
    private String bdpOutputTopic;
    private boolean forwardEvent;

    @Autowired
    private ModelsCacheService modelsCacheService;
    @Autowired
    private ScorersService scorersService;

    @Autowired
    private FeatureScoreJsonEventHandler featureScoreJsonEventHandler;

    @Autowired
    private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

    @Value("${fortscale.bdp.run}")
    private boolean isBDPRunning;

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;

    public ScoringTaskService(Config config, TaskContext context) throws Exception  {
        outputTopic = config.get(OUTPUT_TOPIC_PROPERTY_KEY, "");
        forwardEvent = true;
        if (isBDPRunning && config.containsKey("fortscale.bdp.output.topic")) {
            bdpOutputTopic = config.get("fortscale.bdp.output.topic", "");
            if (StringUtils.isEmpty(bdpOutputTopic)) {
                forwardEvent = false;
            }
        }

        // The following initialization could also be done lazily
        scorersService.loadScorers();
    }

    public JSONObject calculateScoresAndUpdateMessage(JSONObject message, long timestamp, String dataSource) throws Exception {

        List<FeatureScore> featureScores = scorersService.calculateScores(message, timestamp, dataSource);

        if (featureScores != null) {
            featureScoreJsonEventHandler.updateEventWithScoreInfo(message, featureScores);
        }

        return message;
    }

    public void sendEventToOutputTopic(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator, JSONObject message) throws Exception {

        if (StringUtils.isNotEmpty(outputTopic) || StringUtils.isNotEmpty(bdpOutputTopic)){
            // publish the event with score to the subsequent topic in the topology
            if (forwardEvent){
                try {
                    if (StringUtils.isNotEmpty(bdpOutputTopic)) {
                        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", bdpOutputTopic), message.toJSONString()));
                    } else {
                        collector.send(new OutgoingMessageEnvelope(new SystemStream("kafka", outputTopic), message.toJSONString()));
                    }

                } catch (Exception exception) {
                    throw new KafkaPublisherException(String.format("failed to send scoring message after processing the message %s.", (String)envelope.getMessage()), exception);
                }
            }
        }

     }

    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        modelsCacheService.window();
    }


    public void close() throws Exception {
        modelsCacheService.close();
    }

}
