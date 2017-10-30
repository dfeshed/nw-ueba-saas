package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.ml.model.retriever.factories.ContextHistogramRetrieverFactory;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static fortscale.ml.model.retriever.ContextHistogramRetrieverTestUtil.generateHourlyFeatureBuckets;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class ContextHistogramRetrieverTest {

    @Autowired
    public FactoryService<AbstractDataRetriever> factoryService;

    @Autowired
    private BucketConfigurationService bucketConfigurationService;


    @Test
    public void test() {
        ContextHistogramRetrieverConf contextHistogramRetrieverConf =
                new ContextHistogramRetrieverConf(1,
                        Collections.emptyList(),
                        "test_conf_name", "test_feature_name", 86400);

        FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
        when(bucketConfigurationService.getBucketConf("test_conf_name")).thenReturn(featureBucketConf);

        AggregatedFeatureConf aggregatedFeatureConf = mock(AggregatedFeatureConf.class);
        List<AggregatedFeatureConf> aggregatedFeatureConfs = new ArrayList<>();
        aggregatedFeatureConfs.add(aggregatedFeatureConf);


        when(featureBucketConf.getAggrFeatureConfs()).thenReturn(aggregatedFeatureConfs);

        when(aggregatedFeatureConf.getName()).thenReturn("test_feature_name");

        ContextHistogramRetriever retriever = (ContextHistogramRetriever)factoryService.getProduct(contextHistogramRetrieverConf);
        Instant fromInstant = Instant.parse("2017-01-01T00:00:00Z");
        Instant toInstant = fromInstant.plus(25,ChronoUnit.DAYS);

        LinkedList<FeatureBucket> featureBuckets = generateHourlyFeatureBuckets(fromInstant, toInstant);
        long numOfPartitionsOfFeatureBuckets = retriever.calcNumOfPartitionsOfFeatureBuckets(featureBuckets);
        Assert.assertEquals(25,numOfPartitionsOfFeatureBuckets);
    }

    @Configuration
    public static class ContextHistogramRetrieverTestConfig {

        @Autowired
        private ContextHistogramRetrieverFactory contextHistogramRetrieverFactory;

        @MockBean
        @Qualifier("modelBucketConfigService")
        private BucketConfigurationService bucketConfigurationService;
        @MockBean
        private FeatureBucketReader featureBucketReader;

        @Bean
        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
            contextHistogramRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
            return dataRetrieverFactoryService;
        }

        @Bean
        public ContextHistogramRetrieverFactory contextHistogramRetrieverFactory() {
            return new ContextHistogramRetrieverFactory();
        }

    }
}
