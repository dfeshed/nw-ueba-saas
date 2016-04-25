package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.StatsMetricsGroupHandler;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.monitoring.stats.StatsMetricsGroup;
import fortscale.utils.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.utils.logging.Logger;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;


/**
 *
 * This is the main class that handles metrics groups. As such it holds its metrics group object.
 *
 * The class has two major roles:
 *   1. Upon registration, create a list of MetricValueHandlers that can process the metrics fields by scanning
 *      the application metric group class and its annotations.
 *      Note: only the fields declared in the class are scanned. Parent classes are not scanned.
 *
 *   2. Upon a call from the stats service, scan all the metrics value handler and write their values into
 *      StatsEngineMetricsGroupData
 *
 * The instance is created by the stats service when a metrics group is registered to the service
 *
 * Created by gaashh on 4/3/16.
 */
public class StatsMetricsGroupHandlerImpl implements StatsMetricsGroupHandler {

    private static final Logger logger = Logger.getLogger(StatsMetricsGroupHandlerImpl.class);


    // The stats group handled by this class
    protected StatsMetricsGroup metricsGroup;

    // The stats service this instance is registered to.
    protected StatsServiceImpl statsService;

    // metrics group cached fields
    StatsMetricsGroupAttributes metricsGroupAttributes;
    Class metricsGroupInstrumentedClass;

    // The group name, either from groupAttributes or from annotation
    protected String groupName;

    // Metrics lists in this group
    List<MetricValueHandler> metricsValuesHandlers;


    /**
     *
     * Called from stats service when it a new metrics group object is created.
     *
     * The main ctor function is to compile the metrics group fields into an metrics value handlers list.
     *
     * @param metricsGroup      - the metrics group that is being handled by this handler
     * @param statsServiceImpl  - back reference to the stats service
     */
    // ctor
    public StatsMetricsGroupHandlerImpl(StatsMetricsGroup metricsGroup,
                                        StatsServiceImpl statsServiceImpl) {
        // Save fields
        this.metricsGroup = metricsGroup;
        this.statsService = statsServiceImpl;

        metricsValuesHandlers = new LinkedList<MetricValueHandler>();

        // Cache a few fields from the metricsGroup
        metricsGroupAttributes = metricsGroup.getStatsMetricsGroupAttributes();
        metricsGroupInstrumentedClass = metricsGroup.getInstrumentedClass();

        // Compile the metric groups to create the value handlers list
        compileMetricsGroup();

        // Log it if enabled
        if (logger.isDebugEnabled()) {
            logger.debug("Metric group added: {}", this.toString());
        }

    }

    /**
     * See parent class
     */
    public void manualUpdate() {
        manualUpdate(0);
    }

    /**
     *
     * See parent class
     *
     * @param epochTime Sample time
     */
    public void manualUpdate(long epochTime) {
        writeToEngine(epochTime);
    }

    /**
     *
     * See parent class
     *
     * @param epochTime Sample time
     */
    public void writeMetricGroupsToEngine(long epochTime) {
        writeToEngine(epochTime);
    }


    /**
     *  The function scans the metrics group object fields and annotations and build a list of MetricsValuesHandler-s
     *  that represents the metrics fields.
     *
     *  It is call from the ctor upon metrics group object registration
     *
     */
    protected void compileMetricsGroup() {

        // Get group name from attributes. It is typically empty as value is set by the class annotation
        // Note: annotation might change the group name if it is set in the annotation
        groupName = metricsGroupAttributes.getGroupName();



        // Process the metrics group class annotations
        processMetricsGroupClassAnnotations();
        // TODO: Validate GroupName, also check if empty

        // Process the metrics group fields annotations
        processMetricsGroupFieldsAnnotations();

    }

    /**
     * Process the metric group class annotations.
     *
     * Supported annotations:
     *    @StatsMetricsGroupParams
     *       - name => group name
     *
     */
    protected void processMetricsGroupClassAnnotations() {

        // No annotation -> NOP
        if (!metricsGroup.getClass().isAnnotationPresent(StatsMetricsGroupParams.class)) {
            return;
        }

        // Get the annotation
        StatsMetricsGroupParams groupAnno = metricsGroup.getClass().getAnnotation(StatsMetricsGroupParams.class);

        // If groupName is not empty, use it. Possibly overwriting group name set by the group attributes
        if (!groupAnno.name().isEmpty()) {
            groupName = groupAnno.name();
        }

    }

    /**
     *
     * Scan metrics group fields annotations and call the relevant functions to do process the annotation.
     * Note: only the fields declared in the class are scanned. Parent classes are not scanned.
     *
     */
    protected void processMetricsGroupFieldsAnnotations() {

        // Loop all fields
        for (Field field : metricsGroup.getClass().getDeclaredFields()) {


            // Scan long metrics annotations
            StatsLongMetricParams[] longAnnoList = field.getAnnotationsByType(StatsLongMetricParams.class);

            for (StatsLongMetricParams fieldAnno : longAnnoList) {
                processLongMetricsAnnotation(field, fieldAnno);
            }

            // Scan double metrics annotations
            StatsDoubleMetricParams[] doubleAnnoList = field.getAnnotationsByType(StatsDoubleMetricParams.class);

            for (StatsDoubleMetricParams fieldAnno : doubleAnnoList) {
                processDoubleMetricsAnnotation(field, fieldAnno);
            }

            // TODO: scan string metric annotations
            // TODO: scan time   metric annotations

        }

    }

    /**
     *
     * Process fields with StatsLongMetricParams annotation. Calc the metric name and create a long value handler for it.
     *
     * @param field      - reelection field
     * @param fieldAnno  - annotation object
     */
    protected void processLongMetricsAnnotation(Field field, StatsLongMetricParams fieldAnno) {

        // Calc metric name. If annotation has name, use it. If not, default to field name
        String valueName;
        if (!fieldAnno.name().isEmpty()) {
            valueName = fieldAnno.name();
        } else {
            valueName = field.getName();
        }

        // Create a new numeric field handler (to access the fields via reflection)
        StatsNumericField numericField = StatsNumericField.builder(field, metricsGroup);

        LongMetricValueHandler valueHandler = new LongMetricValueHandler(metricsGroup, field, valueName,
                numericField, fieldAnno.factor() , fieldAnno.rateSeconds());

        // Add the value handler to its list
        addMetricValueHandler(valueHandler);

    }

    /**
     *
     * Process fields with StatsDoubleMetricParams annotation. Calc the metric name and create a double value handler
     * for it.
     *
     * @param field      - reelection field
     * @param fieldAnno  - annotation object
     */
    protected void processDoubleMetricsAnnotation(Field field, StatsDoubleMetricParams fieldAnno) {

        // Calc metric name. If annotation has name, use it. If not, default to field name
        String valueName;
        if (!fieldAnno.name().isEmpty()) {
            valueName = fieldAnno.name();
        } else {
            valueName = field.getName();
        }

        // Create a new numeric field handler (to access the fields via reflection)
        StatsNumericField numericField = StatsNumericField.builder(field, metricsGroup);

        DoubleMetricValueHandler valueHandler = new DoubleMetricValueHandler(metricsGroup, field, valueName,
                numericField,
                fieldAnno.factor(), fieldAnno.precisionDigits(), fieldAnno.rateSeconds());

        // Add the value handler to its list
        addMetricValueHandler(valueHandler);

    }

    /**
     *
     * Add a metric value handler to the to the value handler list.
     *
     * Verify metric field name is unique.
     *
     * @param valueHandler - value handler
     */
    protected void addMetricValueHandler(MetricValueHandler valueHandler) {

        // Check name does not already exist
        boolean exists = metricsValuesHandlers.stream().anyMatch(
                vh -> vh.getValueName().equals(valueHandler.getValueName()));

        if (exists) {

            String msg = String.format("Metric name %s already exists. groupName=%s  metricsGroup.class",
                                       valueHandler.getValueName(), groupName, metricsGroup.getClass().getName());

            logger.error(msg);
            throw (new StatsMetricsExceptions.MetricNameAlreadyExistsException(msg));
        }

        // Does not exists, add it
        metricsValuesHandlers.add(valueHandler);
    }


    /**
     *
     * Write the metrics fields to engine data object.
     *
     * It has two step:
     *   1. Build an engine data object that holds all the common values and field values
     *   2. Call the service engine to write the data object
     *
     * @param epochTime - the sample time. If zero, use current system time
     */
    protected void writeToEngine(long epochTime) {

        // Create an empty data engine object
        StatsEngineMetricsGroupData engineMetricsGroupData = new StatsEngineMetricsGroupData();

        // Populate the  engine data object with this metric group data
        populateEngineMetricsGroupData(engineMetricsGroupData, epochTime);

        // Log the results
        if (logger.isDebugEnabled()) {
            logger.debug("Metrics group handler writes date to the engine\n{}", engineMetricsGroupData.toString());
        }

        // Write the data to the engine
        statsService.getStatsEngine().writeMetricsGroupData(engineMetricsGroupData);

    }

    /**
     *
     *  Populate an engine data object with the the metrics group data
     *
     * It has a few steps:
     *   1. Calc sample time. If it zero, get the system time
     *   2. Add metrics group fields: group name, instrumented class and tags
     *   3. Call all the value handler to add their values
     *
     * @param engineMetricsGroupData - engine data object to populate
     * @param epochTime - the sample time. If zero, use current system time
     */
    protected void  populateEngineMetricsGroupData(StatsEngineMetricsGroupData engineMetricsGroupData, long epochTime) {

        // If epochTime is the zero, get the current time
        if (epochTime == 0) {
            epochTime = System.currentTimeMillis() / 1000;  // mSec -> Sec
        }

        // Add metricsGroup common fields
        engineMetricsGroupData.setGroupName(groupName);
        engineMetricsGroupData.setInstrumentedClass(metricsGroupInstrumentedClass);
        engineMetricsGroupData.setMeasurementEpoch(epochTime);
        engineMetricsGroupData.setMetricsTags(Collections.unmodifiableList(metricsGroupAttributes.getMetricsTags()));

        // Loop all field handlers to add their data to the engine data
        for (MetricValueHandler metricValueHandler : metricsValuesHandlers) {
            metricValueHandler.addToEngineData(engineMetricsGroupData, epochTime);
        }

    }

    public String toString() {

        StringBuilder result = new StringBuilder();
        final String NEW_LINE = System.getProperty("line.separator");

        // Header
        result.append(String.format("groupName=%s%s",
                                     groupName,
                                     NEW_LINE));

        // metricsGroup class
        if (metricsGroup != null) {
            result.append(String.format("    MetricsGroup.Class=%s%s",
                    metricsGroup.getClass().getName(),
                    NEW_LINE));
        }


        // Instrumented class
        if (metricsGroupInstrumentedClass != null) {
            result.append(String.format("    Instrumented.Class=%s%s",
                    metricsGroupInstrumentedClass.getName(),
                    NEW_LINE));
        }

        // Group attributes
        result.append(String.format("    Attributes: %s%s", metricsGroupAttributes.toString(), NEW_LINE) );

        // Metrics values handlers
        for (MetricValueHandler valueHandler : metricsValuesHandlers) {
            result.append( String.format("    Field: %s%s", valueHandler.toString(), NEW_LINE));
        }

        return result.toString();

    }

    // --- Getters/setters ---

    public String getGroupName() {
        return groupName;
    }

    public Class getMetricsGroupInstrumentedClass() {
        return metricsGroupInstrumentedClass;
    }

    public StatsMetricsGroupAttributes getMetricsGroupAttributes() {
        return metricsGroupAttributes;
    }

}

