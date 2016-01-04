package fortscale.collection.jobs.gds.configurators;

import fortscale.collection.jobs.gds.GDSConfigurator;
import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.Impl.ComputerTaggingClassConfiguration;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;
import fortscale.utils.logging.Logger;

import java.util.Map;

/**
 * @author gils
 * 04/01/2016
 */
public class GDSComputerTaggingConfigurator implements GDSConfigurator {

    private static Logger logger = Logger.getLogger(GDSComputerTaggingConfigurator.class);

    private GDSCompositeConfigurationState gdsConfigurationState = new GDSCompositeConfigurationState();

    private ConfigurationService computerTaggingClassConfiguration = new ComputerTaggingClassConfiguration();

    @Override
    public GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception {
        // TODO implement
        computerTaggingClassConfiguration.setGDSConfigurationState(gdsConfigurationState);

        return gdsConfigurationState;
    }

    @Override
    public void apply() throws Exception {
        if (computerTaggingClassConfiguration.init()) {
            computerTaggingClassConfiguration.applyConfiguration();
        }

        computerTaggingClassConfiguration.done();
    }

    @Override
    public void reset() throws Exception {
        gdsConfigurationState.reset();
    }
}
