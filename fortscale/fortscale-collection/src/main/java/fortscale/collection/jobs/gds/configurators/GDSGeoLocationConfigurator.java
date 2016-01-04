package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.GeoLocationConfiguration;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * Geo-location configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSGeoLocationConfigurator implements GDSConfigurator {

    private GDSCompositeConfigurationState gdsConfigurationState = new GDSCompositeConfigurationState();

    private ConfigurationService geoLocationConfiguration = new GeoLocationConfiguration();

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        // TODO implement
        geoLocationConfiguration.setGDSConfigurationState(gdsConfigurationState);

        return gdsConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (geoLocationConfiguration.init()) {
            geoLocationConfiguration.applyConfiguration();
        }

        geoLocationConfiguration.done();
    }

    @Override
    public void reset() throws Exception {
        gdsConfigurationState.reset();
    }
}
