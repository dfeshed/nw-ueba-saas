package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.UserNormalizationTaskConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.List;
import java.util.Map;

/**
 * User normalization configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSUserNormalizationConfigurator extends GDSBaseConfigurator {

    private static final String SOURCE_USERNAME_CONFIG_ENTRY = "source.";
    private static final String TARGET_USERNAME_CONFIG_ENTRY = "target.";
	private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topic";

    public GDSUserNormalizationConfigurator() {
        configurationWriterService = new UserNormalizationTaskConfigurationWriter();
    }

    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        List<GDSEnrichmentDefinitionState.UserNormalizationState> userNormalizationStates = currGDSConfigurationState.getEnrichmentDefinitionState().getUserNormalizationStates();


        String sourceUsernameConfigKey = GDS_CONFIG_ENTRY + SOURCE_USERNAME_CONFIG_ENTRY;

        if (configurationParams.containsKey(sourceUsernameConfigKey)) {
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setSourceUsernameResolvingRequired(true);
            addConfiguration(userNormalizationStates, configurationParams, sourceUsernameConfigKey);
        }

        String targetUsernameConfigKey = GDS_CONFIG_ENTRY + TARGET_USERNAME_CONFIG_ENTRY;

        if (configurationParams.containsKey(targetUsernameConfigKey)) {
            currGDSConfigurationState.getStreamingTopologyDefinitionState().setTargetUsernameResolvingRequired(true);
            addConfiguration(userNormalizationStates, configurationParams, targetUsernameConfigKey);
        }
    }

    private void addConfiguration(List<GDSEnrichmentDefinitionState.UserNormalizationState> userNormalizationStates, Map<String, Map<String, ConfigurationParam>> configurationParams, String configurationKey) {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(configurationKey);

		String lastState = currGDSConfigurationState.getStreamingTopologyDefinitionState().getLastStateValue();

		ConfigurationParam taskName = gdsInputHandler.getParamConfiguration(paramsMap, TASK_NAME_PARAM);
		ConfigurationParam outputTopic = gdsInputHandler.getParamConfiguration(paramsMap, OUTPUT_TOPIC_PARAM);

        ConfigurationParam normalizationBasedField = paramsMap.get("normalizationBasedField");
        ConfigurationParam domainField = paramsMap.get("domainFieldName");
        ConfigurationParam domainValue = paramsMap.get("domainValue");
        ConfigurationParam normalizedUserNameField = paramsMap.get("normalizedUserNameField");
        ConfigurationParam normalizeServiceName = paramsMap.get("normalizeServiceName");
        ConfigurationParam updateOnlyFlag = paramsMap.get("updateOnlyFlag");


        GDSEnrichmentDefinitionState.UserNormalizationState userNormalizationState = new GDSEnrichmentDefinitionState.UserNormalizationState();

        userNormalizationState.setNormalizationBasedField(normalizationBasedField.getParamValue());
        userNormalizationState.setDomainField(domainField.getParamValue());
        userNormalizationState.setDomainValue(domainValue.getParamValue());
        userNormalizationState.setNormalizedUserNameField(normalizedUserNameField.getParamValue());
        userNormalizationState.setNormalizeServiceName(normalizeServiceName.getParamValue());
        userNormalizationState.setUpdateOnly(updateOnlyFlag.getParamValue());
		userNormalizationState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);
		userNormalizationState.setLastState(lastState);
		userNormalizationState.setTaskName(taskName.getParamValue());
		userNormalizationState.setOutputTopic(outputTopic.getParamValue());

        userNormalizationStates.add(userNormalizationState);

        String lastStaeClac = taskName.getParamValue();
        if (lastStaeClac.indexOf("_") != -1 )
            lastStaeClac = lastStaeClac.substring(0,lastStaeClac.indexOf("_")-1);

		currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue(lastStaeClac);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getUserNormalizationStates().clear();
    }

    @Override
    public GDSConfigurationType getType() {
        return GDSConfigurationType.USER_NORMALIZATION;
    }
}
