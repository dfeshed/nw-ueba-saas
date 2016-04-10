package fortscale.services.monitoring.stats.impl;

import java.lang.reflect.Field;


import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;


/**
 * Created by gaashh on 4/5/16.
 */
abstract public class MetricValueHandler {

    protected StatsMetricsGroup metricGroup;
    protected Field             field;

    protected String            valueName;

    // ctor
    public MetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName) {

        this.metricGroup = metricGroup;
        this.field       = field;
        this.valueName   = valueName;
    }

    abstract public void addToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime);

    // --- getters / setters

    public StatsMetricsGroup getMetricGroup() {
        return metricGroup;
    }

    public Field getField() {
        return field;
    }

    public String getValueName() {
        return valueName;
    }
}
