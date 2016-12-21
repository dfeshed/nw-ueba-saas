package fortscale.streaming.task;

import fortscale.streaming.service.task.EventScoringPersistencyTaskService;
import fortscale.streaming.task.message.ProcessMessageContext;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;





public class EventScoringPersistencyTask extends AbstractStreamTask{

    private static final String PERFORMING_FLUSH_LOG_MSG = "performing event persistency flush";
    private static Logger logger = Logger.getLogger(EventScoringPersistencyTask.class);

    private EventScoringPersistencyTaskService eventScoringPersistencyTaskService;

    private Counter processedMessageCount;

    private static final String EVENT_SCORING_PERSISTENCY_TASK_CONTROL_TOPIC = "fortscale-event-scoring-persistency-stream-control";

    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        eventScoringPersistencyTaskService = new EventScoringPersistencyTaskService();
        processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-scoring-persistency-message-count");
    }

    @Override
    protected void ProcessMessage(ProcessMessageContext contextualMessage) throws Exception {
        processedMessageCount.inc();

        // enables persist due to message in control topic
        String topic = contextualMessage.getTopicName();
        if (topic.equals(EVENT_SCORING_PERSISTENCY_TASK_CONTROL_TOPIC))
        {
            logger.info("received message={} from controlTopic={}, {}", PERFORMING_FLUSH_LOG_MSG,
                    contextualMessage,topic);
            eventScoringPersistencyTaskService.flush();
            return;
        }

        JSONObject event = contextualMessage.getMessageAsJson();
        eventScoringPersistencyTaskService.saveEvent(event);
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        logger.info("wrappedWindow: {}",PERFORMING_FLUSH_LOG_MSG);
        eventScoringPersistencyTaskService.flush();
    }



    @Override
    protected void wrappedClose() throws Exception {
        if(eventScoringPersistencyTaskService != null){
            logger.info("wrappedClose: {}" ,PERFORMING_FLUSH_LOG_MSG);
            eventScoringPersistencyTaskService.flush();
        }
    }

    /**
     * @return false, cause this class input messages does not contain dataSource field.
     */
    @Override protected boolean messageShouldContainDataSourceField()
    {
        return false;
    }
}
