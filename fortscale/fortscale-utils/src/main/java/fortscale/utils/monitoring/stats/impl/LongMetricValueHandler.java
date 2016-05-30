package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;
import java.text.MessageFormat;

/**
 *
 * A value handler that handles metrics of type long. It extends the abstract MetricValueHandler.
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

    private static final Logger logger = Logger.getLogger(LongMetricValueHandler.class);


    // The numeric fields access class. It holds the metrics group and the field object. It can provide the field value
    protected StatsNumericField statsNumericField;

    // Metric's factor - see annotation for detailed description
    protected double factor;

    // Metric's rate - see annotation for detailed description
    protected long rateSeconds;

    // Metric's negativeRate - see annotation for detailed description
    protected boolean isEnableNegativeRate;

    // Last read field value. null indicates no last read value. Used in rate calculation
    Double lastFieldValue;

    // Last read field epoch time. 0 indicates no last read value. Used in rate calculation.
    long lastEpochTime;



    /**
     *
     * A simple ctor that holds the values for future use. It has base class values and manipulation parameters
     *
     * @param metricGroup          - See base class
     * @param field                - See base class
     * @param valueName            - See base class
     * @param statsNumericField    - The numeric field access instance associated with this metric
     * @param factor               - See StatsLongMetricsParams
     * @param rateSeconds          - See StatsLongMetricsParams
     * @param isEnableNegativeRate - See StatsLongMetricsParams
     */
    // TODO: add validation check, name, annotation params, ...
    public LongMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
                                  StatsNumericField statsNumericField,
                                  double factor, long rateSeconds, boolean isEnableNegativeRate) {

        super(metricGroup, field, valueName);

        // Save ctor values
        this.statsNumericField     = statsNumericField;
        this.factor                = factor;
        this.rateSeconds           = rateSeconds;
        this.isEnableNegativeRate  = isEnableNegativeRate;

        // Reset last field value
        lastFieldValue = null;
        lastEpochTime  = 0;
    }



    /**
     *
     * This function is called from addToEngineData() to read the field value, manipulate it and add it to the engine data
     * In case the field value is not relevant or it is invalid, the field value is not writen to the engine.
     *
     * It does the following:
     *   1. read the field value as long
     *   2. Manipulate it
     *   3. If valid, the field value to the engine data
     *
     * @param engineMetricsGroupData - the engine data to add to
     * @param epochTime              - sample time. Might be used to calculate things like rate.
     */
    protected void calculateValueAndAddToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {

        // Just in case
        try {

            // The metric value. If null, don't write to engine
            Long result;

            // Do we have simple field (no modifications like factor or rate) or complex one?
            if ( factor < 0 && rateSeconds == 0 ) {

                // Simple field, read as long without any tricks
                result = statsNumericField.getAsLong();

                // Log it
                final String msgFormat =
                        "Calculated (simple) long metric value. groupName={} name={} instClass={} metricValue={} " +
                        "factor={} rateSeconds={} epochTime={}";

                logger.debug(msgFormat,
                        metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(), result,
                        factor, rateSeconds, epochTime);

            }
            else {
                // Special processing, read as double
                Double fieldValueInDouble = statsNumericField.getAsDouble();

                // Calculate the metric value with all the modification. Get some "help" form the double metric handler
                Double metricValueInDouble = DoubleMetricValueHandler.calculateMetricValueWithModifications(
                                               fieldValueInDouble, epochTime,
                                               lastFieldValue, lastEpochTime,
                                               factor, 0 /* precisionDigits */,
                                               rateSeconds, isEnableNegativeRate);

                // Log it
                final String msgFormat =
                        "Calculated (complex) long metric value. groupName={} name={} instClass={} metricValue={} " +
                        "factor={} rateSeconds={} isEnableNegativeRate={} " +
                        "fieldValue={} epochTime={} lastFieldValue={} lastEpoch={}";

                logger.debug(msgFormat,
                             metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(), metricValueInDouble,
                             factor, rateSeconds, isEnableNegativeRate,
                             fieldValueInDouble, epochTime, lastFieldValue, lastEpochTime);


                // Save last value and last epoch time
                lastFieldValue = fieldValueInDouble;
                lastEpochTime  = epochTime;

                // Convert the double value to long while preserving null
                if (metricValueInDouble == null) {
                    result = null;
                }
                else {
                    result = Math.round(metricValueInDouble);
                }

            }

            // Write the value to the engine unless result is null
            if (result != null) {
                // Create a long metric data object to hold the field value
                StatsEngineLongMetricData longData = new StatsEngineLongMetricData(valueName, result);

                // Add the  metric data object to the engine
                engineMetricsGroupData.addLongMetricData(longData);

            }

        } catch (Exception ex) {

            // We got an exception :-(

            // Reset last field value
            lastFieldValue = null;
            lastEpochTime  = 0;

            // log it and continue!
            final String msgFormat =
                    "Exception while processing long stats metric. groupName={0} name={1} instClass={2} " +
                            "factor={3} rateSeconds={4} epochTime={5} lastFieldValue={6} lastEpoch={7}";

            String msg = new MessageFormat(msgFormat).format(msgFormat,
                            metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(),
                            factor, rateSeconds, epochTime, lastFieldValue, lastEpochTime);

            logger.error(msg, ex);

        }

    }

    public String toString() {

        return String.format("long %s factor=%e rateSeconds=%d isEnableNegativeRate=%b",
                             super.toString(), factor, rateSeconds, isEnableNegativeRate);

    }

}
