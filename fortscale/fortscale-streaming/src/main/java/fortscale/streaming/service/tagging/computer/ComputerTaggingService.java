package fortscale.streaming.service.tagging.computer;

import fortscale.domain.core.ComputerUsageType;
import fortscale.services.ComputerService;
import fortscale.services.computer.SensitiveMachineService;
import fortscale.streaming.exceptions.FilteredEventException;
import fortscale.streaming.service.StreamingTaskConfigurationService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static fortscale.utils.ConversionUtils.convertToString;

/**
 * Service that receive and event from a specific input topic, resolve the required classification and tagging (is sensitive machine) of the computer
 */
public class ComputerTaggingService extends StreamingTaskConfigurationService<ComputerTaggingConfig> {

	protected ComputerService computerService;

	protected SensitiveMachineService sensitiveMachineService;

	public ComputerTaggingService(ComputerService computerService, SensitiveMachineService sensitiveMachineService,
			Map<StreamingTaskDataSourceConfigKey, ComputerTaggingConfig>  configs) {
		super(configs);

		checkNotNull(computerService);
		checkNotNull(sensitiveMachineService);

		this.computerService = computerService;
		this.sensitiveMachineService = sensitiveMachineService;
		this.configs = configs;
	}

	public JSONObject enrichEvent(ComputerTaggingConfig config, JSONObject event) throws FilteredEventException {

		for (ComputerTaggingFieldsConfig computerTaggingFieldsConfig : config.getComputerTaggingFieldsConfigList()) {
			// get the hostname from the event
			String hostname = convertToString(event.get(computerTaggingFieldsConfig.getHostnameField()));
			if (!StringUtils.isEmpty(hostname)) {
					ensureComputerExists(hostname, computerTaggingFieldsConfig);
					updateComputerUsageType(hostname, event, computerTaggingFieldsConfig);
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
