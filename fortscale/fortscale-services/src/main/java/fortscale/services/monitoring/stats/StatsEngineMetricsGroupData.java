package fortscale.services.monitoring.stats;

import fortscale.services.monitoring.stats.impl.StatsServiceImpl;


import java.util.List;
import java.util.Map;

import fortscale.services.monitoring.stats.StatsMetricsTag;

/**
 * Created by gaashh on 4/3/16.
 */


// A POJO class that holds metric group data to be passed to stats engine

public class StatsEngineMetricsGroupData {

    protected String groupName;
    protected Class<?> instrumentedClass;
    protected List<StatsMetricsTag> metricsTags;

    protected int measurementEpoch;  // Measurement time in seconds

    protected Map<String,Long>    longValues;
    protected Map<String,Double>  doubleValues;
    protected Map<String,String>  stringValues;

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        final String NEW_LINE = System.getProperty("line.separator");

        // Header
        result.append( String.format("groupName=%s  instrumentedClass=%s", groupName, instrumentedClass.getName()) );
        result.append(NEW_LINE);

        result.append( String.format("measurementEpoch=%d", measurementEpoch) );
        result.append(NEW_LINE);

        // Tags
        for (StatsMetricsTag metricTag : metricsTags) {
            result.append( String.format("Tag: %s=%s", metricTag.getName(), metricTag.getValue()));
            result.append(NEW_LINE);
        }

        // Long values
        for (Map.Entry<String,Long> valueEntry : longValues.entrySet()) {
            result.append( String.format("Long: %s=%d", valueEntry.getKey(), valueEntry.getValue()));
            result.append(NEW_LINE);
        }

        // Double values
        for (Map.Entry<String,Double> valueEntry : doubleValues.entrySet()) {
            result.append( String.format("Double: %s=%f", valueEntry.getKey(), valueEntry.getValue()));
            result.append(NEW_LINE);
        }

        // String values
        for (Map.Entry<String,String> valueEntry : stringValues.entrySet()) {
            result.append( String.format("String: %s=%s", valueEntry.getKey(), valueEntry.getValue()));
            result.append(NEW_LINE);
        }

        return result.toString();
    }


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Class<?> getInstrumentedClass() {
        return instrumentedClass;
    }

    public void setInstrumentedClass(Class<?> instrumentedClass) {
        this.instrumentedClass = instrumentedClass;
    }

    public List<StatsMetricsTag> getMetricsTags() {
        return metricsTags;
    }

    public void setMetricsTags(List<StatsMetricsTag> metricsTags) {
        this.metricsTags = metricsTags;
    }

    public int getMeasurementEpoch() {
        return measurementEpoch;
    }

    public void setMeasurementEpoch(int measurementEpoch) {
        this.measurementEpoch = measurementEpoch;
    }

    public Map<String, Long> getLongValues() {
        return longValues;
    }

    public void setLongValues(Map<String, Long> longValues) {
        this.longValues = longValues;
    }

    public Map<String, Double> getDoubleValues() {
        return doubleValues;
    }

    public void setDoubleValues(Map<String, Double> doubleValues) {
        this.doubleValues = doubleValues;
    }

    public Map<String, String> getStringValues() {
        return stringValues;
    }

    public void setStringValues(Map<String, String> stringValues) {
        this.stringValues = stringValues;
    }

}
