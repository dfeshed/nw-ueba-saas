package fortscale.utils.monitoring.stats.engine;

import fortscale.utils.monitoring.stats.StatsMetricsTag;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * A helper for engine tesing
 * 
 * Created by gaashh on 5/2/16.
 */
public class StatsEngineTestingUtils {

    static final long ABOUT_YEAR_2001_EPOCH_TIME = 1000000000;

    static final protected String EXPECTED_METRICS_GROUP_LIST_JSON_STRING1 =
            "{\"version\":100,\"metricGroups\":[{\"groupName\":\"AAA-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000101122,\"tags\":[{\"name\":\"TAG-AAA-1\",\"value\":\"AAA-AAA-AAA-1\"},{\"name\":\"TAG-AAA-2\",\"value\":\"AAA-AAA-AAA-2\"}],\"longFields\":[{\"name\":\"longAAA1\",\"value\":100101},{\"name\":\"longAAA2\",\"value\":100102},{\"name\":\"longAAA3\",\"value\":100103},{\"name\":\"longAAA4\",\"value\":100104}],\"doubleFields\":[{\"name\":\"doubleAAA1\",\"value\":100201.11},{\"name\":\"doubleAAA2\",\"value\":100202.22},{\"name\":\"doubleAAA3\",\"value\":100203.33}],\"stringFields\":[{\"name\":\"stringAAA1\",\"value\":\"AAA-AAA-100301\"},{\"name\":\"stringAAA2\",\"value\":\"AAA-AAA-100302\"},{\"name\":\"stringAAA3\",\"value\":\"AAA-AAA-100303\"}]},{\"groupName\":\"BBB-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000201122,\"tags\":[{\"name\":\"TAG-BBB-1\",\"value\":\"BBB-BBB-BBB-1\"},{\"name\":\"TAG-BBB-2\",\"value\":\"BBB-BBB-BBB-2\"}],\"longFields\":[{\"name\":\"longBBB1\",\"value\":200101},{\"name\":\"longBBB2\",\"value\":200102},{\"name\":\"longBBB3\",\"value\":200103},{\"name\":\"longBBB4\",\"value\":200104}],\"doubleFields\":[{\"name\":\"doubleBBB1\",\"value\":200201.11},{\"name\":\"doubleBBB2\",\"value\":200202.22},{\"name\":\"doubleBBB3\",\"value\":200203.33}],\"stringFields\":[{\"name\":\"stringBBB1\",\"value\":\"BBB-BBB-200301\"},{\"name\":\"stringBBB2\",\"value\":\"BBB-BBB-200302\"},{\"name\":\"stringBBB3\",\"value\":\"BBB-BBB-200303\"}]},{\"groupName\":\"CCC-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000301122,\"tags\":[{\"name\":\"TAG-CCC-1\",\"value\":\"CCC-CCC-CCC-1\"},{\"name\":\"TAG-CCC-2\",\"value\":\"CCC-CCC-CCC-2\"}],\"longFields\":[{\"name\":\"longCCC1\",\"value\":300101},{\"name\":\"longCCC2\",\"value\":300102},{\"name\":\"longCCC3\",\"value\":300103},{\"name\":\"longCCC4\",\"value\":300104}],\"doubleFields\":[{\"name\":\"doubleCCC1\",\"value\":300201.11},{\"name\":\"doubleCCC2\",\"value\":300202.22},{\"name\":\"doubleCCC3\",\"value\":300203.33}],\"stringFields\":[{\"name\":\"stringCCC1\",\"value\":\"CCC-CCC-300301\"},{\"name\":\"stringCCC2\",\"value\":\"CCC-CCC-300302\"},{\"name\":\"stringCCC3\",\"value\":\"CCC-CCC-300303\"}]}]}";

    static final protected String EXPECTED_METRICS_GROUP_LIST_JSON_STRING2 =
            "{\"version\":100,\"metricGroups\":[{\"groupName\":\"AAA-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000201122,\"tags\":[{\"name\":\"TAG-AAA-1\",\"value\":\"AAA-AAA-AAA-1\"},{\"name\":\"TAG-AAA-2\",\"value\":\"AAA-AAA-AAA-2\"}],\"longFields\":[{\"name\":\"longAAA1\",\"value\":200101},{\"name\":\"longAAA2\",\"value\":200102},{\"name\":\"longAAA3\",\"value\":200103},{\"name\":\"longAAA4\",\"value\":200104}],\"doubleFields\":[{\"name\":\"doubleAAA1\",\"value\":200201.11},{\"name\":\"doubleAAA2\",\"value\":200202.22},{\"name\":\"doubleAAA3\",\"value\":200203.33}],\"stringFields\":[{\"name\":\"stringAAA1\",\"value\":\"AAA-AAA-200301\"},{\"name\":\"stringAAA2\",\"value\":\"AAA-AAA-200302\"},{\"name\":\"stringAAA3\",\"value\":\"AAA-AAA-200303\"}]},{\"groupName\":\"BBB-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000401122,\"tags\":[{\"name\":\"TAG-BBB-1\",\"value\":\"BBB-BBB-BBB-1\"},{\"name\":\"TAG-BBB-2\",\"value\":\"BBB-BBB-BBB-2\"}],\"longFields\":[{\"name\":\"longBBB1\",\"value\":400101},{\"name\":\"longBBB2\",\"value\":400102},{\"name\":\"longBBB3\",\"value\":400103},{\"name\":\"longBBB4\",\"value\":400104}],\"doubleFields\":[{\"name\":\"doubleBBB1\",\"value\":400201.11},{\"name\":\"doubleBBB2\",\"value\":400202.22},{\"name\":\"doubleBBB3\",\"value\":400203.33}],\"stringFields\":[{\"name\":\"stringBBB1\",\"value\":\"BBB-BBB-400301\"},{\"name\":\"stringBBB2\",\"value\":\"BBB-BBB-400302\"},{\"name\":\"stringBBB3\",\"value\":\"BBB-BBB-400303\"}]},{\"groupName\":\"CCC-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000601122,\"tags\":[{\"name\":\"TAG-CCC-1\",\"value\":\"CCC-CCC-CCC-1\"},{\"name\":\"TAG-CCC-2\",\"value\":\"CCC-CCC-CCC-2\"}],\"longFields\":[{\"name\":\"longCCC1\",\"value\":600101},{\"name\":\"longCCC2\",\"value\":600102},{\"name\":\"longCCC3\",\"value\":600103},{\"name\":\"longCCC4\",\"value\":600104}],\"doubleFields\":[{\"name\":\"doubleCCC1\",\"value\":600201.11},{\"name\":\"doubleCCC2\",\"value\":600202.22},{\"name\":\"doubleCCC3\",\"value\":600203.33}],\"stringFields\":[{\"name\":\"stringCCC1\",\"value\":\"CCC-CCC-600301\"},{\"name\":\"stringCCC2\",\"value\":\"CCC-CCC-600302\"},{\"name\":\"stringCCC3\",\"value\":\"CCC-CCC-600303\"}]}]}";

    static final protected String EXPECTED_METRICS_GROUP_LIST_JSON_STRING3 =
            "{\"version\":100,\"metricGroups\":[{\"groupName\":\"AAA-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000301122,\"tags\":[{\"name\":\"TAG-AAA-1\",\"value\":\"AAA-AAA-AAA-1\"},{\"name\":\"TAG-AAA-2\",\"value\":\"AAA-AAA-AAA-2\"}],\"longFields\":[{\"name\":\"longAAA1\",\"value\":300101},{\"name\":\"longAAA2\",\"value\":300102},{\"name\":\"longAAA3\",\"value\":300103},{\"name\":\"longAAA4\",\"value\":300104}],\"doubleFields\":[{\"name\":\"doubleAAA1\",\"value\":300201.11},{\"name\":\"doubleAAA2\",\"value\":300202.22},{\"name\":\"doubleAAA3\",\"value\":300203.33}],\"stringFields\":[{\"name\":\"stringAAA1\",\"value\":\"AAA-AAA-300301\"},{\"name\":\"stringAAA2\",\"value\":\"AAA-AAA-300302\"},{\"name\":\"stringAAA3\",\"value\":\"AAA-AAA-300303\"}]},{\"groupName\":\"BBB-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000601122,\"tags\":[{\"name\":\"TAG-BBB-1\",\"value\":\"BBB-BBB-BBB-1\"},{\"name\":\"TAG-BBB-2\",\"value\":\"BBB-BBB-BBB-2\"}],\"longFields\":[{\"name\":\"longBBB1\",\"value\":600101},{\"name\":\"longBBB2\",\"value\":600102},{\"name\":\"longBBB3\",\"value\":600103},{\"name\":\"longBBB4\",\"value\":600104}],\"doubleFields\":[{\"name\":\"doubleBBB1\",\"value\":600201.11},{\"name\":\"doubleBBB2\",\"value\":600202.22},{\"name\":\"doubleBBB3\",\"value\":600203.33}],\"stringFields\":[{\"name\":\"stringBBB1\",\"value\":\"BBB-BBB-600301\"},{\"name\":\"stringBBB2\",\"value\":\"BBB-BBB-600302\"},{\"name\":\"stringBBB3\",\"value\":\"BBB-BBB-600303\"}]},{\"groupName\":\"CCC-GRP\",\"instrumentedClass\":\"fortscale.utils.monitoring.stats.engine.StatsEngineTestingUtils\",\"measurementEpoch\":1000901122,\"tags\":[{\"name\":\"TAG-CCC-1\",\"value\":\"CCC-CCC-CCC-1\"},{\"name\":\"TAG-CCC-2\",\"value\":\"CCC-CCC-CCC-2\"}],\"longFields\":[{\"name\":\"longCCC1\",\"value\":900101},{\"name\":\"longCCC2\",\"value\":900102},{\"name\":\"longCCC3\",\"value\":900103},{\"name\":\"longCCC4\",\"value\":900104}],\"doubleFields\":[{\"name\":\"doubleCCC1\",\"value\":900201.11},{\"name\":\"doubleCCC2\",\"value\":900202.22},{\"name\":\"doubleCCC3\",\"value\":900203.33}],\"stringFields\":[{\"name\":\"stringCCC1\",\"value\":\"CCC-CCC-900301\"},{\"name\":\"stringCCC2\",\"value\":\"CCC-CCC-900302\"},{\"name\":\"stringCCC3\",\"value\":\"CCC-CCC-900303\"}]}]}";
    static final protected String[] EXPECTED_METRICS_GROUP_LIST_JSON_STRING_ARRAY = {
            null,
            EXPECTED_METRICS_GROUP_LIST_JSON_STRING1,
            EXPECTED_METRICS_GROUP_LIST_JSON_STRING2,
            EXPECTED_METRICS_GROUP_LIST_JSON_STRING3
    };
    
    static public String getExpectedMetricsGroupListJsonString(long setIndex) {
        
        return EXPECTED_METRICS_GROUP_LIST_JSON_STRING_ARRAY[(int)setIndex];
    }

    static public List<StatsEngineMetricsGroupData> createdStatsMetricsGroupsList(long setIndex) {

        List<StatsEngineMetricsGroupData> metricsGroupList = new LinkedList<>();

        StatsEngineMetricsGroupData metricGroup;

        metricGroup = createdStatsMetricsGroup("AAA", setIndex * 100 * 1000);
        metricsGroupList.add(metricGroup);

        metricGroup = createdStatsMetricsGroup("BBB", setIndex * 200 * 1000);
        metricsGroupList.add(metricGroup);

        metricGroup = createdStatsMetricsGroup("CCC", setIndex * 300 * 1000);
        metricsGroupList.add(metricGroup);

        return metricsGroupList;
    }

    static protected StatsEngineMetricsGroupData createdStatsMetricsGroup(String prefix, long bigValue) {

        StatsEngineMetricsGroupData metricGroup = new StatsEngineMetricsGroupData();

        // Common
        metricGroup.setGroupName(prefix + "-GRP");
        metricGroup.setInstrumentedClass(StatsEngineTestingUtils.class);
        metricGroup.setMeasurementEpoch(ABOUT_YEAR_2001_EPOCH_TIME + bigValue + 1122);

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




}
