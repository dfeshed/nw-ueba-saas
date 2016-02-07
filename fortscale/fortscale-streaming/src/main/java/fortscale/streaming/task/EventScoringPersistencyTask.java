package fortscale.streaming.task;

import fortscale.streaming.service.task.EventScoringPersistencyTaskService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.metrics.Counter;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;





public class EventScoringPersistencyTask extends AbstractStreamTask{

    private EventScoringPersistencyTaskService eventScoringPersistencyTaskService;

    private Counter processedMessageCount;

    @Override
    protected void wrappedInit(Config config, TaskContext context) throws Exception {
        eventScoringPersistencyTaskService = new EventScoringPersistencyTaskService();
        processedMessageCount = context.getMetricsRegistry().newCounter(getClass().getName(), "event-scoring-persistency-message-count");
    }

    @Override
    protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
        processedMessageCount.inc();

        String messageText = (String)envelope.getMessage();
        JSONObject event = (JSONObject) JSONValue.parseWithException(messageText);
        eventScoringPersistencyTaskService.saveEvent(event);
    }

    @Override
    protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

    }



    @Override
    protected void wrappedClose() throws Exception {

    }
}
