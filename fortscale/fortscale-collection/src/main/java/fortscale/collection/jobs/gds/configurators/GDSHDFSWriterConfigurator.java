package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.HDFSWriteTaskConfiguration;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * HDFS Writer configurator implementation
 *
 * @author gils
 * 04/01/2016
 */
public class GDSHDFSWriterConfigurator implements GDSConfigurator {

    private GDSCompositeConfigurationState gdsConfigurationState = new GDSCompositeConfigurationState();

    private ConfigurationService hdfsWriteTaskConfiguration = new HDFSWriteTaskConfiguration();

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        // TODO implement
        hdfsWriteTaskConfiguration.setGDSConfigurationState(gdsConfigurationState);

        return gdsConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (hdfsWriteTaskConfiguration.init()) {
            hdfsWriteTaskConfiguration.applyConfiguration();
        }

        hdfsWriteTaskConfiguration.done();
    }

    @Override
    public void reset() throws Exception {
        gdsConfigurationState.reset();
    }
}

