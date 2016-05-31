package fortscale.utils.monitoring.stats.impl;

import fortscale.utils.monitoring.stats.*;
import fortscale.utils.monitoring.stats.annotations.StatsDateMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsDoubleMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsLongMetricParams;
import fortscale.utils.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.utils.monitoring.stats.annotations.StatsStringMetricParams;
import fortscale.utils.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.utils.logging.Logger;
import fortscale.utils.process.hostnameService.HostnameService;

import java.lang.reflect.Field;
import java.util.*;


/**
 *
 * This is the main class that handles metrics groups. As such it holds its metrics group object.
 *
 * The class has two major roles:
 *   1. Upon registration, build a list of MetricValueHandlers that can process the metrics fields by scanning
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

    // manual updated mode:
    //    True -> metrics group will be updated via manualUpdate()
    //    False -> automatic updates using periodic updates thread.
    // Default is false
    // Note: value is initially set from attributes but it might be forced to true is manualUpdate() is called
    protected boolean isManualUpdateMode = false;

    // The group name, either from groupAttributes or from annotation
    protected String groupName;

    // Metrics group tags. It is merge of the metrics group attributes tags with the stats service automatic tags
    protected List<StatsMetricsTag> metricsTagList;

    // Holds the last known host name. It is used at calculateMetricTags() to optimize metric tags list calculation
    protected String lastKnowHostname;

    // Metrics fields handlers of this group
    protected List<MetricValueHandler> metricsValuesHandlers;

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

        // Save the manual update flag
        isManualUpdateMode = metricsGroupAttributes.isManualUpdateMode();

        // Compile the metric groups to build the value handlers list
        // Note: it also update groupName
        compileMetricsGroup();

        // Calculate the metric group tags list. It is merge of the metrics group attributes tags with the
        // stats service automatic tags.
        // Should be after compileMetricsGroup() to make sure groupName is up to date
        calculateMetricTags();

        // Short log at info, if enabled
        if (logger.isInfoEnabled()) {
            logger.info("Stats metric group {} with tags [{}] added", groupName, StatsMetricsTag.metricsTagListToString(metricsTagList));
        }

        // Detailed log it if enabled
        if (logger.isDebugEnabled()) {
            logger.debug("Stats metric group added {}", this.toString());
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
     * Call writeToEngine to do the real work but catch and log any exception
     *
     * If class is mode is not manual updates, log an error and set the manual update flag
     *
     * @param epochTime Sample time
     */
    public void manualUpdate(long epochTime) {

        try {
            // Ensure manual update mode is set
            if (isManualUpdateMode == false) {

                // Log an error
                logger.error("Forcing manual update mode. manualUpdate() called but metrics group manual update mode was not set. " +
                             "epoch={} metricsGroup.class={} instrumentedClass={} attributes={}",
                              epochTime, metricsGroup.getClass().getName(), metricsGroupInstrumentedClass.getName(),
                             metricsGroupAttributes.toString());

                // Force the manual update mode flag to true
                isManualUpdateMode = true;
            }

            // Do it
            writeToEngine(epochTime);
        }
        catch (Exception ex) {
            // Just log the exception
            String msg = String.format("manualUpdate() got an exception. epoch=%d metricsGroup.class=%s instrumentedClass=%s",
                                        epochTime, metricsGroup.getClass().getName(), metricsGroupInstrumentedClass.getName());
            logger.error(msg, ex);
        }
    }

    /**
     *
     * Sample the metrics group metrics and to write them to the engine. Typically called by the stats engine at periodic
     * update or from the metrics group at manual update
     *
     * This is internal function
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
     *  Note: it also updates groupName
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

            // Disable field access permission checks (e.g. private)
            field.setAccessible(true);

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

            // Scan date metrics annotations
            StatsDateMetricParams[] dateAnnoList = field.getAnnotationsByType(StatsDateMetricParams.class);

            for (StatsDateMetricParams fieldAnno : dateAnnoList) {
                processDateMetricsAnnotation(field, fieldAnno);
            }

            // Scan string metrics annotations
            StatsStringMetricParams[] stringAnnoList = field.getAnnotationsByType(StatsStringMetricParams.class);

            for (StatsStringMetricParams fieldAnno : stringAnnoList) {
                processStringMetricsAnnotation(field, fieldAnno);
            }

        }

    }

    /**
     *
     * Process fields with StatsLongMetricParams annotation. Calc the metric name and build a long value handler for it.
     *
     * @param field      - reflection field
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
                numericField, fieldAnno.factor() , fieldAnno.rateSeconds(), fieldAnno.negativeRate());

        // Add the value handler to its list
        addMetricValueHandler(valueHandler);

    }

    /**
     *
     * Process fields with StatsDoubleMetricParams annotation. Calc the metric name and build a double value handler
     * for it.
     *
     * @param field      - reflection field
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
                fieldAnno.factor(), fieldAnno.precisionDigits(), fieldAnno.rateSeconds(), fieldAnno.negativeRate());

        // Add the value handler to its list
        addMetricValueHandler(valueHandler);

    }


    /**
     *
     * Process fields with StatsDateMetricParams annotation. Calc the metric name and build a date value handler
     * for it.
     *
     * @param field      - reflection field
     * @param fieldAnno  - annotation object
     */
    protected void processDateMetricsAnnotation(Field field, StatsDateMetricParams fieldAnno) {

        // Calc metric name. If annotation has name, use it. If not, default to field name
        String valueName;
        if (!fieldAnno.name().isEmpty()) {
            valueName = fieldAnno.name();
        } else {
            valueName = field.getName();
        }

        // Create a new numeric field handler (to access the fields via reflection)
        StatsNumericField numericField = StatsNumericField.builder(field, metricsGroup);

        DateMetricValueHandler valueHandler = new DateMetricValueHandler(metricsGroup, field, valueName, numericField);

        // Add the value handler to its list
        addMetricValueHandler(valueHandler);

    }

    /**
     *
     * Process fields with StatsStringMetricParams annotation. Calc the metric name and build a date value handler
     * for it.
     *
     * @param field      - reflection field
     * @param fieldAnno  - annotation object
     */
    protected void processStringMetricsAnnotation(Field field, StatsStringMetricParams fieldAnno) {

        // Calc metric name. If annotation has name, use it. If not, default to field name
        String valueName;
        if (!fieldAnno.name().isEmpty()) {
            valueName = fieldAnno.name();
        } else {
            valueName = field.getName();
        }

        // Create a new numeric field handler (to access the fields via reflection)
        StatsStringField stringField = StatsStringField.builder(field, metricsGroup);


        StringMetricValueHandler valueHandler = new StringMetricValueHandler(metricsGroup, field, valueName, stringField);

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
     *   2. If the engine
     *   2. Call the service engine to write the data object
     *
     * @param epochTime - the sample time. If zero, use current system time
     */
    protected void writeToEngine(long epochTime) {

        // Create an empty data engine object
        StatsEngineMetricsGroupData engineMetricsGroupData = new StatsEngineMetricsGroupData();

        // Populate the  engine data object with this metric group data
        populateEngineMetricsGroupData(engineMetricsGroupData, epochTime);

        // Get metric count and discard the object if there are no metric
        long metricCount = engineMetricsGroupData.getMetricCount();
        if (metricCount == 0) {
            logger.debug("Metrics group handler discards the metric because it has no metric\n{}", engineMetricsGroupData.toString());
            return;
        }

        // Log the results
        if (logger.isDebugEnabled()) {
            logger.debug("Metrics group handler writes metrics data with {} metrics to the engine\n{}",
                         metricCount, engineMetricsGroupData.toString());
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


        // Update the metric tags list (in case host name was changed)
        calculateMetricTags();

        // Add metricsGroup common fields
        engineMetricsGroupData.setGroupName(groupName);
        engineMetricsGroupData.setInstrumentedClass(metricsGroupInstrumentedClass);
        engineMetricsGroupData.setMeasurementEpoch(epochTime);
        engineMetricsGroupData.setMetricsTags(metricsTagList);

        // Loop all field handlers to add their data to the engine data
        for (MetricValueHandler metricValueHandler : metricsValuesHandlers) {
            metricValueHandler.addToEngineData(engineMetricsGroupData, epochTime);
        }

    }

    /**
     * Calculate the metrics tags list and store it at metricsTagList. The tag list is the metric group attributes tags
     * plus the following tags that are added automatically:
     *
     * "process"      - process name. Value is from stats service process name unless it is overridden by the metrics group attributes
     * "processGroup" - process group name. Value is from stats service process group name unless it is overridden by the metrics group attributes
     * "host"         - host name. Value is retrived from the hostname service, if it exists
     *
     * Tags that are added automatically has priority over metrics group attributes tags.
     *
     * Note: the host name might change over time, hence this function should be call on every update. To optimized
     * execution time and to minimize log noise, the function does actual work only on when host name was changed
     *
     *
     */
    protected void calculateMetricTags() {

        // Can we skip the work provided that
        // 1. metricsTagList was populated
        // 2. host service is null (hence it can't be change)
        // 3. host name was not changed

        boolean skip = true;

        if (metricsTagList == null) {
            skip = false;
        }

        String hostname = "internal-error"; // Just in case
        HostnameService hostnameService = statsService.getHostnameService();
        if (hostnameService != null) {
            hostname = hostnameService.getHostname();
            if (lastKnowHostname == null || !lastKnowHostname.equals(hostname)) {
                lastKnowHostname = hostname;
                skip = false;
            }
        }

        if (skip) {
            return;
        }


        // An ordered map of tag name to tag of the tags to add
        Map<String,StatsMetricsTag> additionalTags = new LinkedHashMap<>();

        // Add process name to additional tag list
        // process name is taken from the stats service unless the metric group attributes overrides it
        String tagProcessName;
        String overrideProcessName = metricsGroupAttributes.getOverrideProcessName();
        if (overrideProcessName == null) {
            // Typical operation, get process name from stats service
            tagProcessName = statsService.getProcessName();
        }
        else {
            // Override, get process name from metrics group attributes
            logger.debug("Overriding process name for group {} to {} from attributes", groupName, overrideProcessName);
            tagProcessName = overrideProcessName;
        }
        additionalTags.put(StatsService.PROCESS_NAME_TAG_NAME,
                           new StatsMetricsTag(StatsService.PROCESS_NAME_TAG_NAME, tagProcessName));

        // Add process group name to additional tag list
        // process group name is taken from the stats service unless the metric group attributes overrides it
        String tagProcessGroupName;
        String overrideProcessGroupName = metricsGroupAttributes.getOverrideProcessGroupName();
        if (overrideProcessGroupName == null) {
            // Typical operation, get process group name from stats service
            tagProcessGroupName = statsService.getProcessGroupName();
        }
        else {
            // Override, get process name from metrics group attributes
            logger.debug("Overriding process group name for group {} to {} from attributes", groupName, overrideProcessGroupName);
            tagProcessGroupName = overrideProcessGroupName;
        }
        additionalTags.put(StatsService.PROCESS_GROUP_NAME_TAG_NAME,
                           new StatsMetricsTag(StatsService.PROCESS_GROUP_NAME_TAG_NAME, tagProcessGroupName));

        // Add hostname, if hostname service is available
        if (hostnameService == null) {
            // No hostname service, just log it
            logger.debug("Hostname service is not available for group {}, not setting host tag", groupName);
        }
        else {
            additionalTags.put(StatsService.HOSTNAME_TAG_NAME,
                               new StatsMetricsTag(StatsService.HOSTNAME_TAG_NAME, hostname));
        }

        // Build the tags list.
        metricsTagList = new LinkedList<>();

        // First step, copy the metric group tags while omitted tags that will be added by the service
        List<StatsMetricsTag> metricGroupTags = metricsGroupAttributes.getMetricsTags();
        for (StatsMetricsTag metricTag : metricGroupTags) {
            // Copy tag from tag list unless it is system tag
            String name = metricTag.getName();
            if (additionalTags.containsKey(name)) {
                // Overriding tag, log a warning but do not copy the tag
                logger.warn("Overriding group {} tag {} old value {} with automatic value", groupName, name, metricTag.getValue());
            }
            else {
                metricsTagList.add(metricTag);
            }
        }

        // Seconds step, add the service tags at the end
        for (String name : additionalTags.keySet() ) {
            StatsMetricsTag tag = additionalTags.get(name);
            metricsTagList.add(tag);
        }

        logger.debug("Metrics tags list was updated for group {}. Tags are [{}]",
                     groupName, StatsMetricsTag.metricsTagListToString(metricsTagList));


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

        // Tags
        result.append(String.format("    Tags: [%s]%s", StatsMetricsTag.metricsTagListToString(metricsTagList), NEW_LINE));

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

    @Override
    public StatsServiceImpl getStatsService() {
        return statsService;
    }

    public Class getMetricsGroupInstrumentedClass() {
        return metricsGroupInstrumentedClass;
    }

    public StatsMetricsGroupAttributes getMetricsGroupAttributes() {
        return metricsGroupAttributes;
    }

    public boolean isManualUpdateMode() {
        return isManualUpdateMode;
    }

    public void setManualUpdateMode(boolean manualUpdateMode) {
        isManualUpdateMode = manualUpdateMode;
    }
}

