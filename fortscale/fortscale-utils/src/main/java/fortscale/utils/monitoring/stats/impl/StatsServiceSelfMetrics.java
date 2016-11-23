package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsMetricsGroupManualRegistration;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

/**
 * Created by gaashh on 11/15/16.
 */

@StatsMetricsGroupParams(name = "stats.service")
public class StatsServiceSelfMetrics extends StatsMetricsGroupManualRegistration {

    public StatsServiceSelfMetrics() {
        // Call parent ctor
        super(StatsServiceImpl.class,
                // Create anonymous attribute class with initializer block since it does not have ctor
                new StatsMetricsGroupAttributes() {
                    {
                        // Empty
                    }
                }
        );
    }


    // --- metrics group registration and un-registration counters
    @StatsLongMetricParams  ( name = "registerStatsMetricsGroupCount" )
    @StatsDoubleMetricParams( name = "registerStatsMetricsGroupRate", rateSeconds = 1)
    public long registerStatsMetricsGroup;

    @StatsLongMetricParams  ( name = "registerStatsMetricsGroupErrorCount" )
    @StatsDoubleMetricParams( name = "registerStatsMetricsGroupErrorRate", rateSeconds = 1)
    public long registerStatsMetricsGroupError;

    @StatsLongMetricParams  ( name = "unregisterStatsMetricsGroupCount" )
    @StatsDoubleMetricParams( name = "unregisterStatsMetricsGroupRate", rateSeconds = 1)
    public long unregisterStatsMetricsGroup;

    @StatsLongMetricParams  ( name = "unregisterStatsMetricsGroupErrorCount" )
    @StatsDoubleMetricParams( name = "unregisterStatsMetricsGroupErrorRate", rateSeconds = 1)
    public long unregisterStatsMetricsGroupError;



    // --- Metrics groups creation counters
    @StatsLongMetricParams
    public long metricsGroupCreated;

    @StatsLongMetricParams
    public long metricsGroupsWithoutAnnotations;

    @StatsLongMetricParams
    public long metricsGroupLongFields;

    @StatsLongMetricParams
    public long metricsGroupDoubleFields;

    @StatsLongMetricParams
    public long metricsGroupDateFields;

    @StatsLongMetricParams
    public long metricsGroupStringFields;

    @StatsLongMetricParams
    public long metricsGroupCreatedWithManualUpdate;


    // --- Write metrics groups to stats engine counters
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeMetricsGroupsToEngine;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeMetricsGroupsToEngineError;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupWriteToEngine;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupWriteToEngineNoMetrics;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupMetricsWriteToEngine;  // In other words, number of fields written



    // --- Stats service manual update push counters. Note: this has nothing to do with specific metrics group manual update
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long manualUpdatePush;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long manualUpdatePushError;


    // --- Metrics groups manual update counters
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupManualUpdate;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupManualUpdateError;


    // --- Tick counters
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long tick;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long tickError;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long tickMetricsUpdateSlips;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long tickEnginePushSlips;

}
