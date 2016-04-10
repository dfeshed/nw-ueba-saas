package fortscale.services.monitoring.stats.impl;

import fortscale.services.monitoring.stats.engine.StatsEngineMetricsGroupData;
import fortscale.services.monitoring.stats.StatsMetricsGroup;
import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.services.monitoring.stats.StatsMetricsGroupHandler;
import fortscale.services.monitoring.stats.annotations.StatsMetricsGroupParams;
import fortscale.services.monitoring.stats.annotations.StatsNumericMetricParams;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.LinkedList;


/**
 * Created by gaashh on 4/3/16.
 */
public class StatsMetricsGroupHandlerImpl implements StatsMetricsGroupHandler {

    // The stats group handled by this class
    protected StatsMetricsGroup metricsGroup;

    // Hold the stats service this instance is registered to.
    protected StatsServiceImpl statsService;

    // metricsGroup cached fields
    StatsMetricsGroupAttributes metricsGroupAttributes;
    Class metricsGroupInstrumentedClass;

    // The group name, either from groupAttributes or from annotation
    protected String groupName;

    // Metrics lists in this group
    List<MetricValueHandler> metricsValuesHandlers;


    // ctor
    public StatsMetricsGroupHandlerImpl(StatsMetricsGroup metricsGroup,
                                        StatsServiceImpl statsServiceImpl) {

        this.metricsGroup = metricsGroup;
        this.statsService = statsServiceImpl;

        metricsValuesHandlers = new LinkedList<MetricValueHandler>();

        // Cache a few fields from the metricsGroup
        metricsGroupAttributes = metricsGroup.getStatsMetricsGroupAttributes();
        metricsGroupInstrumentedClass = metricsGroup.getInstrumentedClass();

        // Compile the metric groups
        compileMetricsGroup();

    }

    public void manualUpdate() {
        manualUpdate(0);
    }

    public void manualUpdate(long epochTime) {
        writeToEngine(epochTime);
    }

    public void writeMetricGroupsToEngine(long epochTime) {
        writeToEngine(epochTime);
    }


    protected void compileMetricsGroup() {

        // Get group name from attributes. If might be empty!
        // Note: annotation might change the group name if it is set in the annotation
        groupName = metricsGroupAttributes.getGroupName();


        // Process the merticsGroup class annotations
        processMetricsGroupClassAnnotations();

        // TODO: Validate GroupName, also check if empty

        // Process the merticsGroup class annotations
        processMetricsGroupClassAnnotations();

        // Process the merticsGroup fields annotations
        processMetricsGroupFieldsAnnotations();

    }

    protected void processMetricsGroupClassAnnotations() {

        // No annotation -> NOP
        if (!metricsGroup.getClass().isAnnotationPresent(StatsMetricsGroupParams.class)) {
            return;
        }

        // Get the annotation
        StatsMetricsGroupParams groupAnno = metricsGroup.getClass().getAnnotation(StatsMetricsGroupParams.class);

        // If groupName is not empty, use it
        if (!groupAnno.name().isEmpty()) {
            groupName = groupAnno.name();
        }

    }

    protected void processMetricsGroupFieldsAnnotations() {

        // Loop all fields
        // notes:
        //    We look only declared fields, not inherited fields
        //    Only fields with annotations will be processed, the rest are ignored

        for (Field field : metricsGroup.getClass().getDeclaredFields()) {


            // Scan numeric metrics annotations
            StatsNumericMetricParams[] annoList = field.getAnnotationsByType(StatsNumericMetricParams.class);

            for (StatsNumericMetricParams fieldAnno : annoList) {
                processMetricsGroupOneFieldAnnotation(field, fieldAnno);
            }

            // TODO: scan string metric annotations

        }

    }

    protected void processMetricsGroupOneFieldAnnotation(Field field, StatsNumericMetricParams fieldAnno) {

        // Calc metric name. If annotation has name, use it. If not, default to field name
        String valueName;
        if (!fieldAnno.name().isEmpty()) {
            valueName = fieldAnno.name();
        } else {
            valueName = field.getName();
        }

        // Create a new numeric field handler (to access the fields via reflection)
        // TODO: check for failure (might throw)
        StatsNumericField numericField = StatsNumericField.builder(field, metricsGroup);

        NumericMetricValueHandler valueHandler = new NumericMetricValueHandler(metricsGroup, field, valueName,
                numericField,
                fieldAnno.factor(), fieldAnno.precisionDigits(), fieldAnno.rateSeconds());

        // Add the value handler to its list
        addMetricValueHandler(valueHandler);

    }

    protected void addMetricValueHandler(MetricValueHandler valueHandler) {

        // Check name does not already exist
        boolean exists = metricsValuesHandlers.stream().anyMatch(
                vh -> vh.getValueName().equals(valueHandler.getValueName()));

        if (exists) {
            // TODO throws ...
            System.out.println("duplicate name " + valueHandler.getValueName());
            return;
        }

        // Does not exists, add it
        metricsValuesHandlers.add(valueHandler);
    }


    protected void writeToEngine(long epochTime) {

        StatsEngineMetricsGroupData statsEngineMetricsGroupData;

        statsEngineMetricsGroupData = buildEngineMetricsGroupData(epochTime);
        statsService.getStatsEngine().writeMetricsGroupData(statsEngineMetricsGroupData);

    }

    protected StatsEngineMetricsGroupData buildEngineMetricsGroupData(long epochTime) {

        // If epochTime is the zero, get the current time
        if (epochTime == 0) {
            epochTime = System.currentTimeMillis() / 1000;
        }

        StatsEngineMetricsGroupData engineMetricsGroupData = new StatsEngineMetricsGroupData();

        // Add metricsGroup common fields

        engineMetricsGroupData.setGroupName(groupName);
        engineMetricsGroupData.setInstrumentedClass(metricsGroupInstrumentedClass);
        engineMetricsGroupData.setMeasurementEpoch(epochTime);
        engineMetricsGroupData.setMetricsTags(Collections.unmodifiableList(metricsGroupAttributes.getMetricsTags()));

        // Loop all field handlers to add their data to the engine data
        for (MetricValueHandler metricValueHandler : metricsValuesHandlers) {
            metricValueHandler.addToEngineData(engineMetricsGroupData, epochTime);
        }

        return engineMetricsGroupData;
    }

}
