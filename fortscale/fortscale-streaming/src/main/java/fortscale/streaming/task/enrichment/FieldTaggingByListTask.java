package fortscale.streaming.task.enrichment;

import com.google.common.collect.Iterables;
import fortscale.services.cache.CacheHandler;
import fortscale.streaming.cache.LevelDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.tagging.FieldTaggingService;
import fortscale.streaming.service.tagging.computer.ComputerTaggingConfig;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.utils.StringPredicates;
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
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;

/**
 * Created by idanp on 3/29/2015.
 */
public class FieldTaggingByListTask extends AbstractStreamTask {


	private final static String storeConfigKeyFormat = "fortscale.events.entry.%s.service.cache.store";

	// Map between (update) input topic name and relevant field tagging service
	protected static Map<String, FieldTaggingService> topicToServiceMap;

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


		if (topicToServiceMap == null) {

			topicToServiceMap = new HashMap<>();

			// get spring environment to resolve properties values using configuration files
			Environment env = SpringService.getInstance().resolve(Environment.class);

			Map<String, ComputerTaggingConfig> configs = new HashMap<>();

			// Get configuration properties
			Config fieldsSubset = config.subset("fortscale.events.entry.name.");

			for (String dsSettings : fieldsSubset.keySet()) {
				String datasource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", dsSettings));
				String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", dsSettings));
				StreamingTaskDataSourceConfigKey configKey = new StreamingTaskDataSourceConfigKey(datasource, lastState);

				String inputTopic = getConfigString(config, String.format("fortscale.events.entry.%s.input.topic", dsSettings));
				String outputTopic = getConfigString(config, String.format("fortscale.events.entry.%s.output.topic", dsSettings));
				String partitionField = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.partition.field", dsSettings)));
				String filePath = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.file.path", dsSettings)));
				String tagFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.tag.field.name", dsSettings)));
				String taggingBaesdFieldName = env.getProperty(getConfigString(config, String.format("fortscale.events.entry.%s.tagging.based.field.name", dsSettings)));

				CacheHandler<String,String> topicCache = new LevelDbBasedCache<String, String>((KeyValueStore<String, String>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, dsSettings))),String.class);

				FieldTaggingService fieldTaggingService = new FieldTaggingService(filePath,topicCache,tagFieldName,taggingBaesdFieldName,outputTopic,partitionField);
				topicToServiceMap.put(inputTopic,fieldTaggingService);
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

		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		if (topicToServiceMap.containsKey(inputTopic)) {
			String key = (String) envelope.getKey();
			FieldTaggingService fieldTaggingService = topicToServiceMap.get(inputTopic);

			// parse the message into json
			JSONObject event = (JSONObject) JSONValue.parseWithException(messageText);

			event = fieldTaggingService.enrichEvent(event);

			// construct outgoing message
			try {
				OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", fieldTaggingService.getOutPutTopic()), fieldTaggingService.getPartitionKey(event), event.toJSONString());
				collector.send(output);
			} catch (Exception exception) {
				throw new KafkaPublisherException(String.format("failed to send event from input topic %s to output topic %s after field tagging by list", inputTopic, fieldTaggingService.getOutPutTopic()), exception);
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
