package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.engine.StatsEngineDoubleMetricData;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;

import java.lang.reflect.Field;

/**
 * Created by gaashh on 4/5/16.
 */
public class DoubleMetricValueHandler extends MetricValueHandler {

    protected StatsNumericField statsNumericField;

    protected double factor;
    protected long precisionDigits;
    protected long rateSeconds;  // 0 = normal operation


    // ctor
    // TODO: add validation check, name, annotation params, ...
    public DoubleMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
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


    /**
     *
     * This function is call from addToEngineData() to read the field value, manipulate it and add it to the engine data
     * In case the field value is not relevant or it is invalid, the field value is not writen to the engine.
     *
     * It does the following:
     *   1. read the field value as double
     *   2. Manipulate it // TODO
     *   3. If valid, the field value to the engine data
     *
     * @param engineMetricsGroupData - the engine data to add to
     * @param epochTime              - sample time. Might be used to calculate things like rate.
     */

    void calculateValueAndAddToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {

        try {
            // Read the field value as double
            double doubleValue = statsNumericField.getAsDouble();

            // Create a double metric data object to hold the field value
            StatsEngineDoubleMetricData doubleData = new StatsEngineDoubleMetricData(valueName, doubleValue);

            // Add the  metric data object to the engine
            engineMetricsGroupData.addDoubleMetricData(doubleData);

        } catch (Exception ex) {
            // TODO
            System.out.println("ERROR: get value" + ex.toString());
        }

    }

    public String toString() {

        return String.format("double %s factor=%e precisionDigits=%d rateSeconds=%d",
                             super.toString(), factor, precisionDigits, rateSeconds);

    }

}
