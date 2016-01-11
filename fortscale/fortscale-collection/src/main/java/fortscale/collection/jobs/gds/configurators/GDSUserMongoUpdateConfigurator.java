package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.UserMongoUpdateConfiguration;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.Map;

/**
 * User Mongo update configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSUserMongoUpdateConfigurator extends GDSBaseConfigurator {

    private static final String LAST_STATE_PARAM = "lastState";
    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topics";

    public GDSUserMongoUpdateConfigurator() {
        configurationService = new UserMongoUpdateConfiguration();
    }

    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

        ConfigurationParam lastState = paramsMap.get(LAST_STATE_PARAM);
        ConfigurationParam taskName = paramsMap.get(TASK_NAME_PARAM);
        ConfigurationParam outputTopic = paramsMap.get(OUTPUT_TOPIC_PARAM);
        ConfigurationParam outputTopicEntry = paramsMap.get(OUTPUT_TOPIC_ENTRY_PARAM);

        ConfigurationParam anyRow = paramsMap.get("anyRow");
        ConfigurationParam statusFieldName = paramsMap.get("statusFieldName");
        ConfigurationParam successValue = paramsMap.get("successValue");

        GDSEnrichmentDefinitionState.UserMongoUpdateState userMongoUpdateState = currGDSConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState();

        userMongoUpdateState.setTaskName(taskName.getParamValue());
        userMongoUpdateState.setLastState(lastState.getParamValue());
        userMongoUpdateState.setOutputTopic(outputTopic.getParamValue());
        userMongoUpdateState.setOutputTopicEntry(outputTopicEntry.getParamValue());

        userMongoUpdateState.setAnyRow(anyRow.getParamFlag());
        userMongoUpdateState.setStatusFieldName(statusFieldName.getParamValue());
        userMongoUpdateState.setSuccessValue(successValue.getParamValue());
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState().reset();
    }

    @Override
    public GDSConfigurationType getType() {
        return GDSConfigurationType.USER_MONGO_UPDATE;
    }
}

