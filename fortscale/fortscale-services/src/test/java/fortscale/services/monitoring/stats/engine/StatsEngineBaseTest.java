package fortscale.services.monitoring.stats.engine;

import fortscale.services.monitoring.stats.StatsMetricsGroupAttributes;
import fortscale.services.monitoring.stats.StatsMetricsTag;
import fortscale.services.monitoring.stats.impl.engine.testing.StatsTestingEngine;
import org.junit.Assert;

import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by gaashh on 4/17/16.
 */
public class StatsEngineBaseTest {

/*
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

     */
    
    protected List<StatsEngineMetricsGroupData> createdStatsMetricsGroupsList() {

        List<StatsEngineMetricsGroupData> metricsGroupList = new LinkedList<>();

        StatsEngineMetricsGroupData metricGroup;

        metricGroup = createdStatsMetricsGroup("AAA", 100 * 1000);
        metricsGroupList.add(metricGroup);

        metricGroup = createdStatsMetricsGroup("BBB", 200 * 1000);
        metricsGroupList.add(metricGroup);

        metricGroup = createdStatsMetricsGroup("CCC", 300 * 1000);
        metricsGroupList.add(metricGroup);

        return metricsGroupList;
    }

    protected StatsEngineMetricsGroupData createdStatsMetricsGroup(String prefix, long bigValue) {

        StatsEngineMetricsGroupData metricGroup = new StatsEngineMetricsGroupData();

        // Common
        metricGroup.setGroupName(prefix + "-GRP");
        metricGroup.setInstrumentedClass(StatsEngineBaseTest.class);
        metricGroup.setMeasurementEpoch(bigValue + 1122);

        // Tags
        List<StatsMetricsTag> tags = new LinkedList<>();
        for (long i = 1 ; i < 3 ; i++) {
            StatsMetricsTag tag = new StatsMetricsTag(String.format("TAG-%s-%d", prefix, i),
                                                      String.format("%s-%s-%s-%d", prefix, prefix, prefix, i) );
            tags.add(tag);
        }
        metricGroup.setMetricsTags(tags);
        
        
        // Longs 
        for (long i = 1 ; i <= 4 ; i++ ) {
            StatsEngineLongMetricData longData = new StatsEngineLongMetricData(String.format("long%s%d", prefix,i), 
                                                                               bigValue + 100 + i) ;
            metricGroup.addLongMetricData(longData);
        }

        // Double
        for (long i = 1 ; i <= 3 ; i++ ) {
            StatsEngineDoubleMetricData doubleData = new StatsEngineDoubleMetricData(String.format("double%s%d", prefix, i),
                                                                                     1.0 * (bigValue + 200 + i) + 0.11*i );
            metricGroup.addDoubleMetricData(doubleData);
        }

        // String
        for (long i = 1 ; i <= 3 ; i++ ) {
            StatsEngineStringMetricData stringData = new StatsEngineStringMetricData(
                    String.format("string%s%d", prefix,i),
                    String.format("%s-%s-%d", prefix, prefix, bigValue + 300 + i) );

            metricGroup.addStringMetricData(stringData);
        }

        return metricGroup;
    }

    @Test
    public void testModelMetricGroupToJsonInString() throws Exception {

        List<StatsEngineMetricsGroupData> metricGroupData = createdStatsMetricsGroupsList();

        StatsTestingEngine engine = new StatsTestingEngine();

        String result = engine.statsEngineDataToJsonInString(metricGroupData);

        String expected = "{\"version\":100,\"metricGroups\":[{\"groupName\":\"AAA-GRP\",\"instrumentedClass\":\"fortscale.services.monitoring.stats.engine.StatsEngineBaseTest\",\"measurementEpoch\":101122,\"tags\":[{\"name\":\"TAG-AAA-1\",\"value\":\"AAA-AAA-AAA-1\"},{\"name\":\"TAG-AAA-2\",\"value\":\"AAA-AAA-AAA-2\"}],\"longFields\":[{\"name\":\"longAAA1\",\"value\":100101},{\"name\":\"longAAA2\",\"value\":100102},{\"name\":\"longAAA3\",\"value\":100103},{\"name\":\"longAAA4\",\"value\":100104}],\"doubleFields\":[{\"name\":\"doubleAAA1\",\"value\":100201.11},{\"name\":\"doubleAAA2\",\"value\":100202.22},{\"name\":\"doubleAAA3\",\"value\":100203.33}],\"stringFields\":[{\"name\":\"stringAAA1\",\"value\":\"AAA-AAA-100301\"},{\"name\":\"stringAAA2\",\"value\":\"AAA-AAA-100302\"},{\"name\":\"stringAAA3\",\"value\":\"AAA-AAA-100303\"}]},{\"groupName\":\"BBB-GRP\",\"instrumentedClass\":\"fortscale.services.monitoring.stats.engine.StatsEngineBaseTest\",\"measurementEpoch\":201122,\"tags\":[{\"name\":\"TAG-BBB-1\",\"value\":\"BBB-BBB-BBB-1\"},{\"name\":\"TAG-BBB-2\",\"value\":\"BBB-BBB-BBB-2\"}],\"longFields\":[{\"name\":\"longBBB1\",\"value\":200101},{\"name\":\"longBBB2\",\"value\":200102},{\"name\":\"longBBB3\",\"value\":200103},{\"name\":\"longBBB4\",\"value\":200104}],\"doubleFields\":[{\"name\":\"doubleBBB1\",\"value\":200201.11},{\"name\":\"doubleBBB2\",\"value\":200202.22},{\"name\":\"doubleBBB3\",\"value\":200203.33}],\"stringFields\":[{\"name\":\"stringBBB1\",\"value\":\"BBB-BBB-200301\"},{\"name\":\"stringBBB2\",\"value\":\"BBB-BBB-200302\"},{\"name\":\"stringBBB3\",\"value\":\"BBB-BBB-200303\"}]},{\"groupName\":\"CCC-GRP\",\"instrumentedClass\":\"fortscale.services.monitoring.stats.engine.StatsEngineBaseTest\",\"measurementEpoch\":301122,\"tags\":[{\"name\":\"TAG-CCC-1\",\"value\":\"CCC-CCC-CCC-1\"},{\"name\":\"TAG-CCC-2\",\"value\":\"CCC-CCC-CCC-2\"}],\"longFields\":[{\"name\":\"longCCC1\",\"value\":300101},{\"name\":\"longCCC2\",\"value\":300102},{\"name\":\"longCCC3\",\"value\":300103},{\"name\":\"longCCC4\",\"value\":300104}],\"doubleFields\":[{\"name\":\"doubleCCC1\",\"value\":300201.11},{\"name\":\"doubleCCC2\",\"value\":300202.22},{\"name\":\"doubleCCC3\",\"value\":300203.33}],\"stringFields\":[{\"name\":\"stringCCC1\",\"value\":\"CCC-CCC-300301\"},{\"name\":\"stringCCC2\",\"value\":\"CCC-CCC-300302\"},{\"name\":\"stringCCC3\",\"value\":\"CCC-CCC-300303\"}]}]}";

        Assert.assertEquals(expected,result);
    }
}