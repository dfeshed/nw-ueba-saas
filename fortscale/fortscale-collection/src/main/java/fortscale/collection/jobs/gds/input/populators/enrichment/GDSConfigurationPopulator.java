package fortscale.collection.jobs.gds.input.populators.enrichment;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * Interface for generic data source configuration populators
 *
 * @author gils
 * 03/01/2016
 */
public interface GDSConfigurationPopulator {
    Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception;
}
