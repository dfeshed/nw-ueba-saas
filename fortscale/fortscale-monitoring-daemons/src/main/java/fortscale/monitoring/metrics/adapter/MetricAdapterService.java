package fortscale.monitoring.metrics.adapter;

import fortscale.monitoring.metrics.adapter.topicReader.EngineDataTopicSyncReaderResponse;
import fortscale.utils.monitoring.stats.models.engine.EngineData;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.List;

/**
 * metrics adapter Service purpose is to read fortscale stats from metrics topic and write to time series db
 */
public interface MetricAdapterService {

    /**
     * shut down method
     */
    void shutDown();

    /**
     * forever reads from metrics topic & writes batch to  time series db
     */
    void start();

    /**
     * initiating time series db with default db name and retention
     */
    void init();

    /**
     * reads messages from kafka metrics topic
     *
     * @return list of MetricMessage Pojos from kafka metrics topic
     */
    EngineDataTopicSyncReaderResponse readMetricsTopic();

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     *
     * @param metricMessages metric messages
     * @return BatchPoints
     */
    BatchPoints EnginDataToBatchPoints(EngineDataTopicSyncReaderResponse metricMessages);

    /**
     * converts EngineData POJO to List<Point>. the List is built from the diffrent metrics groups
     * Timeunit is seconds by definition
     *
     * @param data EngineData object to be converted to Points
     * @return list of points (an influxdb DTO)
     */
    List<Point> engineDataToPoints(EngineData data);

}
