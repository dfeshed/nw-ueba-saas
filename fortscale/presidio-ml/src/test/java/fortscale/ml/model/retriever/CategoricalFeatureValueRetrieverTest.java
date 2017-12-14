package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.*;
import fortscale.common.feature.CategoricalFeatureValue;
import fortscale.ml.model.ModelBuilderData;
import fortscale.ml.model.metrics.CategoryRarityModelRetrieverMetricsContainer;
import fortscale.ml.model.retriever.factories.CategoricalFeatureValueRetrieverFactory;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.time.TimeRange;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static fortscale.ml.model.ModelBuilderData.NoDataReason.NO_DATA_IN_DATABASE;
import static fortscale.ml.model.retriever.ContextHistogramRetrieverTestUtil.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by barak_schuster on 10/16/17.
 */
@RunWith(SpringRunner.class)
public class CategoricalFeatureValueRetrieverTest {

    static final int AMOUNT_OF_DAYS_TO_RETRIEVE = 28;
    private static final int EXPECTED_HISTOGRAM_VALUE = 57;

    @Autowired
    private FeatureBucketReader featureBucketReader;
    @Autowired
    private FactoryService<AbstractDataRetriever> dataRetrieverFactoryService;

    static CategoricalFeatureValueRetrieverConf categoricalFeatureValueRetrieverConf;
    static CategoricalFeatureValueRetriever retriever;

    @Test
    public void retrieveEmptyData() throws Exception {
        when(featureBucketReader.getFeatureBuckets(eq(FEATURE_BUCKET_CONF_NAME),eq(CONTEXTID_WITH_NO_DATA),any())).thenReturn(new LinkedList<>());
        ModelBuilderData retrieve = retriever.retrieve(CONTEXTID_WITH_NO_DATA, new Date());
        Assert.assertEquals(null,retrieve.getData());
        Assert.assertEquals(NO_DATA_IN_DATABASE,retrieve.getNoDataReason());
    }

    @Test
    public void retrieveData() throws Exception {
        Instant fromInstant = Instant.parse("2017-01-01T00:00:00Z");
        Date from = Date.from(fromInstant);
        Instant toInstant = fromInstant.plus(Duration.ofDays(AMOUNT_OF_DAYS_TO_RETRIEVE));
        Date to = Date.from(toInstant);
        LinkedList<FeatureBucket> featureBuckets = generateHourlyFeatureBuckets(fromInstant,toInstant);
        when(featureBucketReader.getFeatureBuckets(FEATURE_BUCKET_CONF_NAME,CONTEXT_ID_WITH_DATA,new TimeRange(from,to))).thenReturn(featureBuckets);
        ModelBuilderData builderData = retriever.retrieve(CONTEXT_ID_WITH_DATA, to);
        Assert.assertTrue(builderData.dataExists());
        Assert.assertNull(builderData.getNoDataReason());

        CategoricalFeatureValue builderDataData = (CategoricalFeatureValue) builderData.getData();
        Assert.assertNotNull(builderDataData);
        Assert.assertEquals((3*24*AMOUNT_OF_DAYS_TO_RETRIEVE),builderDataData.getN());
        Assert.assertEquals(FixedDurationStrategy.HOURLY,builderDataData.getStrategy());
    }



    @Configuration
    public static class CategoricalFeatureValueRetrieverTestConfig {

        @Autowired
        private CategoricalFeatureValueRetrieverFactory contextHistogramRetrieverFactory;

        @MockBean
        @Qualifier("modelBucketConfigService")
        private BucketConfigurationService bucketConfigurationService;
        @MockBean
        private FeatureBucketReader featureBucketReader;
        @MockBean
        private CategoryRarityModelRetrieverMetricsContainer categoryRarityMetricsContainer;

        @Bean
        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
            contextHistogramRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
            retriever = (CategoricalFeatureValueRetriever) dataRetrieverFactoryService.getProduct(categoricalFeatureValueRetrieverConf);
            return dataRetrieverFactoryService;
        }

        @Bean
        public CategoricalFeatureValueRetrieverFactory categoricalFeatureValueRetrieverFactory() {
            confsSetup();

            return new CategoricalFeatureValueRetrieverFactory();
        }

        private void confsSetup() {
            FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
            when(featureBucketConf.getName()).thenReturn(FEATURE_BUCKET_CONF_NAME);
            when(featureBucketConf.getStrategyName()).thenReturn(FixedDurationStrategy.HOURLY.toStrategyName());
            when(bucketConfigurationService.getBucketConf(FEATURE_BUCKET_CONF_NAME)).thenReturn(featureBucketConf);

            AggregatedFeatureConf aggregatedFeatureConf = mock(AggregatedFeatureConf.class);
            List<AggregatedFeatureConf> aggregatedFeatureConfs = new ArrayList<>();
            aggregatedFeatureConfs.add(aggregatedFeatureConf);

            when(featureBucketConf.getAggrFeatureConfs()).thenReturn(aggregatedFeatureConfs);

            when(aggregatedFeatureConf.getName()).thenReturn(TEST_FEATURE_NAME);
            categoricalFeatureValueRetrieverConf = new CategoricalFeatureValueRetrieverConf(Duration.ofDays(AMOUNT_OF_DAYS_TO_RETRIEVE).getSeconds(),
                    Collections.emptyList(),
                    FEATURE_BUCKET_CONF_NAME, TEST_FEATURE_NAME);
        }

    }
}