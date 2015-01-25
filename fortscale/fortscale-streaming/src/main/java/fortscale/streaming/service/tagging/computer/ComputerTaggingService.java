package fortscale.streaming.service.tagging.computer;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.CachingService;
import fortscale.services.ComputerService;
import fortscale.services.SensitiveMachineService;
import fortscale.streaming.service.ipresolving.EventResolvingConfig;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Service that receive and event from a specific input topic, resolve the required classification, clustering and tagging (is sensitive machine) of the computer
 */
public class ComputerTaggingService {

	protected ComputerService computerService;

	protected SensitiveMachineService sensitiveMachineService;

	private Map<String, ComputerTaggingConfig> configs = new HashMap<>();

	public ComputerTaggingService(ComputerService computerService, SensitiveMachineService sensitiveMachineService,
			Map<String, ComputerTaggingConfig>  configs) {
		checkNotNull(computerService);
		checkNotNull(sensitiveMachineService);
		checkNotNull(configs);

		this.computerService = computerService;
		this.sensitiveMachineService = sensitiveMachineService;
		this.configs = configs;
	}

	public JSONObject enrichEvent(String inputTopic, JSONObject event) {
		checkNotNull(inputTopic);
		checkNotNull(event);
		ComputerTaggingConfig config = configs.get(inputTopic);
		if (config == null) {
			return event;
		}

		for (ComputerTaggingFieldsConfig computerTaggingFieldsConfig : config.getComputerTaggingFieldsConfigList()) {
			// get the hostname from the event
			String hostname = convertToString(event.get(computerTaggingFieldsConfig.getHostnameField()));
			if (!StringUtils.isEmpty(hostname)) {
				if (computerService != null) {
					ensureComputerExists(hostname, computerTaggingFieldsConfig);
					updateComputerUsageType(hostname, event, computerTaggingFieldsConfig);
					updateComputerCluster(hostname, event, computerTaggingFieldsConfig);
				}
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
		if (!StringUtils.isEmpty(computerTaggingFieldsConfig.getClassificationField())) {
			ComputerUsageType usage = computerService.getComputerUsageType(hostname);
			event.put(computerTaggingFieldsConfig.getClassificationField(), usage == null ? ComputerUsageType.Unknown : usage);
		}
	}

	/** lookup the hostname and get the cluster */
	private void updateComputerCluster(String hostname, JSONObject event, ComputerTaggingFieldsConfig computerTaggingFieldsConfig){
		if (!StringUtils.isEmpty(computerTaggingFieldsConfig.getClusteringField())) {
			String clusterName = computerService.getClusterGroupNameForHostname(hostname);
			event.put(computerTaggingFieldsConfig.getClusteringField(), clusterName);
		}
	}

	/** lookup the hostname and check if sensitive machine */
	private void updateIsMachineSensitive(String hostname, JSONObject event, ComputerTaggingFieldsConfig computerTaggingFieldsConfig) {
		boolean isSensitive = false;
		if (!StringUtils.isEmpty(hostname)) {
			if (sensitiveMachineService != null) {
				// lookup the hostname in the sensitive machines
				isSensitive = sensitiveMachineService.isMachineSensitive(hostname);
			}
		}
		if (!StringUtils.isEmpty(computerTaggingFieldsConfig.getIsSensitiveMachineField())) {
			event.put(computerTaggingFieldsConfig.getIsSensitiveMachineField(), isSensitive);
		}
	}

	public String getOutputTopic(String inputTopic) {
		return (configs.containsKey(inputTopic))? configs.get(inputTopic).getOutputTopic() : null;
	}

	/** Get the partition key to use for outgoing message envelope for the given event */
	public Object getPartitionKey(String inputTopic, JSONObject event) {
		checkNotNull(inputTopic);
		checkNotNull(event);

		// get the configuration for the input topic, if not found skip this event
		ComputerTaggingConfig config = configs.get(inputTopic);
		if (config==null)
			return event;

		return event.get(config.getPartitionField());
	}
}
