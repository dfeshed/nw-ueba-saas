package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
import fortscale.ml.model.retriever.factories.AggregatedFeatureValueRetrieverFactory;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class AggregatedFeatureValueRetrieverTest {

    @Autowired
    public FactoryService<AbstractDataRetriever> factoryService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Test
    public void test() {

        AggregatedFeatureValueRetrieverConf aggregatedFeatureValueRetrieverConf =
                new AggregatedFeatureValueRetrieverConf(1,
                        Collections.emptyList(),
                        "test_conf_name");

        AggregatedFeatureEventConf aggregatedFeatureEventConf = mock(AggregatedFeatureEventConf.class);
        when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf("test_conf_name")).thenReturn(aggregatedFeatureEventConf);

        AbstractDataRetriever abstractDataRetriever = factoryService.getProduct(aggregatedFeatureValueRetrieverConf);

        Assert.assertTrue(abstractDataRetriever instanceof AbstractAggregatedFeatureValueRetriever);
    }

    @Configuration
    public static class AggregatedFeatureValueRetrieverTestConfig {


        @Autowired
        private AggregatedFeatureValueRetrieverFactory aggregatedFeatureValueRetrieverFactory;

        @MockBean
        private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
        @MockBean
        private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;

        @Bean
        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
            aggregatedFeatureValueRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
            return dataRetrieverFactoryService;
        }

        @Bean
        public AggregatedFeatureValueRetrieverFactory aggregatedFeatureValueRetrieverFactory() {
            return new AggregatedFeatureValueRetrieverFactory();
        }

    }
}
