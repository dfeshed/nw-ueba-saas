package fortscale.aggregation.feature.event;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.BucketConfigurationServiceConfig;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import net.minidev.json.JSONObject;
import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

@RunWith(SpringJUnit4ClassRunner.class)
public class AggregatedFeatureEventsConfServiceTest {
    private static final String BUCKET_CONF_AS_STRING1 = "{\"name\":\"bc1\",\"adeEventTypes\":[\"ssh\"],\"contextFieldNames\":[\"field1\",\"field2\"],\"strategyName\":\"strategy1\",\"aggrFeatureConfs\":[{\"name\":\"aggr-feature-1\",\"featureNamesMap\":{\"param1\":[\"feature1\",\"feature2\"]},\"aggrFeatureFuncJson\":{\"type\":\"func1\"},\"allFeatureNames\":[\"feature2\",\"feature1\"],\"filter\":null}],\"allFeatureNames\":[\"feature2\",\"feature1\"]}";
    private static final String AGGR_FEATURE_EVENT_FUNCTION_AS_STRING1 = "{\"params\":{\"param1\":\"valueOfParam1\",\"param2\":\"valueOfParam2\",\"param3\":\"valueOfParam3\"},\"type\":\"type1\"}";
    private static final String FEATURE_NAME_MAP_AS_STRING1 = "{functionArgument=[aggregatedFeatureName1, aggregatedFeatureName2, aggregatedFeatureName3]}";
    private static final String FEATURE_NAMES_AS_STRING1 = "[aggregatedFeatureName1, aggregatedFeatureName3, aggregatedFeatureName2]";
    private static final String BUCKET_CONF_AS_STRING2 = "{\"name\":\"bc2\",\"adeEventTypes\":[\"vpn\"],\"contextFieldNames\":[\"field3\",\"field4\"],\"strategyName\":\"strategy1\",\"aggrFeatureConfs\":[{\"name\":\"aggr-feature-2\",\"featureNamesMap\":{\"param1\":[\"feature3\",\"feature4\"]},\"aggrFeatureFuncJson\":{\"type\":\"func2\"},\"allFeatureNames\":[\"feature4\",\"feature3\"],\"filter\":null}],\"allFeatureNames\":[\"feature4\",\"feature3\"]}";
    private static final String AGGR_FEATURE_EVENT_FUNCTION_AS_STRING2 = "{\"params\":{\"param1\":\"valueOfParam1\",\"param2\":\"valueOfParam2\",\"param3\":\"valueOfParam3\"},\"type\":\"type2\"}";
    private static final String FEATURE_NAMES_AS_STRING2 = "[aggregatedFeatureName1, aggregatedFeatureName3, aggregatedFeatureName2]";

    @Configuration
    @Import(BucketConfigurationServiceConfig.class)
    static class ContextConfiguration {
        @Autowired
        private BucketConfigurationService bucketConfigurationService;

        @Bean
        public AggregatedFeatureEventsConfService getAggregatedFeatureEventsConfService() {
            return new AggregatedFeatureEventsConfService(
                    "classpath:config/asl/aggregated_feature_events.json",
                    "classpath:fortscale/config/asl/aggregation_events/overriding/*.json",
                    null,
                    bucketConfigurationService);
        }

        @Bean
        public static TestPropertiesPlaceholderConfigurer AggregatedFeatureEventsConfServiceTestPropertiesPlaceholderConfigurer() {
            Properties properties = new Properties();
            properties.put("impala.table.fields.data.source", "data_source");
            properties.put("fortscale.aggregation.bucket.conf.json.file.name", "classpath:config/asl/buckets.json");
            properties.put("fortscale.aggregation.bucket.conf.json.overriding.files.path", "classpath:fortscale/config/asl/buckets/overriding/*.json");
            properties.put("fortscale.aggregation.bucket.conf.json.additional.files.path", "classpath:fortscale/config/asl/buckets/additional/*.json");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

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
        Map<String, List<String>> featureNameMap = aggregatedFeatureEventConf.getAggregatedFeatureNamesMap();
        Set<String> featureNames = aggregatedFeatureEventConf.getAllAggregatedFeatureNames();
        int numberOfBuckets = aggregatedFeatureEventConf.getNumberOfBuckets();

        Assert.assertEquals("bc1", bucketConfName);
        Assert.assertEquals(BUCKET_CONF_AS_STRING1, featureBucketConf.toString());
        JSONAssert.assertEquals(AGGR_FEATURE_EVENT_FUNCTION_AS_STRING1, aggrFeatureEventFunction.toString(), false);
        Assert.assertEquals("name1", name);
        Assert.assertEquals(1, bucketLeap);
        Assert.assertEquals(FEATURE_NAME_MAP_AS_STRING1, featureNameMap.toString());
        JSONAssert.assertEquals(FEATURE_NAMES_AS_STRING1, featureNames.toString(), false);
        Assert.assertEquals(1, numberOfBuckets);
    }

    private void assertAggregatedFeatureEventConf2(AggregatedFeatureEventConf aggregatedFeatureEventConf) throws JSONException {
        String bucketConfName = aggregatedFeatureEventConf.getBucketConfName();
        FeatureBucketConf featureBucketConf = aggregatedFeatureEventConf.getBucketConf();
        JSONObject aggrFeatureEventFunction = aggregatedFeatureEventConf.getAggregatedFeatureEventFunction();
        String name = aggregatedFeatureEventConf.getName();
        int bucketLeap = aggregatedFeatureEventConf.getBucketsLeap();
        Set<String> featureNames = aggregatedFeatureEventConf.getAllAggregatedFeatureNames();
        int numberOfBuckets = aggregatedFeatureEventConf.getNumberOfBuckets();

        Assert.assertEquals("bc2", bucketConfName);
        JSONAssert.assertEquals(BUCKET_CONF_AS_STRING2, featureBucketConf.toString(), false);
        JSONAssert.assertEquals(AGGR_FEATURE_EVENT_FUNCTION_AS_STRING2, aggrFeatureEventFunction.toString(), false);
        Assert.assertEquals("name2", name);
        Assert.assertEquals(2, bucketLeap);
        JSONAssert.assertEquals(FEATURE_NAMES_AS_STRING2, featureNames.toString(), false);
        Assert.assertEquals(2, numberOfBuckets);
    }

    @Test
    public void getAggregatedFeatureEventConfListTest() throws JSONException {
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();

        Assert.assertEquals(2, aggregatedFeatureEventConfs.size());
        assertAggregatedFeatureEventConf1(aggregatedFeatureEventConfs.get(0));
        assertAggregatedFeatureEventConf2(aggregatedFeatureEventConfs.get(1));
    }
}
