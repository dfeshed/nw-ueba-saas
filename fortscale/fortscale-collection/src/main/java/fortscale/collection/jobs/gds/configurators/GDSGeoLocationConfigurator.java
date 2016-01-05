package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.Impl.GeoLocationConfiguration;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * Geo-location configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSGeoLocationConfigurator extends GDSBaseConfigurator {

    public GDSGeoLocationConfigurator() {
        configurationService = new GeoLocationConfiguration();
    }

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        // TODO implement
        configurationService.setGDSConfigurationState(currGDSConfigurationState);

        return currGDSConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (configurationService.init()) {
            configurationService.applyConfiguration();
        }

        configurationService.done();
    }

    @Override
    public void reset() throws Exception {
        // TODO implement
    }
}
