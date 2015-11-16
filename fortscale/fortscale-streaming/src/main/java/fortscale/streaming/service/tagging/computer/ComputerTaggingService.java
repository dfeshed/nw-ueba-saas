package fortscale.streaming.service.tagging.computer;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToString;

import java.util.Map;

import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.service.StreamingServiceAbstract;

import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import org.apache.commons.lang.StringUtils;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import net.minidev.json.JSONObject;

/**
 * Service that receive and event from a specific input topic, resolve the required classification, clustering and tagging (is sensitive machine) of the computer
 */
public class ComputerTaggingService extends StreamingServiceAbstract<ComputerTaggingConfig>{



	protected ComputerService computerService;

	protected SensitiveMachineService sensitiveMachineService;

	public ComputerTaggingService(ComputerService computerService, SensitiveMachineService sensitiveMachineService,
			Map<StreamingTaskDataSourceConfigKey, ComputerTaggingConfig>  configs) {
		checkNotNull(computerService);
		checkNotNull(sensitiveMachineService);
		checkNotNull(configs);

		this.computerService = computerService;
		this.sensitiveMachineService = sensitiveMachineService;
		this.configs = configs;
	}

	public JSONObject enrichEvent(StreamingTaskDataSourceConfigKey configKey, JSONObject event) throws FilteredEventException {
		ComputerTaggingConfig config = verifyConfigKeyAndEventFetchConfig(configKey, event, configs);


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

}
