package fortscale.streaming.service.machineNormalization;



import fortscale.streaming.service.StreamingTaskConfigurationService;
import fortscale.streaming.service.config.StreamingTaskDataSourceConfigKey;
import net.minidev.json.JSONObject;
import java.util.List;
import java.util.Map;
import static fortscale.utils.ConversionUtils.convertToString;
import static org.python.google.common.base.Preconditions.checkNotNull;

public class MachineNormalizationService extends StreamingTaskConfigurationService<MachineNormalizationConfig> {


    private List<MachineNormalizationConfig> machineNormalizationConfigList;

    public MachineNormalizationService(Map<StreamingTaskDataSourceConfigKey, MachineNormalizationConfig> configs) {
        super(configs);
    }
    public JSONObject normalizeEvent(MachineNormalizationConfig machineNormalizationConfig,JSONObject event)
    {
        for (MachineNormalizationFieldsConfig machineNormalizationFieldsConfig: machineNormalizationConfig.getMachineNormalizationFieldsConfigs()) {
            String hostname= convertToString(event.get(machineNormalizationFieldsConfig.getHostnameField()));
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

        // strip the hostname up to the first .
        if (machineName.contains("."))
            machineName = machineName.substring(0, machineName.indexOf("."));

        return machineName.toUpperCase();
    }
    public List<MachineNormalizationConfig> getMachineNormalizationConfigList() {return machineNormalizationConfigList;}

}
