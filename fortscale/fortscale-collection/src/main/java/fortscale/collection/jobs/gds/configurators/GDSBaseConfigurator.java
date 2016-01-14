package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationWriterService;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

/**
 * @author gils
 * 05/01/2016
 */
abstract class GDSBaseConfigurator implements GDSConfigurator{

    protected static final String GDS_CONFIG_ENTRY = "gds.config.entry.";

    protected ConfigurationWriterService configurationWriterService;

    protected GDSCompositeConfigurationState currGDSConfigurationState;

    @Override
    public void setConfigurationState(GDSCompositeConfigurationState currConfigurationState) {
        this.currGDSConfigurationState = currConfigurationState;
    }

    public GDSConfigurationResult<String> apply() throws Exception {
        configurationWriterService.setGDSConfigurationState(currGDSConfigurationState);

        if (configurationWriterService.init()) {
            configurationWriterService.applyConfiguration();
        }

        configurationWriterService.done();

        GDSFileBaseConfigurationResult gdsConfigurationResult = new GDSFileBaseConfigurationResult();
        gdsConfigurationResult.setSuccess(true);
        gdsConfigurationResult.setAffectedConfigDescriptors(configurationWriterService.getAffectedConfigList());

        return gdsConfigurationResult;
    }
}
