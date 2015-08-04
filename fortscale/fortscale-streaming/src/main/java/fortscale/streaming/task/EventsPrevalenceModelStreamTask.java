package fortscale.streaming.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import fortscale.streaming.service.EventsPrevalenceModelStreamTaskManager;


/**
 * Streaming task that receive events and build a model that aggregated prevalence of fields 
 * extracted from the events. 
 */
public class EventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {

//	private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTask.class);
	
	private EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager;
	
	
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		eventsPrevalenceModelStreamTaskManager = new EventsPrevalenceModelStreamTaskManager(config, context);
	}
	
	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		eventsPrevalenceModelStreamTaskManager.process(envelope, collector, coordinator);
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		if(eventsPrevalenceModelStreamTaskManager != null){
			eventsPrevalenceModelStreamTaskManager.window(collector, coordinator);
		}
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {
		if(eventsPrevalenceModelStreamTaskManager != null){
			eventsPrevalenceModelStreamTaskManager.close();
			eventsPrevalenceModelStreamTaskManager = null;
		}		
	}
}
