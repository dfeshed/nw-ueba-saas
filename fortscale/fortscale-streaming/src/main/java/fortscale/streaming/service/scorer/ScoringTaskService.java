package fortscale.streaming.service.scorer;

import fortscale.common.event.Event;
import fortscale.domain.core.FeatureScore;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.scorer.ScorersService;
import fortscale.streaming.service.event.EventPersistencyHandlerFactory;
import fortscale.streaming.service.topology.KafkaEventTopologyService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configurable(preConstruction = true)
public class ScoringTaskService {
    private static final Logger logger = Logger.getLogger(ScoringTaskService.class);

    private static final String OUTPUT_TOPIC_PROPERTY_KEY = "fortscale.output.topic";

    @Autowired
    private ModelsCacheService modelsCacheService;

    @Autowired
    private ScorersService scorersService;

    @Autowired
    private FeatureScoreJsonEventHandler featureScoreJsonEventHandler;

    @Autowired
    private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

    @Autowired
    private KafkaEventTopologyService eventTopologyService;

    @Value("${fortscale.bdp.run}")
    private boolean isBDPRunning;

    @Value("${streaming.event.field.type}")
    private String eventTypeFieldName;

    private String jobName;

    public ScoringTaskService(Config config, TaskContext context) throws Exception  {

        jobName = config.get("job.name");
        eventTopologyService.setSendingJobName(jobName);

        // The following initialization could also be done lazily
        scorersService.loadScorers();
    }

    public JSONObject calculateScoresAndUpdateMessage(Event event, long timestamp) throws Exception {
        List<FeatureScore> featureScores = scorersService.calculateScores(event, timestamp);

        if (featureScores != null) {
            featureScoreJsonEventHandler.updateEventWithScoreInfo(event.getJSONObject(), featureScores);
        }

        return event.getJSONObject();
    }

    public void sendEventToOutputTopic(MessageCollector collector, JSONObject message) throws Exception {
        eventTopologyService.setMessageCollector(collector);
        eventTopologyService.sendEvent(message);
     }

    public void window(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        modelsCacheService.window();
    }


    public void close() throws Exception {
        modelsCacheService.close();
    }

}
