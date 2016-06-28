package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.engine.StatsEngineLongMetricData;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;
import java.text.MessageFormat;

import static fortscale.utils.time.TimestampUtils.convertEpochInAnyUnitToSeconds;
import static fortscale.utils.time.TimestampUtils.epochToDateInLong;

/**
 *
 * A value handler that handles metrics of type date. It extends the abstract MetricValueHandler.
 *
 * This value handler is use for fields with the StatsDateMetricsParams.
 *
 * It is responsible for reading the field value, manipulating it and writing in to the engine.
 *
 * See StatsDateMetricsParams for supported manipulations.
 *
 * Created by gaashh on 5/29/16.
 */
public class DateMetricValueHandler extends MetricValueHandler {

    private static final Logger logger = Logger.getLogger(DateMetricValueHandler.class);


    // The numeric fields access class. It holds the metrics group and the field object. It can provide the field value
    protected StatsNumericField statsNumericField;


    /**
     *
     * A simple ctor that holds the values for future use. It has base class values
     *
     * @param metricGroup        - See base class
     * @param field              - See base class
     * @param valueName          - See base class
     * @param statsNumericField  - The numeric field access instance associated with this metric
     */

    public DateMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
                                  StatsNumericField statsNumericField) {

        super(metricGroup, field, valueName);

        // Save ctor values
        this.statsNumericField = statsNumericField;

    }


    /**
     *
     * This function is called from addToEngineData() to read the field value, manipulate it and add it to the engine data
     * In case the field value is not relevant or it is invalid, the field value is not writen to the engine.
     *
     * It does the following:
     *   1. read the field value as long
     *   2. Convert it to "date in long format"
     *   3. If valid, the field value to the engine data
     *
     * @param engineMetricsGroupData - the engine data to add to
     * @param epochTime              - sample time. Might be used to calculate things like rate.
     */
    protected void calculateValueAndAddToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {

        Long fieldValue = null;  // Defined outside the try/catch block to enable reference in catch block

        // Just in case
        try {

            // Read the field. Assume it is epoch. Might be null
            fieldValue = statsNumericField.getAsLong();

            // The result. Set to null by default -> don't write it
            Long result = null;
            Long fieldEpochInSeconds = null;

            // If we have real value, convert it
            if (fieldValue != null) {
                // Convert the field epoch to seconds
                fieldEpochInSeconds = convertEpochInAnyUnitToSeconds(fieldValue);

                // Convert the field epoch (in seconds) to "date-in-long" format
                result = epochToDateInLong(fieldEpochInSeconds);
            }

            // Log it
            final String msgFormat =
                     "Calculating (simple) date metric value. groupName={} name={} instClass={} metricValue={} " +
                             "fieldValue={} fieldEpochInSeconds={} epochTime={}";

            logger.debug(msgFormat,
                     metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(), result,
                     fieldValue, fieldEpochInSeconds, epochTime);
            // Write the value to the engine unless result is null
            if (result != null) {

                // Create a long metric data object to hold the field value
                StatsEngineLongMetricData longData = new StatsEngineLongMetricData(valueName, result);

                // Add the  metric data object to the engine
                engineMetricsGroupData.addLongMetricData(longData);

            }

        } catch (Exception ex) {

            // We got an exception :-(

            // log it and continue!
            final String msgFormat =
                    "Exception while processing date stats metric. groupName={0} name={1} instClass={2} " +
                            "fieldValue={3} epochTime={4}";

            String msg = new MessageFormat(msgFormat).format(msgFormat,
                    metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(),
                    fieldValue, epochTime);

            logger.error(msg, ex);

        }


    }
    public String toString() {

         return String.format("date (epoch) %s", super.toString());

    }

}
