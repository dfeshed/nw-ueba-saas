package fortscale.streaming.task.enrichment;

import com.google.common.collect.Iterables;
import fortscale.domain.core.Computer;
import fortscale.services.CachingService;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import fortscale.services.computer.SensitiveMachineServiceImpl;
import fortscale.services.impl.ComputerServiceImpl;
import fortscale.streaming.cache.KeyValueDbBasedCache;
import fortscale.streaming.exceptions.KafkaPublisherException;
import fortscale.streaming.service.FortscaleValueResolver;
import fortscale.services.impl.SpringService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import fortscale.streaming.service.tagging.computer.ComputerTaggingConfig;
import fortscale.streaming.service.tagging.computer.ComputerTaggingFieldsConfig;
import fortscale.streaming.service.tagging.computer.ComputerTaggingService;
import fortscale.streaming.task.AbstractStreamTask;
import fortscale.streaming.task.monitor.MonitorMessaages;
import fortscale.utils.StringPredicates;
import net.minidev.json.JSONObject;
import org.apache.samza.config.Config;
import org.apache.samza.storage.kv.KeyValueStore;
import org.apache.samza.system.IncomingMessageEnvelope;
import org.apache.samza.system.OutgoingMessageEnvelope;
import org.apache.samza.system.SystemStream;
import org.apache.samza.task.MessageCollector;
import org.apache.samza.task.TaskContext;
import org.apache.samza.task.TaskCoordinator;

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

	protected Map<StreamingTaskDataSourceConfigKey, ComputerTaggingConfig> configs = new HashMap<>();

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



		res = SpringService.getInstance().resolve(FortscaleValueResolver.class);
		// initialize the computer tagging service only once for all streaming task instances. Since we can
		// host several task instances in this process, we want all of them to share the same computer and tagging cache
		// instances. To do so, we can have the ComputerTaggingService defined as a static member and be shared
		// for all task instances. We won't have a problem for concurrent accesses here, since samza is a single
		// threaded and all task instances run on the same thread, meaning we cannot have concurrent calls to
		// init or process methods here, so it is safe to check for initialization the way we did.
		if (computerTaggingService == null) {
			// create the computer service with the levelDB cache
			ComputerService computerService = SpringService.getInstance().resolve(ComputerServiceImpl.class);
			computerService.setCache(new KeyValueDbBasedCache<String, Computer>((KeyValueStore<String, Computer>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, computerKey))), Computer.class));
			topicToServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, computerKey)), computerService);

			// create the SensitiveMachine service with the levelDB cache
			SensitiveMachineService sensitiveMachineService = SpringService.getInstance().resolve(SensitiveMachineServiceImpl.class);
			sensitiveMachineService.setCache(new KeyValueDbBasedCache<String, String>((KeyValueStore<String, String>) context.getStore(getConfigString(config, String.format(storeConfigKeyFormat, sensitiveMachineKey))), String.class));
			topicToServiceMap.put(getConfigString(config, String.format(topicConfigKeyFormat, sensitiveMachineKey)), sensitiveMachineService);




			for (Map.Entry<String,String> configField :  config.subset("fortscale.events.entry.name.").entrySet()) {
				String configKey = configField.getValue();
				String dataSource = getConfigString(config, String.format("fortscale.events.entry.%s.data.source", configKey));
				String lastState = getConfigString(config, String.format("fortscale.events.entry.%s.last.state", configKey));
				String outputTopic = getConfigString(config, String.format("fortscale.events.entry.%s.output.topic", configKey));
				String partitionField = resolveStringValue(config, String.format("fortscale.events.entry.%s.partition.field", configKey), res);

				List<ComputerTaggingFieldsConfig> computerTaggingFieldsConfigs = new ArrayList<>();
				Config fieldsSubset = config.subset(String.format("fortscale.events.entry.%s.", configKey));
				for (String fieldConfigKey : Iterables.filter(fieldsSubset.keySet(), StringPredicates.endsWith(".hostname.field"))) {

					String tagType = fieldConfigKey.substring(0, fieldConfigKey.indexOf(".hostname.field"));

					String hostnameField = resolveStringValue(config, String.format("fortscale.events.entry.%s.%s.hostname.field", configKey, tagType), res);
					String classificationField =resolveStringValue(config, String.format("fortscale.events.entry.%s.%s.classification.field", configKey, tagType), res);
					String clusteringField = resolveStringValue(config, String.format("fortscale.events.entry.%s.%s.clustering.field", configKey, tagType), res);
					String isSensitiveMachineField = null;
					String isSensitiveMachineFieldKey = String.format("fortscale.events.entry.%s.%s.is-sensitive-machine.field", configKey, tagType);
					if (isConfigContainKey(config, isSensitiveMachineFieldKey)) {
						isSensitiveMachineField = resolveStringValue(config, isSensitiveMachineFieldKey, res);
					}
					boolean createNewComputerInstances = config.getBoolean(String.format("fortscale.events.entry.%s.%s.create-new-computer-instances", configKey, tagType));
					computerTaggingFieldsConfigs.add(new ComputerTaggingFieldsConfig(tagType, hostnameField, classificationField, clusteringField, isSensitiveMachineField, createNewComputerInstances));
				}
				configs.put(new StreamingTaskDataSourceConfigKey(dataSource,lastState), new ComputerTaggingConfig(dataSource,lastState,outputTopic, partitionField, computerTaggingFieldsConfigs));

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

		// Get the input topic- only to resolve computer caching
		String inputTopicComputerCache = envelope.getSystemStreamPartition().getSystemStream().getStream();

		if (topicToServiceMap.containsKey(inputTopicComputerCache)) {

			CachingService cachingService = topicToServiceMap.get(inputTopicComputerCache);
			cachingService.handleNewValue((String) envelope.getKey(), (String) envelope.getMessage());
		} else {
			// parse the message into json
			JSONObject message = parseJsonMessage(envelope);
			StreamingTaskDataSourceConfigKey configKey = extractDataSourceConfigKeySafe(message);
			if (configKey == null){
				taskMonitoringHelper.countNewFilteredEvents(super.UNKNOW_CONFIG_KEY, MonitorMessaages.BAD_CONFIG_KEY);
				return;
			}
			ComputerTaggingConfig config = configs.get(configKey);
			if (config == null) {
				taskMonitoringHelper.countNewFilteredEvents(configKey, MonitorMessaages.NO_STATE_CONFIGURATION_MESSAGE);
				return;
			}

			try {
				message = computerTaggingService.enrichEvent(config, message);
			} catch (Exception e){
				taskMonitoringHelper.countNewFilteredEvents(configKey,e.getMessage());
				throw e;
			}
			// construct outgoing message
			try {
				OutgoingMessageEnvelope output = new OutgoingMessageEnvelope(new SystemStream("kafka", computerTaggingService.getOutputTopic(configKey)), computerTaggingService.getPartitionKey(configKey  , message), message.toJSONString());
				handleUnfilteredEvent(message, configKey);
				collector.send(output);
			} catch (Exception exception) {
				throw new KafkaPublisherException(String.format("failed to send event from input topic %s to output topic %s after computer tagging and clustering", inputTopicComputerCache, computerTaggingService.getOutputTopic(configKey)), exception);
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
