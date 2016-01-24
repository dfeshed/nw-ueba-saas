package fortscale.collection.jobs.gds.input.populators.modelAndScore;

import fortscale.collection.jobs.gds.input.populators.enrichment.GDSConfigurationPopulator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * @author gils
 * 12/01/2016
 */
public class GDSAggregatedEventsCLIPopulator implements GDSConfigurationPopulator{
    @Override
    public Map<String, Map<String, ConfigurationParam>> populateConfigurationData(GDSCompositeConfigurationState currentConfigurationState) throws Exception {
        return null;
    }
}
