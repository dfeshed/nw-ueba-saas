package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.ComputerTaggingClassConfiguration;
import fortscale.services.configuration.gds.state.GDSEnrichmentDefinitionState;

import java.util.Map;

/**
 * Computer tagging configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSComputerTaggingConfigurator extends GDSBaseConfigurator {

    private static final String LAST_STATE_PARAM = "lastState";
    private static final String TASK_NAME_PARAM = "taskName";
    private static final String OUTPUT_TOPIC_PARAM = "outputTopic";
    private static final String OUTPUT_TOPIC_ENTRY_PARAM = "output.topic";

    public GDSComputerTaggingConfigurator() {
        configurationService = new ComputerTaggingClassConfiguration();
    }

    @Override
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {
        Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

        ConfigurationParam lastState = paramsMap.get(LAST_STATE_PARAM);
        ConfigurationParam taskName = paramsMap.get(TASK_NAME_PARAM);
        ConfigurationParam outputTopic = paramsMap.get(OUTPUT_TOPIC_PARAM);


        ConfigurationParam sourceHost = paramsMap.get("sourceHost");
        ConfigurationParam targetHost = paramsMap.get("targetHost");
        ConfigurationParam srcMachineClassifier = paramsMap.get("srcMachineClassifier");
        ConfigurationParam srcClusteringField = paramsMap.get("srcClusteringField");
        ConfigurationParam createNewComputerFlag = paramsMap.get("createNewComputerFlag");
        ConfigurationParam dstMachineClassifier = paramsMap.get("dstMachineClassifier");
        ConfigurationParam dstClusteringField = paramsMap.get("dstClusteringField");

        GDSEnrichmentDefinitionState.ComputerTaggingState computerTaggingState = currGDSConfigurationState.getEnrichmentDefinitionState().getComputerTaggingState();

        computerTaggingState.setTaskName(taskName.getParamValue());
        computerTaggingState.setLastState(lastState.getParamValue());
        computerTaggingState.setOutputTopic(outputTopic.getParamValue());
        computerTaggingState.setOutputTopicEntry(OUTPUT_TOPIC_ENTRY_PARAM);

        computerTaggingState.setSourceHost(sourceHost.getParamValue());
        computerTaggingState.setTargetHost(targetHost.getParamValue());
        computerTaggingState.setSrcMachineClassifier(srcMachineClassifier.getParamValue());
        computerTaggingState.setSrcClusteringField(srcClusteringField.getParamValue());
        computerTaggingState.setCreateNewComputerFlag(createNewComputerFlag.getParamFlag());
        computerTaggingState.setDstMachineClassifier(dstMachineClassifier.getParamValue());
        computerTaggingState.setDstClusteringField(dstClusteringField.getParamValue());
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getComputerTaggingState().reset();
    }
}
