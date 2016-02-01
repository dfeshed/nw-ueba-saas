package fortscale.streaming.task.enrichment;

import fortscale.services.cache.CacheHandler;
import fortscale.streaming.cache.KeyValueDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.services.impl.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.tagging.FieldTaggingService;
import fortscale.streaming.service.tagging.computer.ComputerTaggingConfig;
import fortscale.streaming.task.AbstractStreamTask;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

import java.util.HashMap;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;

/**
 * Created by idanp on 3/29/2015.
 */
public class FieldTaggingByListTask extends AbstractStreamTask {


	private final static String storeConfigKeyFormat = "fortscale.events.entry.%s.service.cache.store";

	// Map between (update) input topic name and relevant field tagging service
	protected static Map<StreamingTaskDataSourceConfigKey, FieldTaggingService> topicToServiceMap;

	/**
	 * This method response to the initiation of the streaming job
	 * First step is to create the caching based on spring configuration
	 * Then we retrieve the needed values from the property  file
	 * Last step is to create the tagging service that will handel the entire logeic at the process part
	 *
	 * @param config  - represent the config from the Samza framework based on the task property file
	 * @param context
	 * @throws Exception
	 */
	@Override
	protected void wrappedInit(Config config, TaskContext context) throws Exception {


		res = SpringService.getInstance().resolve(FortscaleValueResolver.class);

		if (topicToServiceMap == null) {

			topicToServiceMap = new HashMap<>();

			// get spring environment to resolve properties values using configuration files


			Map<String, ComputerTaggingConfig> configs = new HashMap<>();

			// Get configuration properties
			Config fieldsSubset = config.subset("fortscale.events.entry.name.");

			for (String dsSettings : fieldsSubset.keySet()) {
				String datasource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", dsSettings));
				String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", dsSettings));
				StreamingTaskDataSourceConfigKey configKey = new StreamingTaskDataSourceConfigKey(datasource, lastState);
				String outputTopic = getConfigString(config, String.format("fortscale.events.entry.%s.output.topic", dsSettings));
				String partitionField = resolveStringValue(config, String.format("fortscale.events.entry.%s.partition.field", dsSettings),res);
				String filePath = resolveStringValue(config, String.format("fortscale.events.entry.%s.file.path", dsSettings),res);
				String tagFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.tag.field.name", dsSettings),res);
				String taggingBaesdFieldName = resolveStringValue(config, String.format("fortscale.events.entry.%s.tagging.based.field.name", dsSettings),res);

				CacheHandler<String,String> topicCache = new KeyValueDbBasedCache<String, String>((KeyValueStore<String, String>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, dsSettings))),String.class);

				FieldTaggingService fieldTaggingService = new FieldTaggingService(filePath,topicCache,tagFieldName,taggingBaesdFieldName,outputTopic,partitionField);
				topicToServiceMap.put(configKey,fieldTaggingService);
			}
		}
	}

	/**
	 * This is the process part of the Samza job
	 * At this part we retrieve message from the needed topic and based on the input topic we start the needed service
	 * @param envelope
	 * @param collector
	 * @param coordinator
	 * @throws Exception
	 */
	@Override
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,TaskCoordinator coordinator) throws Exception {

		// get message
		String messageText = (String) envelope.getMessage();

		// parse the message into json
		JSONObject event = (JSONObject) JSONValue.parseWithException(messageText);


		StreamingTaskDataSourceConfigKey key = this.extractDataSourceConfigKeySafe(event);

		if (key != null && topicToServiceMap.containsKey(key)) {

			FieldTaggingService fieldTaggingService = topicToServiceMap.get(key);
			event = fieldTaggingService.enrichEvent(event);

			// construct outgoing message
			try {
				OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", fieldTaggingService.getOutPutTopic()), fieldTaggingService.getPartitionKey(event), event.toJSONString());
				collector.send(output);
			} catch (Exception exception) {
				throw new KafkaPublisherException(String.format("failed to send event from State %s to output topic %s after field tagging by list", key, fieldTaggingService.getOutPutTopic()), exception);
			}
		}

	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

	}

	@Override
	protected void wrappedClose() throws Exception {
		for(FieldTaggingService fieldTaggingService: topicToServiceMap.values()) {
			fieldTaggingService.close();
		}
		topicToServiceMap.clear();
	}
}
