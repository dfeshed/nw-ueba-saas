package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;

import java.lang.reflect.Field;

/**
 *
 * An value handler that handles metrics of type long. It extends the abstract MetricValueHandler.
 *
 * This value handler is use for fields with the StatsLongMetricsParams.
 *
 * It is responsible for reading the field value, manipulating it and writing in to the engine.
 *
 * See StatsLongMetricsParams for supported manipulations.
 *
 * Created by gaashh on 4/5/16.
 */
public class LongMetricValueHandler extends MetricValueHandler {

    // The numeric fields access class. It holds the metrics group and the field object. It can provide the field value
    protected StatsNumericField statsNumericField;

    // TODO
    protected double factor;
    // TODO
    protected long rateSeconds;  // 0 = normal operation


    /**
     *
     * A simple ctor that holds the values for future use. It has base class values and manipulation parameters
     *
     * @param metricGroup        - see base class
     * @param field              - see base class
     * @param valueName          - see base class
     * @param statsNumericField  - // TODO
     * @param factor             - // TODO
     * @param rateSeconds        - // TODO
     */
    // ctor
    // TODO: add validation check, name, annotation params, ...
    public LongMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
                                  StatsNumericField statsNumericField,
                                  double factor, long rateSeconds) {

        super(metricGroup, field, valueName);

        this.statsNumericField = statsNumericField;
        this.factor = factor;
        this.rateSeconds = rateSeconds;

    }

    /**
     *
     * This function is called when the metric group is written to the engine.
     *
     * It call an internal function to do the actual work.
     *
     * See base class for additional documentation
     *
     * @param engineMetricsGroupData - the engine data to add to
     * @param epochTime              - sample time. Might be used to calculate things like rate.
     */
    public void addToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {
        calculateValueAndAddToEngineData(engineMetricsGroupData, epochTime);
    }


    /**
     *
     * This function is call from addToEngineData() to read the field value, manipulate it and add it to the engine data
     * In case the field value is not relevant or it is invalid, the field value is not writen to the engine.
     *
     * It does the following:
     *   1. read the field value as long
     *   2. Manipulate it // TODO
     *   3. If valid, the field value to the engine data
     *
     * @param engineMetricsGroupData - the engine data to add to
     * @param epochTime              - sample time. Might be used to calculate things like rate.
     */
    void calculateValueAndAddToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {

        // TODO - error handling
        try {

            // Read the field value as long
            long longValue = statsNumericField.getAsLong();

            // Create a long metric data object to hold the field value
            StatsEngineLongMetricData longData = new StatsEngineLongMetricData(valueName, longValue);

            // Add the  metric data object to the engine
            engineMetricsGroupData.addLongMetricData(longData);

        } catch (Exception ex) {
            // TODO
            System.out.println("ERROR: get value" + ex.toString());
        }

    }

    public String toString() {

        return String.format("long %s factor=%e rateSeconds=%d", super.toString(), factor, rateSeconds);

    }

}
