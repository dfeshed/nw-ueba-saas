package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.metrics.MaxContinuousModelRetrieverMetricsContainer;
import fortscale.ml.model.retriever.factories.AccumulatedAggregatedFeatureValueRetrieverFactory;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by maria_dorohin on 7/17/17.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class AccumulatedAggregatedFeatureValueRetrieverTest {
    @Autowired
    public FactoryService<AbstractDataRetriever> factoryService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Test
    public void test() {
        AccumulatedAggregatedFeatureValueRetrieverConf accumulatedAggregatedFeatureValueRetrieverConf = new AccumulatedAggregatedFeatureValueRetrieverConf(1, Collections.emptyList(), "test_conf_name");
        AggregatedFeatureEventConf aggregatedFeatureEventConf = mock(AggregatedFeatureEventConf.class);
        when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf("test_conf_name")).thenReturn(aggregatedFeatureEventConf);
        AbstractDataRetriever abstractDataRetriever = factoryService.getProduct(accumulatedAggregatedFeatureValueRetrieverConf);
        Assert.assertTrue(abstractDataRetriever instanceof AccumulatedAggregatedFeatureValueRetriever);
    }

    @Configuration
    public static class AccumulatedAggregatedFeatureValueRetrieverTestConfig {
        @MockBean
        private AggregationEventsAccumulationDataReader store;
        @MockBean
        private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
        @Autowired
        private AccumulatedAggregatedFeatureValueRetrieverFactory accumulatedAggregatedFeatureValueRetrieverFactory;
        @MockBean
        private MaxContinuousModelRetrieverMetricsContainer maxContinuousModelRetrieverMetricsContainer;

        @Bean
        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
            accumulatedAggregatedFeatureValueRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
            return dataRetrieverFactoryService;
        }

        @Bean
        public AccumulatedAggregatedFeatureValueRetrieverFactory accumulatedAggregatedFeatureValueRetrieverFactory() {
            return new AccumulatedAggregatedFeatureValueRetrieverFactory();
        }
    }
}
