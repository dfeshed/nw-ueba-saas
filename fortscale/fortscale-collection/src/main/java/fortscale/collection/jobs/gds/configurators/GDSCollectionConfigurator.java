package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * Implementation of Collection configurator
 *
 * @author gils
 * 30/12/2015
 */
public class GDSCollectionConfigurator implements GDSConfigurator{

    public GDSCollectionConfigurator() {
    }

    @Override
    public void setConfigurationState(GDSCompositeConfigurationState currConfigurationState) {
        // TODO implement
    }

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) {
        // TODO implement
        return null;
    }

    @Override
    public void apply() {
        // TODO implement
    }

    @Override
    public void reset() throws Exception {
        // TODO implement
    }
}
