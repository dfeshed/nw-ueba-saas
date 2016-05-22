package fortscale.monitoring.metricAdapter;

import fortscale.monitoring.metricAdapter.engineData.topicReader.EngineDataTopicSyncReaderResponse;
import fortscale.utils.monitoring.stats.models.engine.EngineData;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;

import java.util.List;

/**
 * Created by cloudera on 5/15/16.
 */
public interface MetricAdapterService {

    /**
     * shut down method
     */
    public void shutDown();

    /**
     * forever reads from metrics topic & writes batch to  time series db
     */
    public void start();

    /**
     * initiating time series db with default db name and retention
     */
    public void init();

    /**
     * reads messages from kafka metrics topic
     *
     * @return list of MetricMessage Pojos from kafka metrics topic
     */
    List<EngineDataTopicSyncReaderResponse> readMetricsTopic();

    /**
     * converts MetricMessages to BatchPoints. (if engine data has valid version and not null)
     *
     * @param metricMessages
     * @return BatchPoints
     */
    BatchPoints EnginDataToBatchPoints(List<EngineDataTopicSyncReaderResponse> metricMessages);

    /**
     * converts EngineData POJO to List<Point>. the List is built from the diffrent metrics groups
     * Timeunit is seconds by definition
     *
     * @param data EngineData object to be converted to Points
     * @return list of points (an influxdb DTO)
     */
    List<Point> engineDataToPoints(EngineData data);

}
