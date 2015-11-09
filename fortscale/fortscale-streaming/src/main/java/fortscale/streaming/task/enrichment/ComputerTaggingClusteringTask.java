package fortscale.streaming.task.enrichment;

import com.google.common.collect.Iterables;
import fortscale.domain.core.Computer;
import fortscale.services.CachingService;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import fortscale.services.computer.SensitiveMachineServiceImpl;
import fortscale.services.impl.ComputerServiceImpl;
import fortscale.streaming.cache.LevelDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.SpringService;
import fortscale.streaming.service.tagging.computer.ComputerTaggingConfig;
import fortscale.streaming.service.tagging.computer.ComputerTaggingFieldsConfig;
import fortscale.streaming.service.tagging.computer.ComputerTaggingService;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static fortscale.streaming.ConfigUtils.getConfigString;
import static fortscale.streaming.ConfigUtils.isConfigContainKey;

/**
 * Created by danal on 18/01/2015.
 */
public class ComputerTaggingClusteringTask extends AbstractStreamTask {

	private final static String topicConfigKeyFormat = "fortscale.%s.service.cache.topic";
	private final static String storeConfigKeyFormat = "fortscale.%s.service.cache.store";

	private final static String computerKey = "computer";
	private final static String sensitiveMachineKey = "sensitive-machine";

	protected static ComputerTaggingService computerTaggingService;

	// Map between (update) input topic name and relevant caching service
	protected static Map<String, CachingService> topicToServiceMap = new HashMap<>();

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
	@Override protected void wrappedInit(Config config, TaskContext context) throws Exception {

		// initialize the computer tagging service only once for all streaming task instances. Since we can
		// host several task instances in this process, we want all of them to share the same computer and tagging cache
		// instances. To do so, we can have the ComputerTaggingService defined as a static member and be shared
		// for all task instances. We won't have a problem for concurrent accesses here, since samza is a single
		// threaded and all task instances run on the same thread, meaning we cannot have concurrent calls to
		// init or process methods here, so it is safe to check for initialization the way we did.
		if (computerTaggingService == null) {
		// create the computer service with the levelDB cache
		ComputerService computerService = SpringService.getInstance().resolve(ComputerServiceImpl.class);
		computerService.setCache(new LevelDbBasedCache<String, Computer>((KeyValueStore<String, Computer>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, computerKey))), Computer.class));
		topicToServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, computerKey)), computerService);

		// create the SensitiveMachine service with the levelDB cache
		SensitiveMachineService sensitiveMachineService = SpringService.getInstance().resolve(SensitiveMachineServiceImpl.class);
		sensitiveMachineService.setCache(new LevelDbBasedCache<String, String>((KeyValueStore<String, String>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, sensitiveMachineKey))), String.class));
		topicToServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, sensitiveMachineKey)), sensitiveMachineService);

		// get spring environment to resolve properties values using configuration files
		Environment env = SpringService.getInstance().resolve(Environment.class);

		Map<String, ComputerTaggingConfig> configs = new HashMap<>();

		Config configSubset = config.subset("fortscale.events.");
		for (String configKey : Iterables.filter(configSubset.keySet(), StringPredicates.endsWith(".input.topic"))) {

			String eventType = configKey.substring(0, configKey.indexOf(".input.topic"));

			String inputTopic = getConfigString(config, String.format("fortscale.events.%s.input.topic", eventType));
			String outputTopic = getConfigString(config, String.format("fortscale.events.%s.output.topic", eventType));
			String partitionField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.partition.field", eventType)));

			List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigs = new ArrayList<>();
			Config fieldsSubset = config.subset(String.format("fortscale.events.%s.", eventType));
			for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".hostname.field"))) {

				String tagType = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".hostname.field"));

				String hostnameField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.%s.hostname.field", eventType, tagType)));
				String classificationField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.%s.classification.field", eventType, tagType)));
				String clusteringField = env.getProperty(getConfigString(config, String.format("fortscale.events.%s.%s.clustering.field", eventType, tagType)));
				String isSensitiveMachineField = null;
				String isSensitiveMachineFieldKey = String.format("fortscale.events.%s.%s.is-sensitive-machine.field", eventType, tagType);
				if (isConfigContainKey(config, isSensitiveMachineFieldKey)) {
					isSensitiveMachineField = env.getProperty(getConfigString(config, isSensitiveMachineFieldKey));
				}
				boolean createNewComputerInstances = config.getBoolean(String.format("fortscale.events.%s.%s.create-new-computer-instances", eventType, tagType));
				computerTaggingFieldsConfigs.add(new ComputerTaggingFieldsConfig(tagType, hostnameField, classificationField, clusteringField, isSensitiveMachineField, createNewComputerInstances));
			}
			configs.put(inputTopic, new ComputerTaggingConfig(eventType, inputTopic, outputTopic, partitionField, computerTaggingFieldsConfigs));

		}

		computerTaggingService = new ComputerTaggingService(computerService, sensitiveMachineService, configs);
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
	protected void wrappedProcess(IncomingMessageEnvelope envelope, MessageCollector collector,
			TaskCoordinator coordinator) throws Exception {

		// get message
	//	String messageText = (String) envelope.getMessage();

		// Get the input topic
		String inputTopic = envelope.getSystemStreamPartition().getSystemStream().getStream();

		if (topicToServiceMap.containsKey(inputTopic)) {
			String key = (String) envelope.getKey();
			CachingService cachingService = topicToServiceMap.get(inputTopic);
			cachingService.handleNewValue((String) envelope.getKey(), (String) envelope.getMessage());
		} else {
			// parse the message into json
			JSONObject event = parseJsonMessage(envelope);

			try {
				event = computerTaggingService.enrichEvent(inputTopic, event);
			} catch (Exception e){
				taskMonitoringHelper.countNewFilteredEvents(e.getMessage());
				throw e;
			}
			// construct outgoing message
			try {
				OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", computerTaggingService.getOutputTopic(inputTopic)), computerTaggingService.getPartitionKey(inputTopic, event), event.toJSONString());
				taskMonitoringHelper.handleUnFilteredEvents(getDataSource(event),event.getAsNumber("date_time_unix"), event.getAsString("date_time"));
				collector.send(output);
			} catch (Exception exception) {
				throw new KafkaPublisherException(String.format("failed to send event from input topic %s to output topic %s after computer tagging and clustering", inputTopic, computerTaggingService.getOutputTopic(inputTopic)), exception);
			}
		}
	}

	@Override
	protected void wrappedWindow(MessageCollector collector, TaskCoordinator coordinator) throws Exception {

	}

	@Override
	protected String getJobLabel() {
		return "ComputerTaggingClusteringTask";
	}

	@Override
	protected void wrappedClose() throws Exception {
		for(CachingService cachingService: topicToServiceMap.values()) {
			cachingService.getCache().close();
		}
		topicToServiceMap.clear();
	}
}
