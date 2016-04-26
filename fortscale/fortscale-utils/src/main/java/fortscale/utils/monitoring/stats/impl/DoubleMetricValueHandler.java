package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.engine.StatsEngineDoubleMetricData;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;
import java.text.MessageFormat;

/**
 *
 * A value handler that handles metrics of type double. It extends the abstract MetricValueHandler.
 *
 * This value handler is use for fields with the StatsDoubleMetricsParams.
 *
 * It is responsible for reading the field value, manipulating it and writing in to the engine.
 *
 * See StatsDoubleMetricsParams for supported manipulations.
 *
 * Created by gaashh on 4/5/16.
 */
public class DoubleMetricValueHandler extends MetricValueHandler {

    private static final Logger logger = Logger.getLogger(LongMetricValueHandler.class);


    // The numeric fields access class. It holds the metrics group and the field object. It can provide the field value
    protected StatsNumericField statsNumericField;

    // Metric's factor - see annotation for detailed description
    protected double factor;

    // Metric's rate - see annotation for detailed description
    protected long rateSeconds;

    // Metric's precision digits - see annotation for detailed description
    protected long precisionDigits;

    // Last read field value. null indicates no last read value. Used in rate calculation
    Double lastFieldValue;

    // Last read field epoch time. 0 indicates no last read value. Used in rate calculation.
    long lastEpochTime;

    /**
     *
     * A simple ctor that holds the values for future use. It has base class values and manipulation parameters
     *
     * @param metricGroup        - See base class
     * @param field              - See base class
     * @param valueName          - See base class
     * @param statsNumericField  - The numeric field access instance associated with this metric
     * @param factor             - See StatsDoubleMetricsParams
     * @param precisionDigits    - See StatsDoubleMetricsParams
     * @param rateSeconds        - See StatsDoubleMetricsParams
     */
    // TODO: add validation check, name, annotation params, ...
    public DoubleMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
                                    StatsNumericField statsNumericField,
                                    double factor, long precisionDigits, long rateSeconds) {

        super(metricGroup, field, valueName);

        // Save ctor values
        this.statsNumericField = statsNumericField;
        this.factor            = factor;
        this.precisionDigits   = precisionDigits;
        this.rateSeconds       = rateSeconds;

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

            // Read the field value as double
            Double fieldValueInDouble = statsNumericField.getAsDouble();

            // Metric value (we are going to calculate it)
            Double metricValueInDouble;

            // Do we have simple field (no modifications like factor or rate) or complex one?
            if ( factor < 0 && rateSeconds == 0 && precisionDigits == 0) {

                // Simple field, metric value is the field value
                metricValueInDouble = fieldValueInDouble;

                // Log it
                final String msgFormat =
                        "Calculating (simple) double metric value. groupName={} name={} instClass={} metricValue={} " +
                                "factor={} precisionDigits={} rateSeconds={} epochTime={}";

                logger.debug(msgFormat,
                        metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(), fieldValueInDouble,
                        factor, precisionDigits, rateSeconds, epochTime);

            }
            else {

                // Calculate the metric value with all the modification. Get some "help" form the double metric handler
                metricValueInDouble = DoubleMetricValueHandler.calculateMetricValueWithModifications(
                                          fieldValueInDouble, epochTime, lastFieldValue, lastEpochTime,
                                          factor, precisionDigits, rateSeconds);

                // Log it
                final String msgFormat =
                        "Calculating (complex) long metric value. groupName={} name={} instClass={} metricValue={} " +
                                "factor={} precisionDigits={} rateSeconds={} " +
                                "fieldValue={} epochTime={} lastFieldValue={} lastEpoch={}";

                logger.debug(msgFormat,
                        metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(), metricValueInDouble,
                        factor, precisionDigits, rateSeconds,
                        fieldValueInDouble, epochTime, lastFieldValue, lastEpochTime);


                // Save last value and last epoch time
                lastFieldValue = fieldValueInDouble;
                lastEpochTime  = epochTime;

            }

            // Write the value to the engine unless result is null
            if (metricValueInDouble != null) {

                // Create a double metric data object to hold the field value
                StatsEngineDoubleMetricData doubleData = new StatsEngineDoubleMetricData(valueName, metricValueInDouble);

                // Add the  metric data object to the engine
                engineMetricsGroupData.addDoubleMetricData(doubleData);

            }

        } catch (Exception ex) {

            // We got an exception :-(

            // Reset last field value
            lastFieldValue = null;
            lastEpochTime  = 0;

            // log it and continue!
            final String msgFormat =
                    "Exception while processing double stats metric. groupName={0} name={1} instClass={2} " +
                            "factor={3} precisionDigits={4} rateSeconds={5} epochTime={6} lastFieldValue={7} lastEpoch={8}";

            String msg = new MessageFormat(msgFormat).format(msgFormat,
                    metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(),
                    factor, precisionDigits, rateSeconds, epochTime, lastFieldValue, lastEpochTime);

            logger.error(msg, ex);

        }

    }


    /**
     *
     * Calculates the metric value. It handles the metric modifies (e.g. factor, rate, precision digits)
     *
     * If the value cannot be calculated, a null is returned.
     *
     * See StatsDoubleMetricParams for detailed description
     *
     * Note: LongMetricValueHandler uses this function too. Keep it static!
     *
     * @param fieldValue      - current field value
     * @param epochTime       - current epoch (used in rate math)
     * @param lastFieldValue  - last field value (used in rate math) -> null -> invalid
     * @param lastEpochTime   - last epoch (used in rate math). 0 -> invalid
     * @param factor          - factor. Multiple by this value unless negative
     * @param precisionDigits - round the number to 10^precisionDigits. 0 -> no rounding
     * @param rateSeconds     - rate time period. 0 -> no rate
     * @return metric value or null
     */
    static public Double calculateMetricValueWithModifications(Double fieldValue, long epochTime, Double lastFieldValue,
                                                               long lastEpochTime,
                                                               double factor, long precisionDigits, long rateSeconds) {

        // Check null value;
        if (fieldValue == null) {
            return null;
        }

        // The simple case, result is the value
        Double result = fieldValue;

        // Process rate, if exists
        if (rateSeconds != 0) {
            // rate is set, check we have all the values we need
            if (lastFieldValue == null || lastEpochTime == 0 || epochTime == lastEpochTime) {
                return null;
            }

            result = (fieldValue - lastFieldValue) / (epochTime - lastEpochTime) * rateSeconds;
        }

        // Process factor, if exists
        if (factor >= 0) {
            result = result * factor;
        }

        // Process precisionDigits, if exists
        if (precisionDigits != 0) {
            double scale = Math.pow(10, precisionDigits);
            result = Math.round( result * scale ) / scale;
        }

        // All is good
        return result;
    }

    public String toString() {

        return String.format("double %s factor=%e precisionDigits=%d rateSeconds=%d",
                super.toString(), factor, precisionDigits, rateSeconds);

    }

}
