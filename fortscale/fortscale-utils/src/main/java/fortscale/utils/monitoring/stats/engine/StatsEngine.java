package fortscale.utils.monitoring.stats.engine;

/**
 *
 * This is an interface definition to the stats engine. The stats engine gets the stats metrics field values and writes
 * them to the stats data bases (e.g. influx) via various transport layers
 *
 * Created by gaashh on 4/6/16.
 */
public interface StatsEngine {


    /**
     *
     * Write metrics group data toward the stats DB.
     *
     * Note: The engine might accumulate the data until flush is called.
     *
     * Note: This function should be thread safe
     *
     * @param metricsGroupData - the metrics group data to write to the stats engine
     */
    void writeMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData);

    /**
     *
     * Flush the accumulated groups metrics data to their final destination.
     *
     * See writeMetricsGroupData() also.
     *
     * Note: This function should be thread safe
     *
     * @param metricsGroupData - the metrics group data to write to the stats engine
     */
     void flushMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData);

}
