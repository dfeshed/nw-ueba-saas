package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurationType;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.UserMongoUpdateConfigurationWriter;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.Map;

/**
 * User Mongo update configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSUserMongoUpdateConfigurator extends GDSBaseConfigurator {


    private static final String OUTPUT_TOPIC_ENTRY_PARAM = "";

    public GDSUserMongoUpdateConfigurator() {
        configurationWriterService = new UserMongoUpdateConfigurationWriter();
    }

    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

        String lastState = currGDSConfigurationState.getStreamingTopologyDefinitionState().getLastStateValue();
        ConfigurationParam taskName = paramsMap.get(TASK_NAME_PARAM);
        ConfigurationParam outputTopic = paramsMap.get(OUTPUT_TOPIC_PARAM);


        ConfigurationParam anyRow = paramsMap.get("anyRow");
        ConfigurationParam statusFieldName = paramsMap.get("statusFieldName");
        ConfigurationParam successValue = paramsMap.get("successValue");

        GDSEnrichmentDefinitionState.UserMongoUpdateState userMongoUpdateState = currGDSConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState();

        userMongoUpdateState.setTaskName(taskName.getParamValue());
        userMongoUpdateState.setLastState(lastState);
        userMongoUpdateState.setOutputTopic(outputTopic.getParamValue());
        userMongoUpdateState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);

        userMongoUpdateState.setAnyRow(anyRow.getParamFlag());

		if (statusFieldName != null )
			userMongoUpdateState.setStatusFieldName(statusFieldName.getParamValue());
		if (successValue != null )
        	userMongoUpdateState.setSuccessValue(successValue.getParamValue());

		//currGDSConfigurationState.getStreamingTopologyDefinitionState().setLastStateValue(taskName.getParamValue());
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

