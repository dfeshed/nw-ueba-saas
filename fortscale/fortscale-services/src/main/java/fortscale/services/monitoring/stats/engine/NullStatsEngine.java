package fortscale.services.monitoring.stats.engine;

/**
 * A Null stats engine. It discards all the data it gets and it is always happy (whatever this means)
 *
 * The stats service uses this engine until a real engine is registered. This is useful for unit test when a real
 * engine might not be used.
 *
 * Created by gaashh on 4/18/16.
 */
public class NullStatsEngine implements StatsEngine {

    @Override
    public void writeMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData) {
        // NOP
    }

    @Override
    public void flushMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData) {
        // NOP
    }
}
