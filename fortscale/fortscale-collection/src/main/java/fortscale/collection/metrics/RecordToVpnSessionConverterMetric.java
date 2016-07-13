package fortscale.collection.metrics;

/**
 * Created by gaashh on 5/29/16.
 */

import fortscale.collection.jobs.event.process.EventProcessJob;
import fortscale.collection.morphlines.RecordToVpnSessionConverter;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsService;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Metrics for RecordToStringItemsProcessor
 */
@StatsMetricsGroupParams(name = "ETL.record-to-vp-session-converter.service")
public class RecordToVpnSessionConverterMetric extends StatsMetricsGroup {

    public RecordToVpnSessionConverterMetric(StatsService statsService, String name) {
        // Call parent ctor
        super(statsService, RecordToVpnSessionConverter.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        addTag("name", name);
                    }
                }
        );

    }

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long record;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long vpnSessionClosed;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long vpnSessionSuccess;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long vpnSessionFailed;
}


