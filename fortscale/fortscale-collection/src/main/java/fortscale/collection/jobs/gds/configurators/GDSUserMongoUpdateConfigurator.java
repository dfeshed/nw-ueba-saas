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
    public void configure(Map<String, Map<String, ConfigurationParam>> configurationParams) throws Exception {

        Map<String, ConfigurationParam> paramsMap = configurationParams.get(GDS_CONFIG_ENTRY);

        ConfigurationParam anyRow = paramsMap.get("anyRow");
        ConfigurationParam statusFieldName = paramsMap.get("statusFieldName");
        ConfigurationParam successValue = paramsMap.get("successValue");

        GDSEnrichmentDefinitionState.UserMongoUpdateState userMongoUpdateState = currGDSConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState();

        userMongoUpdateState.setAnyRow(anyRow.getParamFlag());
        userMongoUpdateState.setStatusFieldName(statusFieldName.getParamValue());
        userMongoUpdateState.setSuccessValue(successValue.getParamValue());
    }

    @Override
    public void reset() throws Exception {
        currGDSConfigurationState.getEnrichmentDefinitionState().getUserMongoUpdateState().reset();
    }
}

