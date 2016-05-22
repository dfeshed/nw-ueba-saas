package fortscale.monitoring.external.stats.samza.collector.service;

import fortscale.monitoring.external.stats.samza.collector.topicReader.SamzaMetricsTopicSyncReaderResponse;

import java.util.List;

/**
 * Created by cloudera on 5/22/16.
 */
public interface SamzaMetricsCollectorService {
    /**
     * shut down method
     */
    public void shutDown();

    /**
     * forever reads from metrics topic & writes batch to  time series db
     */
    public void start();


    /**
     * reads messages from kafka metrics topic
     *
     * @return list of MetricMessage Pojos from kafka metrics topic
     */
    List<SamzaMetricsTopicSyncReaderResponse> readMetricsTopic();

}
