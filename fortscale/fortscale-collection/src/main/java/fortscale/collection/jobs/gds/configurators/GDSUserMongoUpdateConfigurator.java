package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.UserMongoUpdateConfiguration;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * @author gils
 * 04/01/2016
 */
public class GDSUserMongoUpdateConfigurator implements GDSConfigurator {

    private static Logger logger = Logger.getLogger(GDSUserMongoUpdateConfigurator.class);

    private GDSCompositeConfigurationState gdsConfigurationState = new GDSCompositeConfigurationState();

    private ConfigurationService userMongoUpdateConfiguration = new UserMongoUpdateConfiguration();

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        // TODO implement
        userMongoUpdateConfiguration.setGDSConfigurationState(gdsConfigurationState);

        return gdsConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (userMongoUpdateConfiguration.init()) {
            userMongoUpdateConfiguration.applyConfiguration();
        }

        userMongoUpdateConfiguration.done();
    }

    @Override
    public void reset() throws Exception {
        gdsConfigurationState.reset();
    }
}

