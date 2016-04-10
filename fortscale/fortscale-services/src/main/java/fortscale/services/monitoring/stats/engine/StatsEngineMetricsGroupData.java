package fortscale.services.monitoring.stats.engine;


import java.util.LinkedList;
import java.util.List;

import fortscale.services.monitoring.stats.StatsMetricsTag;

/**
 * Created by gaashh on 4/3/16.
 */


// A POJO class that holds metric group data to be passed to stats engine

public class StatsEngineMetricsGroupData {

    protected String groupName;
    protected Class<?> instrumentedClass;
    protected List<StatsMetricsTag> metricsTags;

    protected long measurementEpoch;  // Measurement time in seconds

    // POJO - no inheritance => -:(
    protected List<StatsEngineLongMetricData>   longMetricsDataList;
    protected List<StatsEngineDoubleMetricData> doubleMetricsDataList;
    protected List<StatsEngineStringMetricData> stringMetricsDataList;

    // ctor
    public StatsEngineMetricsGroupData() {
        longMetricsDataList   = new LinkedList<>();
        doubleMetricsDataList = new LinkedList<>();
        stringMetricsDataList = new LinkedList<>();
    }

    public void addLongMetricData(StatsEngineLongMetricData longMetricData) {
        longMetricsDataList.add(longMetricData);
    }

    public void addDoubleMetricData(StatsEngineDoubleMetricData doubleMetricData) {
        doubleMetricsDataList.add(doubleMetricData);
    }

    public void addStringMetricData(StatsEngineStringMetricData stringMetricData) {
        stringMetricsDataList.add(stringMetricData);
    }

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
        for (StatsEngineLongMetricData metricData : longMetricsDataList) {
            result.append( String.format("Long: %s=%d", metricData.getName(), metricData.getValue()));
            result.append(NEW_LINE);
        }

        // Double values
        for (StatsEngineDoubleMetricData metricData : doubleMetricsDataList) {
            result.append( String.format("Data: %s=%d", metricData.getName(), metricData.getValue()));
            result.append(NEW_LINE);
        }

        // String values
        for (StatsEngineStringMetricData metricData : stringMetricsDataList) {
            result.append( String.format("String: %s=%d", metricData.getName(), metricData.getValue()));
            result.append(NEW_LINE);
        }

        return result.toString();
    }

    // --- getters/setters ---


    public Class<?> getInstrumentedClass() {
        return instrumentedClass;
    }

    public void setInstrumentedClass(Class<?> instrumentedClass) {
        this.instrumentedClass = instrumentedClass;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<StatsMetricsTag> getMetricsTags() {
        return metricsTags;
    }

    public void setMetricsTags(List<StatsMetricsTag> metricsTags) {
        this.metricsTags = metricsTags;
    }

    public long getMeasurementEpoch() {
        return measurementEpoch;
    }

    public void setMeasurementEpoch(long measurementEpoch) {
        this.measurementEpoch = measurementEpoch;
    }

    public List<StatsEngineLongMetricData> getLongMetricsDataList() {
        return longMetricsDataList;
    }

    public void setLongMetricsDataList(List<StatsEngineLongMetricData> longMetricsDataList) {
        this.longMetricsDataList = longMetricsDataList;
    }

    public List<StatsEngineDoubleMetricData> getDoubleMetricsDataList() {
        return doubleMetricsDataList;
    }

    public void setDoubleMetricsDataList(List<StatsEngineDoubleMetricData> doubleMetricsDataList) {
        this.doubleMetricsDataList = doubleMetricsDataList;
    }

    public List<StatsEngineStringMetricData> getStringMetricsDataList() {
        return stringMetricsDataList;
    }

    public void setStringMetricsDataList(List<StatsEngineStringMetricData> stringMetricsDataList) {
        this.stringMetricsDataList = stringMetricsDataList;
    }
}
