package fortscale.streaming.service.machineNormalization;

import fortscale.streaming.service.StreamingTaskConfigurationService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import net.minidev.json.JSONObject;

import java.util.Map;

import static fortscale.utils.ConversionUtils.convertToString;
import static org.python.google.common.base.Preconditions.checkNotNull;

public class MachineNormalizationService extends StreamingTaskConfigurationService<MachineNormalizationConfig> {


    public MachineNormalizationService(Map<StreamingTaskDataSourceConfigKey, MachineNormalizationConfig> configs) {
        super(configs);
    }
    public JSONObject normalizeEvent(MachineNormalizationConfig machineNormalizationConfig,JSONObject event)
    {
        for (MachineNormalizationFieldsConfig machineNormalizationFieldsConfig: machineNormalizationConfig.
                getMachineNormalizationFieldsConfigs()) {
            String hostnameField = machineNormalizationFieldsConfig.getHostnameField();
            String hostname = convertToString(event.get(hostnameField));
            checkNotNull(hostname, String.format("event doesn't contain hostnameField: %s, event: %s", hostnameField, event));
            normalizeMachine(hostname,event,machineNormalizationFieldsConfig);
        }
        return event;
    }

    public void normalizeMachine(String hostname, JSONObject event,MachineNormalizationFieldsConfig machineNormalizationFieldsConfig)
    {
        String normalizedMachineName=getNormalizedMachineName(hostname);
        event.put(machineNormalizationFieldsConfig.getNormalizationField(),normalizedMachineName);
    }
    public String getNormalizedMachineName(String machineName)
    {
        checkNotNull(machineName);
        logger.debug("normalizing machine name: {}",machineName);
        String normalizedMachineName = new String(machineName);
        // strip the hostname up to the first .
        if (normalizedMachineName.contains(".")) {
            normalizedMachineName = normalizedMachineName.substring(0, machineName.indexOf("."));
        }
        // string host name to contain only machine name, i.e. : machine name: DOMAIN\MACHINE-NAME01 normalized: MACHINE-NAME01
        if(normalizedMachineName.contains("\\"))
        {
            normalizedMachineName =
                    normalizedMachineName.substring(normalizedMachineName.lastIndexOf("\\"),normalizedMachineName.length());
        }

        normalizedMachineName = normalizedMachineName.toUpperCase();
        logger.debug("original machine name: {} normalized machine name: {}",machineName,normalizedMachineName);
        return normalizedMachineName;
    }

}
