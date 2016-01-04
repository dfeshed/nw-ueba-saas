package fortscale.collection.jobs.gds;

import fortscale.services.configuration.ConfigurationParam;
import fortscale.services.configuration.state.GDSCompositeConfigurationState;

import java.util.Map;

/**
 * @author gils
 * 30/12/2015
 */
public interface GDSConfigurator {
    GDSCompositeConfigurationState configure(Map<String, ConfigurationParam> configurationParams) throws Exception;
    void apply() throws Exception;
    void reset() throws Exception;
}
