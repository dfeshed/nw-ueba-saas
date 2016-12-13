package fortscale.aggregation.feature.bucket;

import fortscale.aggregation.feature.functions.AggrFeatureHistogramFunc;
import fortscale.aggregation.util.MongoDbUtilServiceConfig;
import fortscale.domain.core.dao.MongoDbRepositoryUtil;
import fortscale.utils.mongodb.config.SpringMongoConfiguration;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by barak_schuster on 12/12/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class FeatureBucketsMongoStoreTest {
    @Configuration
    @Import({SpringMongoConfiguration.class,
            NullStatsServiceConfig.class,
            MongoDbUtilServiceConfig.class
    })
    @ComponentScan(basePackageClasses = MongoDbRepositoryUtil.class,includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MongoDbRepositoryUtil.class))
    public static class springConfig {

        @Bean
        public FeatureBucketsMongoStore featureBucketsMongoStore()
        {
            return new FeatureBucketsMongoStore();
        }
        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("mongo.host.name", "localhost");
            properties.put("mongo.host.port","27017");
            properties.put("mongo.db.name","test");
            properties.put("mongo.map.dot.replacement","#dot#");
            properties.put("mongo.map.dollar.replacement","#dlr#");
            properties.put("mongo.db.user", "");
            properties.put("mongo.db.password", "");
            properties.put("fortscale.store.collection.backup.prefix","a_");

            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

    @Autowired
    private FeatureBucketsMongoStore featureBucketsMongoStore;

    @Autowired
    private MongoTemplate mongoTemplate;


    @Test
    public void should_handle_large_document_failure() throws Exception {
        AggregatedFeatureConf aggregatedFeatureConf = createAggrFeatureConf(10);
        FeatureBucketConf featureBucketConf = new FeatureBucketConf("name", Collections.singletonList("dataSource"),Collections.singletonList("contextFieldName"),"strategyName",Collections.singletonList(aggregatedFeatureConf));
        FeatureBucket featureBucket = getLargeFeatureBucket();

        // clean collection if exists
        String collectionName = featureBucketsMongoStore.getCollectionName(featureBucketConf);
        if(mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }

        featureBucketsMongoStore.insertFeatureBuckets(featureBucketConf,Collections.singletonList(featureBucket));
        FeatureBucketsStoreMetrics metrics = featureBucketsMongoStore.getMetrics(featureBucketConf);
        Assert.assertTrue(metrics.documentTooLarge.longValue()>0);
    }

    /**
     * generagates feature bucket larger that 16MB which is the current limitiation on BSON size
     * @return
     */
    private FeatureBucket getLargeFeatureBucket() {
        FeatureBucket featureBucket = new FeatureBucket();
        featureBucket.setBucketId("bucketId");
        Map<String, String> contextFieldNameToValueMap = new HashMap<>();
        for (int i=1; i<20000;i++)
        {
            String someLongString = StringUtils.repeat(String.format("someLongString%d",i), 30);
            contextFieldNameToValueMap.put(someLongString,someLongString);
        }
        featureBucket.setContextFieldNameToValueMap(contextFieldNameToValueMap);
        return featureBucket;
    }

    private AggregatedFeatureConf createAggrFeatureConf(int num) {
        List<String> featureNames = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            featureNames.add(String.format("feature%d", i));
        }
        Map<String, List<String>> featureNamesMap = new HashMap<>();
        featureNamesMap.put(AggrFeatureHistogramFunc.GROUP_BY_FIELD_NAME, featureNames);
        return new AggregatedFeatureConf("MyAggrFeature", featureNamesMap, new JSONObject());
    }

}