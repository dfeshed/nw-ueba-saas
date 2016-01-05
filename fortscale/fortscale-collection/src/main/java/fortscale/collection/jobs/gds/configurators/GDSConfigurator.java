package fortscale.collection.jobs.gds.configurators;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.gds.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * Interface for Generic data source configurator
 *
 * @author gils
 * 30/12/2015
 */
public interface GDSConfigurator {
    void setConfigurationState(GDSCompositeConfigurationState currConfigurationState);
    GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception;
    void apply() throws Exception;
    void reset() throws Exception;
}
