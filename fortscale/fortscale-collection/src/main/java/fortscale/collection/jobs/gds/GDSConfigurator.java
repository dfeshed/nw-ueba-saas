package fortscale.collection.jobs.gds;

/**
 * @author gils
 * 30/12/2015
 */
interface GDSConfigurator {
    void configure() throws Exception;
    void apply() throws Exception;
    void reset() throws Exception;
}
