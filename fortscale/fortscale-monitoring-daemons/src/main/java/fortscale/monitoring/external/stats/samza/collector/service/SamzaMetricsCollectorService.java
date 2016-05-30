package fortscale.monitoring.external.stats.samza.collector.service;

/**
 * collects standard samza metrics and converts to stats metrics
 */
public interface SamzaMetricsCollectorService {
    /**
     * shut down method
     */
    void shutDown();

    /**
     * forever reads from metrics topic
     */
    void start();


}
