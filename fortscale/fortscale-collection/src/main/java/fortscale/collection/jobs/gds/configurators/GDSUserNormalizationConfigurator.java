package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.UserNormalizationTaskConfiguration;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * @author gils
 * 04/01/2016
 */
public class GDSUserNormalizationConfigurator implements GDSConfigurator {

    private static Logger logger = Logger.getLogger(GDSUserNormalizationConfigurator.class);

    private GDSCompositeConfigurationState gdsConfigurationState = new GDSCompositeConfigurationState();

    private ConfigurationService userNormalizationTaskConfiguration = new UserNormalizationTaskConfiguration();

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        // TODO implement
        userNormalizationTaskConfiguration.setGDSConfigurationState(gdsConfigurationState);

        return gdsConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (userNormalizationTaskConfiguration.init()) {
            userNormalizationTaskConfiguration.applyConfiguration();
        }

        userNormalizationTaskConfiguration.done();
    }

    @Override
    public void reset() throws Exception {
        gdsConfigurationState.reset();
    }
}
