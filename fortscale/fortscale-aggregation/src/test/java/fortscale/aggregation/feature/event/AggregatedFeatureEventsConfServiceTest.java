package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.skyscreamer.jsonassert.JSONAssert;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by amira on 20/10/2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/bucketconf-context-test.xml" })
public class AggregatedFeatureEventsConfServiceTest {

    static final String BUCKET_CONF_AS_STRING1 = "{\"name\":\"bc1\",\"dataSources\":[\"ssh\"],\"contextFieldNames\":[\"field1\",\"field2\"],\"strategyName\":\"strategy1\",\"aggrFeatureConfs\":[{\"name\":\"aggr-feature-1\",\"featureNamesMap\":{\"param1\":[\"feature1\",\"feature2\"]},\"aggrFeatureFuncJson\":{\"type\":\"func1\"},\"allFeatureNames\":[\"feature2\",\"feature1\"],\"filter\":null}],\"expireAfterSeconds\":null,\"allFeatureNames\":[\"feature2\",\"feature1\"]}";
    static final String AGGR_FEATURE_EVENT_FUNCTION_AS_STRING1 = "{\"params\":{\"param1\":\"valueOfParam1\",\"param2\":\"valueOfParam2\",\"param3\":\"valueOfParam3\"},\"type\":\"type1\"}";
    static final String FEATURE_NAME_MAP_AS_STRING1 = "{functionArgument=[aggregatedFeatureName1, aggregatedFeatureName2, aggregatedFeatureName3]}";
    static final String FEATURE_NAMES_AS_STRING1 = "[aggregatedFeatureName1, aggregatedFeatureName3, aggregatedFeatureName2]";

    static final String BUCKET_CONF_AS_STRING2 = "{\"name\":\"bc2\",\"dataSources\":[\"vpn\"],\"contextFieldNames\":[\"field3\",\"field4\"],\"strategyName\":\"strategy1\",\"aggrFeatureConfs\":[{\"name\":\"aggr-feature-2\",\"featureNamesMap\":{\"param1\":[\"feature3\",\"feature4\"]},\"aggrFeatureFuncJson\":{\"type\":\"func2\"},\"allFeatureNames\":[\"feature4\",\"feature3\"],\"filter\":null}],\"expireAfterSeconds\":null,\"allFeatureNames\":[\"feature4\",\"feature3\"]}";
    static final String AGGR_FEATURE_EVENT_FUNCTION_AS_STRING2 = "{\"params\":{\"param1\":\"valueOfParam1\",\"param2\":\"valueOfParam2\",\"param3\":\"valueOfParam3\"},\"type\":\"type2\"}";
    static final String FEATURE_NAME_MAP_AS_STRING2 = "{functionArgument=[aggregatedFeatureName1, aggregatedFeatureName2, aggregatedFeatureName3]}";
    static final String FEATURE_NAMES_AS_STRING2 = "[aggregatedFeatureName1, aggregatedFeatureName3, aggregatedFeatureName2]";

    @Autowired
    AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Test
    public void getAggregatedFeatureEventConfTest() throws JSONException {
        AggregatedFeatureEventConf aggregatedFeatureEventConf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf("name1");
        assertAggregatedFeatureEventConf1(aggregatedFeatureEventConf);
        aggregatedFeatureEventConf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf("name2");
        assertAggregatedFeatureEventConf2(aggregatedFeatureEventConf);
    }

    private void assertAggregatedFeatureEventConf1(AggregatedFeatureEventConf aggregatedFeatureEventConf) throws JSONException {
        String bucketConfName = aggregatedFeatureEventConf.getBucketConfName();
        FeatureBucketConf featureBucketConf = aggregatedFeatureEventConf.getBucketConf();
        JSONObject aggrFeatureEventFunction = aggregatedFeatureEventConf.getAggregatedFeatureEventFunction();
        String name = aggregatedFeatureEventConf.getName();
        int bucketLeap = aggregatedFeatureEventConf.getBucketsLeap();
        boolean fireEventsAlsoForEmptyBucketTicks = aggregatedFeatureEventConf.getFireEventsAlsoForEmptyBucketTicks();
        Map<String, List<String>> featureNameMap = aggregatedFeatureEventConf.getAggregatedFeatureNamesMap();
        Set<String> featureNames = aggregatedFeatureEventConf.getAllAggregatedFeatureNames();
        String anomalyType = aggregatedFeatureEventConf.getAnomalyType();
        String evidencesFilterStrategy = aggregatedFeatureEventConf.getEvidencesFilterStrategy();
        int numberOfBuckets = aggregatedFeatureEventConf.getNumberOfBuckets();
        String outputBucketStrategy = aggregatedFeatureEventConf.getOutputBucketStrategy();
        String retentionStrategyName = aggregatedFeatureEventConf.getRetentionStrategyName();

        Assert.assertEquals("bc1", bucketConfName);
        Assert.assertEquals(BUCKET_CONF_AS_STRING1, featureBucketConf.toString());
        JSONAssert.assertEquals(AGGR_FEATURE_EVENT_FUNCTION_AS_STRING1, aggrFeatureEventFunction.toString(), false);
        Assert.assertEquals("name1", name);
        Assert.assertEquals(1, bucketLeap);
        Assert.assertEquals(false, fireEventsAlsoForEmptyBucketTicks);
        Assert.assertEquals(FEATURE_NAME_MAP_AS_STRING1, featureNameMap.toString());
        JSONAssert.assertEquals(FEATURE_NAMES_AS_STRING1, featureNames.toString(), false);
        Assert.assertEquals("number_of_successful_ssh_events_hourly", anomalyType);
        Assert.assertEquals("HIGHEST_SCORE", evidencesFilterStrategy);
        Assert.assertEquals(1, numberOfBuckets);
        Assert.assertNull(outputBucketStrategy);
        Assert.assertEquals("hourly_feature_retention_strategy", retentionStrategyName);
    }

    private void assertAggregatedFeatureEventConf2(AggregatedFeatureEventConf aggregatedFeatureEventConf) throws JSONException {
        String bucketConfName = aggregatedFeatureEventConf.getBucketConfName();
        FeatureBucketConf featureBucketConf = aggregatedFeatureEventConf.getBucketConf();
        JSONObject aggrFeatureEventFunction = aggregatedFeatureEventConf.getAggregatedFeatureEventFunction();
        String name = aggregatedFeatureEventConf.getName();
        int bucketLeap = aggregatedFeatureEventConf.getBucketsLeap();
        boolean fireEventsAlsoForEmptyBucketTicks = aggregatedFeatureEventConf.getFireEventsAlsoForEmptyBucketTicks();
        Map<String, List<String>> featureNameMap = aggregatedFeatureEventConf.getAggregatedFeatureNamesMap();
        Set<String> featureNames = aggregatedFeatureEventConf.getAllAggregatedFeatureNames();
        String anomalyType = aggregatedFeatureEventConf.getAnomalyType();
        String evidencesFilterStrategy = aggregatedFeatureEventConf.getEvidencesFilterStrategy();
        int numberOfBuckets = aggregatedFeatureEventConf.getNumberOfBuckets();
        String outputBucketStrategy = aggregatedFeatureEventConf.getOutputBucketStrategy();
        String retentionStrategyName = aggregatedFeatureEventConf.getRetentionStrategyName();

        Assert.assertEquals("bc2", bucketConfName);
        JSONAssert.assertEquals(BUCKET_CONF_AS_STRING2, featureBucketConf.toString(), false);
        JSONAssert.assertEquals(AGGR_FEATURE_EVENT_FUNCTION_AS_STRING2, aggrFeatureEventFunction.toString(), false);
        Assert.assertEquals("name2", name);
        Assert.assertEquals(2, bucketLeap);
        Assert.assertEquals(false, fireEventsAlsoForEmptyBucketTicks);
        JSONAssert.assertEquals(FEATURE_NAME_MAP_AS_STRING2, featureNameMap.toString(), false);
        JSONAssert.assertEquals(FEATURE_NAMES_AS_STRING2, featureNames.toString(), false);
        Assert.assertEquals("number_of_successful_ssh_events_daily", anomalyType);
        Assert.assertEquals("HIGHEST_SCORE_PER_VALUE", evidencesFilterStrategy);
        Assert.assertEquals(2, numberOfBuckets);
        Assert.assertNull(outputBucketStrategy);
        Assert.assertEquals("daily_feature_retention_strategy", retentionStrategyName);
    }
    @Test
    public void getAggregatedFeatureEventConfListTest() throws JSONException {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();

        Assert.assertEquals(2, aggregatedFeatureEventConfs.size());
        assertAggregatedFeatureEventConf1(aggregatedFeatureEventConfs.get(0));
        assertAggregatedFeatureEventConf2(aggregatedFeatureEventConfs.get(1));
    }

    @Test
    public void getAnomalyTypeTest() {
        Assert.assertEquals("number_of_successful_ssh_events_hourly", aggregatedFeatureEventsConfService.getAnomalyType("name1"));
        Assert.assertEquals("number_of_successful_ssh_events_daily", aggregatedFeatureEventsConfService.getAnomalyType("name2"));
    }

    @Test
    public void getEvidenceReadingStrategyTest() {
        Assert.assertEquals(AggrEventEvidenceFilteringStrategyEnum.HIGHEST_SCORE, aggregatedFeatureEventsConfService.getEvidenceReadingStrategy("name1"));
        Assert.assertEquals(AggrEventEvidenceFilteringStrategyEnum.HIGHEST_SCORE_PER_VALUE, aggregatedFeatureEventsConfService.getEvidenceReadingStrategy("name2"));
    }

}
