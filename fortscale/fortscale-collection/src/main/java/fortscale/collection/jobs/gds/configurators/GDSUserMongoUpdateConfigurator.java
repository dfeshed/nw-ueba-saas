package fortscale.collection.jobs.gds.configurators;

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

    public GDSUserMongoUpdateConfigurator() {
        configurationService = new UserMongoUpdateConfiguration();
    }

    @Override
    public void configure(Map<String, ConfigurationParam> configurationParams) throws Exception {

        GDSEnrichmentDefinitionState.UserMongoUpdateState userMongoUpdateState = currGDSConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState();

        ConfigurationParam anyRow = configurationParams.get("anyRow");
        ConfigurationParam statusFieldName = configurationParams.get("statusFieldName");
        ConfigurationParam successValue = configurationParams.get("successValue");

        userMongoUpdateState.setAnyRow(anyRow.getParamFlag());
        userMongoUpdateState.setStatusFieldName(statusFieldName.getParamValue());
        userMongoUpdateState.setSuccessValue(successValue.getParamValue());

        configurationService.setGDSConfigurationState(currGDSConfigurationState);
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState().reset();
    }
}

