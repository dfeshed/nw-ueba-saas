package fortscale.services.monitoring.stats.impl;

import java.lang.reflect.Field;


import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;


/**
 *
 * An abstract class that handles a field value from reading the field value to writing it to the engine.
 *
 * The class implements the field manipulators (e.g. scaling, rate calculation, ...)
 *
 * Created by gaashh on 4/5/16.
 */
abstract public class MetricValueHandler {

    // The metric group that holds the field
    protected StatsMetricsGroup metricGroup;

    // The field reflection object
    protected Field             field;

    // The field name (used when written the value to the engine)
    protected String            valueName;

    /**
     *
     * A simple ctor that saves a few fields
     *
     * @param metricGroup - the metric group that holds the field
     * @param field       - the field reflection object
     * @param valueName   - the field name (used when written the value to the engine)
     */
    public MetricValueHandler(StatsMetricsGroup metricGroup, Field field, String valueName) {

        this.metricGroup = metricGroup;
        this.field       = field;
        this.valueName   = valueName;
    }

    /**
     *
     * This function is called when the metric group is written to the engine.
     *
     * Add the field value(s) to the engine data. Obviously, the fields values are calculated before adding them :-)
     *
     * @param engineMetricsGroupData - the engine data to add to
     * @param epochTime              - sample time. Might be used to claculate things like rate.
     */
    abstract public void addToEngineData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime);

    public String toString() {

        return String.format("%s: field.name=%s field.type=%s",
                             valueName, field.getName(), field.getType().getName() );

    }

    // --- getters / setters ---

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
