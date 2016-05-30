package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.engine.StatsEngineStringMetricData;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;
import java.text.MessageFormat;

/**
 *
 * A value handler that handles metrics of type String. It extends the abstract MetricValueHandler.
 *
 * This value handler is use for fields with the StatsStringMetricsParams.
 *
 * It is responsible for reading the field value, manipulating it and writing in to the engine.
 *
 * See StatsStringMetricsParams for supported manipulations.
 *
 * Created by gaashh on 5/29/16.
 */
public class StringMetricValueHandler extends MetricValueHandler {

    private static final Logger logger = Logger.getLogger(StringMetricValueHandler.class);


    // The string fields access class. It holds the metrics group and the field object. It can provide the field's value
    protected StatsStringField statsStringField;


    /**
     *
     * A simple ctor that holds the values for future use. It has base class values
     *
     * @param metricGroup        - See base class
     * @param field              - See base class
     * @param valueName          - See base class
     * @param statsStringField   - The string field access instance associated with this metric
     */

    // TODO: add validation check, name, annotation params, ...
    public StringMetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName,
                                    StatsStringField statsStringField) {

        super(metricGroup, field, valueName);

        // Save ctor values
        this.statsStringField = statsStringField;

    }


    /**
     *
     * This function is called from addToEngineData() to read the field value, manipulate it and add it to the engine data
     * In case the field value is not relevant or it is invalid, the field value is not writen to the engine.
     *
     * It does the following:
     *   1. read the field value as string
     *   2. If valid, the field value to the engine data
     *
     * @param engineMetricsGroupData - the engine data to add to
     * @param epochTime              - sample time. Might be used to calculate things like rate.
     */
    protected void calculateValueAndAddToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {

        String fieldValue = null;  // Defined outside the try/catch block to enable reference in catch block

        // Just in case
        try {

            // Read the field. Assume it is epoch. Might be null
            fieldValue = statsStringField.getAsString();

            // The result. Set to null by default -> don't write it
            String result = null;

            // If we have real value, use it
            if (fieldValue != null) {

                result = fieldValue;
            }

            // Log it
            final String msgFormat =
                    "Calculating (simple) string metric value. groupName={} name={} instClass={} metricValue='{}' epochTime={}";

            logger.debug(msgFormat,
                    metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(), result, epochTime);

            // Write the value to the engine unless result is null
            if (result != null) {

                // Create a long metric data object to hold the field value
                StatsEngineStringMetricData stringData = new StatsEngineStringMetricData(valueName, result);

                // Add the  metric data object to the engine
                engineMetricsGroupData.addStringMetricData(stringData);

            }

        } catch (Exception ex) {

            // We got an exception :-(

            // log it and continue!
            final String msgFormat =
                    "Exception while processing string stats metric. groupName={0} name={1} instClass={2} " +
                            "fieldValue={3} epochTime={4}";

            String msg = new MessageFormat(msgFormat).format(msgFormat,
                    metricGroup.getGroupName(), valueName, metricGroup.getInstrumentedClass().getName(),
                    fieldValue, epochTime);

            logger.error(msg, ex);

        }


    }
    public String toString() {

        return String.format("string %s", super.toString());

    }

}

