package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.FortscaleNoConfigurationException;
import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.IpResolvingTaskConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.List;
import java.util.Map;

/**
 * IP Resolving configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSIPResolvingConfigurator extends GDSBaseConfigurator {

    private static final String SOURCE_IP_CONFIG_ENTRY = "source.";
    private static final String TARGET_IP_CONFIG_ENTRY = "target.";
	private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topic";

    public GDSIPResolvingConfigurator() {
        configurationWriterService = new IpResolvingTaskConfigurationWriter();
    }

    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        List<GDSEnrichmentDefinitionState.IPResolvingState> ipResolvingStates = currGDSConfigurationState.getEnrichmentDefinitionState().getIpResolvingStates();

        addConfiguration(ipResolvingStates, configurationParams, GDS_CONFIG_ENTRY + SOURCE_IP_CONFIG_ENTRY);
        try {
            addConfiguration(ipResolvingStates, configurationParams, GDS_CONFIG_ENTRY + TARGET_IP_CONFIG_ENTRY);
        } catch (FortscaleNoConfigurationException e){
            //Target IP resolving is optional. If failed with FortscaleNoConfigurationException - do nothing
        }
    }

    private void addConfiguration(List<GDSEnrichmentDefinitionState.IPResolvingState> ipResolvingStates, Map<String, Map<String, ConfigurationParam>> configurationParams, String configurationKey)
            throws FortscaleNoConfigurationException{

        Map<String, ConfigurationParam> paramsMap = configurationParams.get(configurationKey);
        if (paramsMap == null){
            throw new FortscaleNoConfigurationException();
        }

		String lastState = currGDSConfigurationState.getStreamingTopologyDefinitionState().getLastStateValue();
		ConfigurationParam taskName = gdsInputHandler.getParamConfiguration(paramsMap, TASK_NAME_PARAM);
		ConfigurationParam outputTopic = gdsInputHandler.getParamConfiguration(paramsMap, OUTPUT_TOPIC_PARAM);

        ConfigurationParam restrictToAD = paramsMap.get("restrictToAD");
        ConfigurationParam shortNameUsage = paramsMap.get("shortNameUsage");
        ConfigurationParam removeLastDotUsage = paramsMap.get("removeLastDotUsage");
        ConfigurationParam dropOnFailUsage = paramsMap.get("dropOnFailUsage");
        ConfigurationParam overrideIpWithHostNameUsage = paramsMap.get("overrideIpWithHostNameUsage");
        ConfigurationParam updateOnlyFlag = paramsMap.get("ipField");
        ConfigurationParam hostField = paramsMap.get("hostField");


        GDSEnrichmentDefinitionState.IPResolvingState ipResolvingState = new GDSEnrichmentDefinitionState.IPResolvingState();

        ipResolvingState.setRestrictToAD(restrictToAD.getParamFlag());
        ipResolvingState.setShortNameUsage(shortNameUsage.getParamFlag());
        ipResolvingState.setDropOnFailUsage(dropOnFailUsage.getParamFlag());
        ipResolvingState.setOverrideIpWithHostNameUsage(overrideIpWithHostNameUsage.getParamFlag());
        ipResolvingState.setHostField(updateOnlyFlag.getParamValue());
        ipResolvingState.setRemoveLastDotUsage(removeLastDotUsage.getParamFlag());
		ipResolvingState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);
		ipResolvingState.setHostField(hostField.getParamValue());
		ipResolvingState.setTaskName(taskName.getParamValue());
		ipResolvingState.setLastState(lastState);
		ipResolvingState.setOutputTopic(outputTopic.getParamValue());

        ipResolvingStates.add(ipResolvingState);

        String lastStaeClac = taskName.getParamValue();
        if (lastStaeClac.indexOf("_") != -1 )
            lastStaeClac = lastStaeClac.substring(0,lastStaeClac.indexOf("_")-1);

		currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue(lastStaeClac);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getIpResolvingStates().clear();
    }

    @Override
    public GDSConfigurationType getType() {
        return GDSConfigurationType.IP_RESOLVING;
    }
}
