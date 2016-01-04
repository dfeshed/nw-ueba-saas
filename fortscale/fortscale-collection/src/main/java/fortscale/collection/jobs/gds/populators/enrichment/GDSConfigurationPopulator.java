package fortscale.collection.jobs.gds.populators.enrichment;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * @author gils
 * 03/01/2016
 */
public interface GDSConfigurationPopulator {
    Map<String, ConfigurationParam> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception;
}
