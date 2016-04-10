package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;

import java.lang.reflect.Field;

/**
 * Created by gaashh on 4/5/16.
 */
public class NumericMetricValueHandler extends MetricValueHandler {

    protected StatsNumericField statsNumericField;

    protected double factor;
    protected long precisionDigits;
    protected long rateSeconds;  // 0 = normal operation


    // ctor
    // TODO: add validation check, name, annotation params, ...
    public NumericMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
                                     StatsNumericField statsNumericField,
                                     double factor, long precisionDigits, long rateSeconds) {

        super(metricGroup, field, valueName);

        this.statsNumericField = statsNumericField;
        this.factor = factor;
        this.precisionDigits = precisionDigits;
        this.rateSeconds = rateSeconds;

    }

    public void addToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {
        calculateValueAndAddToEngineData(engineMetricsGroupData, epochTime);
    }

    void calculateValueAndAddToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {

        try {
            long longValue = statsNumericField.getAsLong();
            StatsEngineLongMetricData longData = new StatsEngineLongMetricData(valueName, longValue);
            engineMetricsGroupData.addLongMetricData(longData);

        } catch (Exception ex) {
            System.out.println("ERROR: get value" + ex.toString());
        }

    }

}
