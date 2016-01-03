package fortscale.collection.jobs.gds;

import fortscale.collection.jobs.gds.state.GDSConfigurationState;

/**
 * @author gils
 * 30/12/2015
 */
public interface GDSConfigurator {
    void init(GDSConfigurationState gdsConfigurationState);
    void configure() throws Exception;
    void apply() throws Exception;
    void reset() throws Exception;
}
