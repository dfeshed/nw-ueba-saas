package fortscale.monitoring.external.stats.samza.collector.service;

import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReaderResponse;

import java.util.List;

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
