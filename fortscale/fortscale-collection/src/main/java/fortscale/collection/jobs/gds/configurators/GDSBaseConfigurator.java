package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

/**
 * @author gils
 * 05/01/2016
 */
abstract class GDSBaseConfigurator implements GDSConfigurator{

    protected ConfigurationService configurationService;

    protected GDSCompositeConfigurationState currGDSConfigurationState;

    @Override
    public void setConfigurationState(GDSCompositeConfigurationState currConfigurationState) {
        this.currGDSConfigurationState = currConfigurationState;
    }
}
