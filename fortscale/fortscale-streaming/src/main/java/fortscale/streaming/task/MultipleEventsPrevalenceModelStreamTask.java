package fortscale.streaming.task;

import static fortscale.streaming.ConfigUtils.getConfigString;

import java.util.HashMap;
import java.util.Map;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import com.google.common.collect.Iterables;

import fortscale.streaming.service.EventsPrevalenceModelStreamTaskManager;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.SpringService;
import fortscale.utils.StringPredicates;

public class MultipleEventsPrevalenceModelStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {

//	private static final Logger logger = LoggerFactory.getLogger(EventsPrevalenceModelStreamTask.class);
	
	private Map<String, EventsPrevalenceModelStreamTaskManager> dataSourceToEventsPrevalenceModelStreamTaskManagerMap = new HashMap<String, EventsPrevalenceModelStreamTaskManager>();
	private Map<String, String> inputTopicTodataSourceMap = new HashMap<String, String>();
	
	
	
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {
		FortscaleStringValueResolver res = SpringService.getInstance().resolve(FortscaleStringValueResolver.class);
		
		// Get configuration properties
		Config fieldsSubset = config.subset("fortscale.");
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".fortscale.input.topic"))) {
			String dataSource = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".fortscale.input.topic"));
			Config dataSourceConfig = fieldsSubset.subset(String.format("%s.", dataSource));
			EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = new EventsPrevalenceModelStreamTaskManager(dataSourceConfig, context);
			dataSourceToEventsPrevalenceModelStreamTaskManagerMap.put(dataSource, eventsPrevalenceModelStreamTaskManager);
			String inputTopic = resolveStringValue(dataSourceConfig, "fortscale.input.topic", res);
			inputTopicTodataSourceMap.put(inputTopic, dataSource);
		}

	}
	
	private String resolveStringValue(Config config, String string, FortscaleStringValueResolver resolver) {
		return resolver.resolveStringValue(getConfigString(config, string));
	}
	
	/** Process incoming events and update the user models stats */
	@Override public void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		String dataSource = inputTopicTodataSourceMap.get(inputTopic);
		EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager = dataSourceToEventsPrevalenceModelStreamTaskManagerMap.get(dataSource);
		eventsPrevalenceModelStreamTaskManager.process(envelope, collector, coordinator);
	}

	
	
	/** periodically save the state to mongodb as a secondary backing store */
	@Override public void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) {
		for(EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager: dataSourceToEventsPrevalenceModelStreamTaskManagerMap.values()){
			eventsPrevalenceModelStreamTaskManager.window(collector, coordinator);
		}
	}

	/** save the state to mongodb when the job shutsdown */
	@Override protected void wrappedClose() throws Exception {
		for(EventsPrevalenceModelStreamTaskManager eventsPrevalenceModelStreamTaskManager: dataSourceToEventsPrevalenceModelStreamTaskManagerMap.values()){
			eventsPrevalenceModelStreamTaskManager.close();
		}
		dataSourceToEventsPrevalenceModelStreamTaskManagerMap.clear();
	}
}
