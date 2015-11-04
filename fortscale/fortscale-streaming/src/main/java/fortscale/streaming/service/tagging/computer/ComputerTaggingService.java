package fortscale.streaming.service.tagging.computer;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Service that receive and event from a specific input topic, resolve the required classification, clustering and tagging (is sensitive machine) of the computer
 */
public class ComputerTaggingService {

	protected ComputerService computerService;

	protected SensitiveMachineService sensitiveMachineService;

	private Map<String, ComputerTaggingConfig> configs = new HashMap<>();

	public static Logger logger = LoggerFactory.getLogger(ComputerTaggingService.class);

	public ComputerTaggingService(ComputerService computerService, SensitiveMachineService sensitiveMachineService,
			Map<String, ComputerTaggingConfig>  configs) {
		checkNotNull(computerService);
		checkNotNull(sensitiveMachineService);
		checkNotNull(configs);

		this.computerService = computerService;
		this.sensitiveMachineService = sensitiveMachineService;
		this.configs = configs;
	}

	public JSONObject enrichEvent(String dataSource, JSONObject event) {
		checkNotNull(dataSource);
		checkNotNull(event);
		ComputerTaggingConfig config = configs.get(dataSource);
		if (config == null) {
			logger.error("received event from data source {} that does not appear in configuration", dataSource);
			return event;
		}

		for (ComputerTaggingFieldsConfig computerTaggingFieldsConfig : config.getComputerTaggingFieldsConfigList()) {
			// get the hostname from the event
			String hostname = convertToString(event.get(computerTaggingFieldsConfig.getHostnameField()));
			if (!StringUtils.isEmpty(hostname)) {
					ensureComputerExists(hostname, computerTaggingFieldsConfig);
					updateComputerUsageType(hostname, event, computerTaggingFieldsConfig);
					updateComputerCluster(hostname, event, computerTaggingFieldsConfig);
			}
			updateIsMachineSensitive(hostname, event, computerTaggingFieldsConfig);
		}
		return event;
	}

	/** ensure computer exists */
	private void ensureComputerExists(String hostname, ComputerTaggingFieldsConfig computerTaggingFieldsConfig){
		if (computerTaggingFieldsConfig.isCreateNewComputerInstances()) {
			computerService.ensureComputerExists(hostname);
		}
	}

	/** lookup the hostname and get the usage type */
	private void updateComputerUsageType(String hostname, JSONObject event, ComputerTaggingFieldsConfig computerTaggingFieldsConfig){
		if (computerTaggingFieldsConfig.runClassification()) {
			ComputerUsageType usage = computerService.getComputerUsageType(hostname);
			event.put(computerTaggingFieldsConfig.getClassificationField(), usage == null ? ComputerUsageType.Unknown : usage);
		}
	}

	/** lookup the hostname and get the cluster */
	private void updateComputerCluster(String hostname, JSONObject event, ComputerTaggingFieldsConfig computerTaggingFieldsConfig){
		if (computerTaggingFieldsConfig.runClustering()) {
			String clusterName = computerService.getClusterGroupNameForHostname(hostname);
			event.put(computerTaggingFieldsConfig.getClusteringField(), clusterName);
		}
	}

	/** lookup the hostname and check if sensitive machine */
	private void updateIsMachineSensitive(String hostname, JSONObject event, ComputerTaggingFieldsConfig computerTaggingFieldsConfig) {
		if (computerTaggingFieldsConfig.runIsSensitiveMachine()) {
			boolean isSensitive = false;
			if (!StringUtils.isEmpty(hostname)) {
				if (sensitiveMachineService != null) {
					// lookup the hostname in the sensitive machines
					isSensitive = sensitiveMachineService.isMachineSensitive(hostname);
				}
			}
			event.put(computerTaggingFieldsConfig.getIsSensitiveMachineField(), isSensitive);
		}
	}

	public String getOutputTopic(String dataSource) {
		if (configs.containsKey(dataSource))
			return configs.get(dataSource).getOutputTopic();
		else
			throw new RuntimeException("received events from data source " + dataSource + " that does not appear in configuration");
	}

	/** Get the partition key to use for outgoing message envelope for the given event */
	public Object getPartitionKey(String dataSource, JSONObject event) {
		checkNotNull(dataSource);
		checkNotNull(event);

		// get the configuration for the data source, if not found skip this event
		ComputerTaggingConfig config = configs.get(dataSource);
		if (config==null) {
			logger.error("received event from data source {} that does not appear in configuration", dataSource);

			return null;
		}

		return event.get(config.getPartitionField());
	}
}
