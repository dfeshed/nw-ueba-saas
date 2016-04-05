package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;

import java.lang.reflect.Field;

/**
 * Created by gaashh on 4/5/16.
 */
public class NumericMetricValueHandler extends MetricValueHandler {

    protected StatsNumericField statsNumericField;

    protected double factor;
    protected long   precisionDigits;
    protected long   rateSeconds;  // 0 = normal operation


    // ctor
    // TODO: add validation check, name, annotation params, ...
    public NumericMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
                                     StatsNumericField statsNumericField,
                                     double factor, long precisionDigits, long rateSeconds) {

        super(metricGroup, field, valueName);

        this.statsNumericField  = statsNumericField;
        this.factor             = factor;
        this.precisionDigits    = precisionDigits;
        this.rateSeconds        = rateSeconds;

    }

}

