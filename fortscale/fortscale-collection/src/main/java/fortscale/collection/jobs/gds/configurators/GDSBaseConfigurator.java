package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;
import fortscale.services.configuration.state.GDSEntityType;

import java.util.Map;

/**
 * @author gils
 * 30/12/2015
 */
abstract class GDSBaseConfigurator implements GDSConfigurator {

    protected GDSCompositeConfigurationState gdsConfigurationState = new GDSCompositeConfigurationState();

    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        ConfigurationParam dataSourceName = configurationParams.get("dataSourceName");
        ConfigurationParam dataSourceType = configurationParams.get("dataSourceType");
        ConfigurationParam dataSourceLists = configurationParams.get("dataSourceLists");

        gdsConfigurationState.setDataSourceName(dataSourceName.getParamValue());
        gdsConfigurationState.setEntityType(GDSEntityType.valueOf(dataSourceType.getParamValue().toUpperCase()));
        gdsConfigurationState.setExistingDataSources(dataSourceLists.getParamValue());

        return gdsConfigurationState;
    }
}
