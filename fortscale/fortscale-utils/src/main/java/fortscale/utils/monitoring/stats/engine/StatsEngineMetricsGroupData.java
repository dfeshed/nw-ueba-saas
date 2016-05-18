package fortscale.utils.monitoring.stats.engine;


import java.util.LinkedList;
import java.util.List;

import fortscale.utils.monitoring.stats.StatsMetricsTag;
import fortscale.utils.logging.Logger;

/**
 * A POJO class that holds metric group data to be passed to stats engine.
 *
 * It has the following data items:
 *   - Metric group common values: measurement epoch, group name, instrumented class
 *   - Tags (Stats StatsMetricsTag) list. A list of tag name/value pairs
 *   - Long fields data (StatsEngineLongMetricData) list. Basically it has field name and its value
 *   - Double fields data (StatsEngineDataMetricData) list. Basically it has field name and its value
 *   - String fields data (StatsEngineStringMetricData) list. Basically it has field name and its value
 *
 * The class is passes between the stats service and the stats engine
 *
 * Created by gaashh on 4/3/16.
 */

public class StatsEngineMetricsGroupData {

    private static final Logger logger = Logger.getLogger(StatsEngineMetricsGroupData.class);

    final static String NEW_LINE = System.getProperty("line.separator");

    // Metrics group name (Measurement name)
    protected String groupName;

    // Instrumented class (for mainly debugging and logging)
    protected Class<?> instrumentedClass;

    // A list of metrics tags
    protected List<StatsMetricsTag> metricsTags;

    // Measurement time in seconds
    protected long measurementEpoch;

    // List of long/double/string metrics data
    // POJO - no inheritance => -:(
    protected List<StatsEngineLongMetricData>   longMetricsDataList;
    protected List<StatsEngineDoubleMetricData> doubleMetricsDataList;
    protected List<StatsEngineStringMetricData> stringMetricsDataList;

    // Metric count (of all types)
    long metricCount;

    /**
     *  ctor - does nothing smart
     */
    // ctor
    public StatsEngineMetricsGroupData() {
        metricsTags           = new LinkedList<>();
        longMetricsDataList   = new LinkedList<>();
        doubleMetricsDataList = new LinkedList<>();
        stringMetricsDataList = new LinkedList<>();
        metricCount           = 0;
    }


    /**
     *
     * Add long metric data to the long fields list
     *
     * @param longMetricData - data to add
     */
    public void addLongMetricData(StatsEngineLongMetricData longMetricData) {


        logger.debug("Adding long metric data: groupName={} name={} value={} epoch={}",
                     groupName, longMetricData.getName(), longMetricData.getValue(), measurementEpoch);

        longMetricsDataList.add(longMetricData);
        metricCount++;
    }

    /**
     *
     * Add double metric data to the long fields list
     *
     * @param doubleMetricData - data to add
     */
    public void addDoubleMetricData(StatsEngineDoubleMetricData doubleMetricData) {

        logger.debug("Adding double metric data: groupName={} name={} value={} epoch={}",
                groupName, doubleMetricData.getName(), doubleMetricData.getValue(), measurementEpoch);

        doubleMetricsDataList.add(doubleMetricData);
        metricCount++;

    }

    /**
     *
     * Add string metric data to the long fields list
     *
     * @param stringMetricData - data to add
     */
    public void addStringMetricData(StatsEngineStringMetricData stringMetricData) {

        logger.debug("Adding string metric data: groupName={} name={} value={} epoch={}",
                groupName, stringMetricData.getName(), stringMetricData.getValue(), measurementEpoch);

        stringMetricsDataList.add(stringMetricData);
        metricCount++;

    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();

        // Header
        result.append( String.format("    groupName=%s  instrumentedClass=%s", groupName, instrumentedClass.getName()) );
        result.append(NEW_LINE);

        result.append( String.format("    measurementEpoch=%d metricCount=%d", measurementEpoch, metricCount) );
        result.append(NEW_LINE);

        // Tags
        result.append("    Tags: [");
        for (StatsMetricsTag metricTag : metricsTags) {
            result.append( String.format(" %s=%s", metricTag.getName(), metricTag.getValue()));
        }
        result.append(" ]" + NEW_LINE);

        // Long values
        result.append("    Longs: [");
        for (StatsEngineLongMetricData metricData : longMetricsDataList) {
            result.append( String.format(" %s=%d", metricData.getName(), metricData.getValue()));
        }
        result.append(" ]" + NEW_LINE);

        // Double values
        result.append("    Doubles: [");
        for (StatsEngineDoubleMetricData metricData : doubleMetricsDataList) {
            result.append( String.format(" %s=%e", metricData.getName(), metricData.getValue()));
        }
        result.append(" ]" + NEW_LINE);

        // String values
        result.append("    Strings: [");
        for (StatsEngineStringMetricData metricData : stringMetricsDataList) {
            result.append( String.format(" %s=%s", metricData.getName(), metricData.getValue()));
        }
        result.append(" ]" + NEW_LINE);

        return result.toString();
    }

    /**
     *
     * A static helper function to convert a list of StatsEngineMetricsGroupData into a string.
     * Useful for logging.
     *
     * @param engineMetricsGroupDataList - The list to convert
     * @return                           - A long string
     */
    static public String listToString(List<StatsEngineMetricsGroupData> engineMetricsGroupDataList) {

        StringBuilder sb = new StringBuilder();
        sb.append(NEW_LINE);
        for (StatsEngineMetricsGroupData engineMetricsGroupData : engineMetricsGroupDataList) {
            sb.append(engineMetricsGroupData.toString());
            sb.append(NEW_LINE);
        }

        return sb.toString();

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

    public List<StatsEngineDoubleMetricData> getDoubleMetricsDataList() {
        return doubleMetricsDataList;
    }

    public List<StatsEngineStringMetricData> getStringMetricsDataList() {
        return stringMetricsDataList;
    }

    public long getMetricCount() {
        return metricCount;
    }
}
