package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsLongFlexMetric;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.monitoring.stats.StatsMetricsGroupManualRegistration;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by gaashh on 11/15/16.
 */

@StatsMetricsGroupParams(name = "monitoring.stats.service")
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

        // Point some counters to counters that are actually counted at StatsMetricsGroup
        metricsGroupRegisterWithNullStatsService = StatsMetricsGroup.getRegisterWithNullStatsServiceCounter();
        metricsGroupManualUpdateWithNullStatsService = StatsMetricsGroup.getManualUpdateWithNullStatsServiceCounter();
    }


    // --- metrics group registration and un-registration counters
    @StatsLongMetricParams  ( name = "metricsGroupRegisterCount" )
    @StatsDoubleMetricParams( name = "metricsGroupRegisterRate", rateSeconds = 1)
    public long metricsGroupRegister;

    @StatsLongMetricParams  ( name = "metricsGroupRegisterErrorCount" )
    public long metricsGroupRegisterError;

    @StatsLongMetricParams  ( name = "metricsGroupUnregisterCount" )
    @StatsDoubleMetricParams( name = "metricsGroupUnregisterRate", rateSeconds = 1)
    public long metricsGroupUnregister;

    @StatsLongMetricParams  ( name = "metricsGroupUnregisterErrorCount" )
    public long metricsGroupUnregisterError;

    @StatsLongMetricParams  ( name = "metricsGroupRegisterWithNullStatsServiceCount" )
    public AtomicLong metricsGroupRegisterWithNullStatsService;

    @StatsLongMetricParams  ( name = "metricsGroupManualUpdateWithNullStatsServiceCount" )
    public AtomicLong metricsGroupManualUpdateWithNullStatsService;

    // --- Metrics groups creation counters
    @StatsLongMetricParams
    public long metricsGroupCreated;

    @StatsLongMetricParams
    public long metricsGroupsWithoutAnnotations;

    @StatsLongMetricParams
    public long metricsGroupCreatedWithManualUpdate;

    @StatsLongMetricParams
    public long metricsGroupLongFields;

    @StatsLongMetricParams
    public long metricsGroupDoubleFields;

    @StatsLongMetricParams
    public long metricsGroupDateFields;

    @StatsLongMetricParams
    public long metricsGroupStringFields;

    @StatsLongMetricParams
    private StatsLongFlexMetric metricsGroupFields = new StatsLongFlexMetric() {
        @Override
        public Long getValue() {
            return metricsGroupLongFields + metricsGroupDoubleFields + metricsGroupDateFields + metricsGroupStringFields;
        }
    };


    // --- Write metrics groups to stats engine counters
    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeMetricsGroupsToEngine;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long writeMetricsGroupsToEngineError;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupWriteToEngine;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupWritesToEngineWithoutFields;

    @StatsDoubleMetricParams(rateSeconds = 1)
    public long metricsGroupFieldsWritesToEngine;  // In other words, number of fields written



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
