package fortscale.streaming.service;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

public class EventsPrevalenceModelStreamTaskManager {
	private boolean skipScore;
	private boolean skipModel;
	
	private EventsPrevalenceModelStreamTaskService eventsPrevalenceModelStreamTaskService;
	private EventsScoreStreamTaskService eventsScoreStreamTaskService;
	
	
	
	public EventsPrevalenceModelStreamTaskManager(Config config, TaskContext context) throws Exception {
		// get task configuration parameters
		skipScore = config.getBoolean("fortscale.skip.score", false);
		skipModel = config.getBoolean("fortscale.skip.model", false);
		
		eventsPrevalenceModelStreamTaskService = new EventsPrevalenceModelStreamTaskService(config, context);		
		eventsScoreStreamTaskService = new EventsScoreStreamTaskService(config, context, eventsPrevalenceModelStreamTaskService.getModelStreamingService(), 
				eventsPrevalenceModelStreamTaskService.getFeatureExtractionService());
	}
	
	/** Process incoming events and update the user models stats */
	public void process(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		if (!skipModel) {
			eventsPrevalenceModelStreamTaskService.process(envelope, collector, coordinator);
		}
		
		if(!skipScore){
			eventsScoreStreamTaskService.process(envelope, collector, coordinator);
		}
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	public void window(MessageCollector collector, TaskCoordinator coordinator) {
		if(eventsPrevalenceModelStreamTaskService != null){
			eventsPrevalenceModelStreamTaskService.window(collector, coordinator);
		}
	}

	/** save the state to mongodb when the job shutsdown */
	public void close() throws Exception {
		if(eventsPrevalenceModelStreamTaskService != null){
			eventsPrevalenceModelStreamTaskService.close();
			eventsPrevalenceModelStreamTaskService = null;
		}
		
		eventsScoreStreamTaskService = null;
	}
}
