package fortscale.streaming.task;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import fortscale.streaming.service.EventsPrevalenceModelStreamTaskService;
import fortscale.streaming.service.EventsScoreStreamTaskService;
import fortscale.streaming.service.SpringService;


/**
 * Streaming task that receive events and build a model that aggregated prevalence of fields 
 * extracted from the events. 
 */
public class EventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {

//	private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTask.class);
	
	
	private boolean skipScore;
	private boolean skipModel;
	
	private EventsPrevalenceModelStreamTaskService eventsPrevalenceModelStreamTaskService;
	private EventsScoreStreamTaskService eventsScoreStreamTaskService;
	
	
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		// get task configuration parameters
		skipScore = config.getBoolean("fortscale.skip.score", false);
		skipModel = config.getBoolean("fortscale.skip.model", false);
		
		eventsPrevalenceModelStreamTaskService = SpringService.getInstance().resolve(EventsPrevalenceModelStreamTaskService.class);
		eventsPrevalenceModelStreamTaskService.init(config, context);
		
		eventsScoreStreamTaskService = SpringService.getInstance().resolve(EventsScoreStreamTaskService.class);
		eventsScoreStreamTaskService.init(config, context);
	}
	
	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (!skipModel) {
			eventsPrevalenceModelStreamTaskService.process(envelope, collector, coordinator);
		}
		
		if(!skipScore){
			eventsScoreStreamTaskService.process(envelope, collector, coordinator);
		}
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		if(eventsPrevalenceModelStreamTaskService != null){
			eventsPrevalenceModelStreamTaskService.window(collector, coordinator);
		}
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {
		if(eventsPrevalenceModelStreamTaskService != null){
			eventsPrevalenceModelStreamTaskService.close();
		}
	}
}
