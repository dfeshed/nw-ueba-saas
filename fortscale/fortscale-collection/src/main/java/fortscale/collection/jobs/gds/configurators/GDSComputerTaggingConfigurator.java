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

    public GDSComputerTaggingConfigurator() {
        configurationService = new ComputerTaggingClassConfiguration();
    }

    @Override
    public void configure(Map<String, ConfigurationParam> configurationParams) throws Exception {

        GDSEnrichmentDefinitionState.ComputerTaggingState computerTaggingState = currGDSConfigurationState.getEnrichmentDefinitionState().getComputerTaggingState();

        ConfigurationParam sourceHost = configurationParams.get("sourceHost");
        ConfigurationParam targetHost = configurationParams.get("targetHost");
        ConfigurationParam srcMachineClassifier = configurationParams.get("srcMachineClassifier");
        ConfigurationParam srcClusteringField = configurationParams.get("srcClusteringField");
        ConfigurationParam createNewComputerFlag = configurationParams.get("createNewComputerFlag");
        ConfigurationParam dstMachineClassifier = configurationParams.get("dstMachineClassifier");
        ConfigurationParam dstClusteringField = configurationParams.get("dstClusteringField");

        computerTaggingState.setSourceHost(sourceHost.getParamValue());
        computerTaggingState.setTargetHost(targetHost.getParamValue());
        computerTaggingState.setSrcMachineClassifier(srcMachineClassifier.getParamValue());
        computerTaggingState.setSrcClusteringField(srcClusteringField.getParamValue());
        computerTaggingState.setCreateNewComputerFlag(createNewComputerFlag.getParamFlag());
        computerTaggingState.setDstMachineClassifier(dstMachineClassifier.getParamValue());
        computerTaggingState.setDstClusteringField(dstClusteringField.getParamValue());

        configurationService.setGDSConfigurationState(currGDSConfigurationState);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getComputerTaggingState().reset();
    }
}
