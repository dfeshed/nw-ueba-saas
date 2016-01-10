package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationService;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

/**
 * @author gils
 * 05/01/2016
 */
abstract class GDSBaseConfigurator implements GDSConfigurator{

    protected static final String GDS_CONFIG_ENTRY = "gds.config.entry.";

    protected ConfigurationService configurationService;

    protected GDSCompositeConfigurationState currGDSConfigurationState;

    @Override
    public void setConfigurationState(GDSCompositeConfigurationState currConfigurationState) {
        this.currGDSConfigurationState = currConfigurationState;
    }

    public GDSConfigurationResult<String> apply() throws Exception {
        configurationService.setGDSConfigurationState(currGDSConfigurationState);

        if (configurationService.init()) {
            configurationService.applyConfiguration();
        }

        configurationService.done();

        GDSConfigurationResultImpl gdsConfigurationResult = new GDSConfigurationResultImpl();
        gdsConfigurationResult.setSuccess(true);
        gdsConfigurationResult.setAffectedConfigDescriptors(configurationService.getAffectedConfigList());

        return gdsConfigurationResult;
    }
}
