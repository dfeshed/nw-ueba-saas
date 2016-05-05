package fortscale.utils.monitoring.stats.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.monitoring.stats.StatsMetricsTag;
import fortscale.utils.logging.Logger;
import  fortscale.utils.monitoring.stats.models.engine.EngineData;
import  fortscale.utils.monitoring.stats.models.engine.MetricGroup;
import  fortscale.utils.monitoring.stats.models.engine.Tag;
import  fortscale.utils.monitoring.stats.models.engine.LongField;
import  fortscale.utils.monitoring.stats.models.engine.DoubleField;
import  fortscale.utils.monitoring.stats.models.engine.StringField;

import java.util.LinkedList;
import java.util.List;


/**
 * A Helper class that provide common functionality to a few engines (e.g. Samza, topic)
 *
 * Created by gaashh on 4/17/16.
 */
public abstract class StatsEngineBase implements StatsEngine {

    private static final Logger logger = Logger.getLogger(StatsEngineBase.class);

    // Holds a list of metrics group data entries written by the metric group handlers via writeMetricsGroupData()
    // until the engine will collect the data.
    // The derived class consumes the data
    // The list might be null when the engine starts and after the engine consumed the data
    // The list is locked by accumulatedMetricsGroupDataListLock
    protected List<StatsEngineMetricsGroupData> accumulatedMetricsGroupDataList = null;

    // A lock object for accumulatedMetricsGroupDataList
    protected Object accumulatedMetricsGroupDataListLock = new Object();

    // Something to write to the JSON. Will be more important in the future
    final long MODEL_VERSION = 100;

    /**
     *
     * Adds an metrics group data entry to the engine. The engine collects those entries until it writes them
     * to the destination.
     *
     * Typically called from the metrics group handler
     *
     * @param metricsGroupData - the metrics group data to write to the stats engine
     */
    @Override
    public void writeMetricsGroupData(StatsEngineMetricsGroupData metricsGroupData) {

        // Lock the
        synchronized (accumulatedMetricsGroupDataListLock) {

            // If the accumulated metrics group list does not exists, create it
            if (accumulatedMetricsGroupDataList == null) {
                accumulatedMetricsGroupDataList = new LinkedList<>();
            }

            // Add the new entry at the end
            accumulatedMetricsGroupDataList.add(metricsGroupData);

        }

    }

    /**
     * 
     * Creates a model engine data (the JSON used in the metrics topic) from stats engine data (of the API) list
     * 
     * @return
     */
    protected EngineData statsEngineDataToModelData(List<StatsEngineMetricsGroupData> statsEngineDataList) {

        EngineData engineData = new EngineData();

        // Common fields
        engineData.setVersion( MODEL_VERSION );

        // Scan engine data entries and add them
        List<MetricGroup> modelMetricGroupList = new LinkedList<MetricGroup>();
        for (StatsEngineMetricsGroupData statsMetricGroupData : statsEngineDataList) {

            // Build the model metric group
            MetricGroup metricGroup = statsEngineDataEntryToModelMetricGroup(statsMetricGroupData);

            // Add metric group to the list
            modelMetricGroupList.add(metricGroup);

        }
        engineData.setMetricGroups(modelMetricGroupList);

        return engineData;

    }

    /**
     * Creates a model metric group from stat engine data entry
     * 
     * A helper function to statsEngineDataToModelData()
     */
    protected MetricGroup statsEngineDataEntryToModelMetricGroup (StatsEngineMetricsGroupData statsEngineData) {

        MetricGroup metricGroup = new MetricGroup();         
        
        // Add common fields
        metricGroup.setGroupName( statsEngineData.getGroupName() );
        metricGroup.setMeasurementEpoch( statsEngineData.getMeasurementEpoch() );
        metricGroup.setInstrumentedClass( statsEngineData.getInstrumentedClass().getName() );
            
        // Add tags from attributes
        List<Tag> modelTagsList = new LinkedList<>(); 
        for (StatsMetricsTag statsTag : statsEngineData.getMetricsTags() ) {
            Tag tag = new Tag( statsTag.getName(), statsTag.getValue() );
            modelTagsList.add( tag );
        }
        metricGroup.setTags(modelTagsList);

        // Add long fields from attributes
        List<LongField> modelLongFieldsList = new LinkedList<>();
        for (StatsEngineLongMetricData statsLongField : statsEngineData.getLongMetricsDataList() ) {
            LongField longField =  new LongField( statsLongField.getName(), statsLongField.getValue() );
            modelLongFieldsList.add( longField );
        }
        metricGroup.setLongFields(modelLongFieldsList);

        // Add double fields from attributes
        List<DoubleField> modelDoubleFieldsList = new LinkedList<>();
        for (StatsEngineDoubleMetricData statsDoubleField : statsEngineData.getDoubleMetricsDataList() ) {
            DoubleField doubleField =  new DoubleField( statsDoubleField.getName(), statsDoubleField.getValue() );
            modelDoubleFieldsList.add( doubleField );
        }
        metricGroup.setDoubleFields(modelDoubleFieldsList);

        // Add string fields from attributes
        List<StringField> modelStringFieldsList = new LinkedList<>();
        for (StatsEngineStringMetricData statsStringField : statsEngineData.getStringMetricsDataList() ) {
            StringField stringField =  new StringField( statsStringField.getName(), statsStringField.getValue() );
            modelStringFieldsList.add( stringField );
        }
        metricGroup.setStringFields(modelStringFieldsList);

        return metricGroup;

    }

    /**
     *
     * Serialize model metric group object into JSON in a string
     *
     * @param engineData - object to serialize
     * @return JSON in a string
     */
    protected String modelMetricGroupToJsonInString(EngineData engineData) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(engineData);

            return jsonInString;
        }
        catch (Exception ex) {
            String msg = "modelMetricGroupToJsonInString() failed to build JSON";
            logger.error(msg, ex);
            throw new StatsEngineExceptions.ModelEngineDataToJsonFailureException(msg, ex);
        }

    }

}
