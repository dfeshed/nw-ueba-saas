package fortscale.streaming.task;

import java.util.HashMap;
import java.util.Map;

import fortscale.streaming.service.aggregation.AggrEventTopologyService;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import org.apache.samza.config.Config;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.task.ClosableTask;
import org.apache.samza.task.InitableTask;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import com.google.common.collect.Iterables;

import fortscale.streaming.ExtendedSamzaTaskContext;
import fortscale.streaming.service.FortscaleStringValueResolver;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.aggregation.AggregatorManager;
import fortscale.utils.StringPredicates;
import org.springframework.beans.factory.annotation.Autowired;

import static fortscale.streaming.ConfigUtils.getConfigString;

public class AggregationEventsStreamTask extends AbstractStreamTask implements InitableTask, ClosableTask {
	private AggregatorManager aggregatorManager;
	private Map<String, String> topicToDataSourceMap = new HashMap<String, String>();
	private String dataSourceFieldName;

	@Autowired
	AggrEventTopologyService aggrEventTopologyService;


	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {		
		FortscaleStringValueResolver res = SpringService.getInstance().resolve(FortscaleStringValueResolver.class);
		
		
		Config fieldsSubset = config.subset("fortscale.");
		for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".input.topic"))) {
			String eventType = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".input.topic"));
			String inputTopic = getConfigString(config, String.format("fortscale.%s.input.topic", eventType));
			topicToDataSourceMap.put(inputTopic, eventType);
		}
		
		dataSourceFieldName = resolveStringValue(config, "fortscale.data.source.field", res);
		
		aggregatorManager = new AggregatorManager(config, new ExtendedSamzaTaskContext(context));
	}
	
	private String resolveStringValue(Config config, String string, FortscaleStringValueResolver resolver) {
		return resolver.resolveStringValue(getConfigString(config, string));
	}


	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		// Get the input topic
		String topic = envelope.getSystemStreamPartition().getSystemStream().getStream();
		// Get Event
		String messageText = (String)envelope.getMessage();
		JSONObject event = (JSONObject)JSONValue.parseWithException(messageText);
		
		//Add data source to the event. In the future it should already be part of the event.
		if(!event.containsKey(dataSourceFieldName)){
			event.put(dataSourceFieldName, topicToDataSourceMap.get(topic));
		}

		aggrEventTopologyService.setMessageCollector(collector);

		aggregatorManager.processEvent(event);
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {
		aggrEventTopologyService.setMessageCollector(collector);

		if (aggregatorManager != null) {
			aggregatorManager.window(collector, coordinator);
		}
	}

	@Override
	protected void wrappedClose() throws Exception {
		if (aggregatorManager != null) {
			aggregatorManager.close();
			aggregatorManager = null;
		}
	}
}
