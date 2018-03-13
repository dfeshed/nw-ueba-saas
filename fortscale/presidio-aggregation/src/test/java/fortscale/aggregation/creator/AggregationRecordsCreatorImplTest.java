package fortscale.aggregation.creator;

import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.common.feature.AggrFeatureValue;
import fortscale.common.feature.Feature;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * Created by barak_schuster on 7/10/17.
 */

@RunWith(SpringRunner.class)
@ContextConfiguration()
public class AggregationRecordsCreatorImplTest {
    @Autowired
    private AggregationRecordsCreator aggregationRecordsCreator;

    @Test
    public void shouldReturnEmptyAggregationsForZeroFeatureBuckets() throws Exception {
        List<FeatureBucket> featureBuckets = Collections.emptyList();
        List<AdeAggregationRecord> aggregations = aggregationRecordsCreator.createAggregationRecords(featureBuckets);
        Assert.assertTrue(aggregations.isEmpty());
        aggregations = aggregationRecordsCreator.createAggregationRecords(null);
        Assert.assertTrue(aggregations.isEmpty());
    }

    @Test
    public void createAggregations() throws Exception {
        List<AdeAggregationRecord> aggregations = aggregationRecordsCreator.createAggregationRecords(generateFeatureBuckets());
        Assert.assertTrue(aggregations.size() > 0);
    }

    private List<FeatureBucket> generateFeatureBuckets() {
        List<FeatureBucket> featureBuckets = new LinkedList<>();
        Instant startTime = Instant.EPOCH;
        Instant endTime = startTime.plus(1, ChronoUnit.HOURS);
        FeatureBucket featureBucket = new FeatureBucket();
        featureBucket.setStartTime(startTime);
        featureBucket.setEndTime(endTime);
        Map<String, Double> value = new HashMap<>();
        value.put("server_app_1", 14.0);
        AggrFeatureValue aggrFeatureValue = new AggrFeatureValue(value, 1L);

        Feature aggrFeature = new Feature("normalized_src_machine_to_highest_score_map", aggrFeatureValue);

        HashMap<String, Feature> aggregatedFeatures = new HashMap<>();
        aggregatedFeatures.put("normalized_src_machine_to_highest_score_map", aggrFeature);
        featureBucket.setAggregatedFeatures(aggregatedFeatures);
        featureBucket.setFeatureBucketConfName("normalized_src_machine_to_highest_score_map_normalized_username_dlpfile_hourly");
        Map<String, String> contextFieldNameToValueMap = new HashMap<>();

        contextFieldNameToValueMap.put("normalized_username", "bestUserInTheWorld");
        featureBucket.setContextFieldNameToValueMap(contextFieldNameToValueMap);
        featureBuckets.add(featureBucket);
        return featureBuckets;
    }

    @Configuration
    @Import({
            AggregationRecordsCreatorConfig.class
    })
    public static class springConfig {

        @Bean
        public static TestPropertiesPlaceholderConfigurer aggregationCreatorTestProperties() {
            Properties properties = new Properties();
            properties.put("fortscale.aggregation.bucket.conf.json.file.name", "classpath:fortscale/config/asl/feature-buckets/score_aggregation_feature_buckets.json");
            properties.put("fortscale.aggregation.feature.event.conf.json.file.name", "classpath:fortscale/config/asl/aggregated-features/score_aggregation_features.json");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }

    }
}
