package fortscale.streaming.task;

import fortscale.streaming.service.task.EventScoringPersistencyTaskService;
import fortscale.utils.logging.Logger;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
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
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        processedMessageCount.inc();

        // enables persist due to message in control topic
        String topic = getIncomingMessageTopicName(envelope);
        if (topic.equals(EVENT_SCORING_PERSISTENCY_TASK_CONTROL_TOPIC))
        {
            logger.info("received message={} from controlTopic={}, {}", PERFORMING_FLUSH_LOG_MSG,
                    envelope,topic);
            eventScoringPersistencyTaskService.flush();
            return;
        }

        String messageText = (String)envelope.getMessage();
        JSONObject event = (JSONObject) JSONValue.parseWithException(messageText);
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
}
