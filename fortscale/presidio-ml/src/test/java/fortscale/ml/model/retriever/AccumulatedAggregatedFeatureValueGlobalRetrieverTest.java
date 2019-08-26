package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.metrics.AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer;
import fortscale.ml.model.retriever.factories.AccumulatedAggregatedFeatureValueGlobalRetrieverFactory;
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


@RunWith(SpringJUnit4ClassRunner.class)
public class AccumulatedAggregatedFeatureValueGlobalRetrieverTest {
    @Autowired
    public FactoryService<AbstractDataRetriever> factoryService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Test
    public void test() {
        AccumulatedAggregatedFeatureValueGlobalRetrieverConf accumulatedAggregatedFeatureValueGlobalRetrieverConf = new AccumulatedAggregatedFeatureValueGlobalRetrieverConf(1, Collections.emptyList(), "test_conf_name");
        AggregatedFeatureEventConf aggregatedFeatureEventConf = mock(AggregatedFeatureEventConf.class);
        when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf("test_conf_name")).thenReturn(aggregatedFeatureEventConf);
        AbstractDataRetriever abstractDataRetriever = factoryService.getProduct(accumulatedAggregatedFeatureValueGlobalRetrieverConf);
        Assert.assertTrue(abstractDataRetriever instanceof AccumulatedAggregatedFeatureValueGlobalRetriever);
    }

    @Configuration
    public static class AccumulatedAggregatedFeatureValueGlobalRetrieverTestConfig {
        @MockBean
        private AggregationEventsAccumulationDataReader store;
        @MockBean
        private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
        @Autowired
        private AccumulatedAggregatedFeatureValueGlobalRetrieverFactory accumulatedAggregatedFeatureValueGlobalRetrieverFactory;
        @MockBean
        private AccumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer accumulatedAggregatedFeatureValueGlobalRetrieverMetricsContainer;

        @Bean
        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
            accumulatedAggregatedFeatureValueGlobalRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
            return dataRetrieverFactoryService;
        }

        @Bean
        public AccumulatedAggregatedFeatureValueGlobalRetrieverFactory accumulatedAggregatedFeatureValueGlobalRetrieverFactory() {
            return new AccumulatedAggregatedFeatureValueGlobalRetrieverFactory();
        }
    }
}
