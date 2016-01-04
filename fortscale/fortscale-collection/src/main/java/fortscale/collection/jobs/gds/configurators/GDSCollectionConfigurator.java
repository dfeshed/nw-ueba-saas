package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * @author gils
 *         30/12/2015
 */
public class GDSCollectionConfigurator implements GDSConfigurator{

    public GDSCollectionConfigurator() {
    }

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) {
        return null;
    }

    @Override
    public void apply() {

    }

    @Override
    public void reset() throws Exception {

    }
}
