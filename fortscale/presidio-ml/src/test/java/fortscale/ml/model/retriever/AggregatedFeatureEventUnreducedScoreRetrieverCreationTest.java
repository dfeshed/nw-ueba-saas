//package fortscale.ml.model.retriever;
//
//import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
//import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
//import fortscale.aggregation.feature.event.store.AggregatedFeatureEventsReaderService;
//import fortscale.ml.model.retriever.factories.AggregatedFeatureEventUnreducedScoreRetrieverFactory;
//import fortscale.utils.factory.FactoryService;
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
//import java.util.Collections;
//import java.util.List;
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@Ignore
//public class AggregatedFeatureEventUnreducedScoreRetrieverCreationTest {
//
//
//    @Autowired
//    public FactoryService<AbstractDataRetriever> factoryService;
//
//    @Autowired
//    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
//
//    @Test
//    public void test() {
//
//        AggregatedFeatureEventUnreducedScoreRetrieverConf aggregatedFeatureEventUnreducedScoreRetrieverConf =
//                new AggregatedFeatureEventUnreducedScoreRetrieverConf(Collections.emptyList(),
//                        "test_aggr_feature_event_to_conf_name",
//                        "test_score_name_to_calibrate",
//                        1,
//                        1);
//
//        AggregatedFeatureEventConf aggregatedFeatureEventConf = mock(AggregatedFeatureEventConf.class);
//        when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf("test_aggr_feature_event_to_conf_name")).thenReturn(aggregatedFeatureEventConf);
//
//        AbstractDataRetriever abstractDataRetriever = factoryService.getProduct(aggregatedFeatureEventUnreducedScoreRetrieverConf);
//
//        Assert.assertTrue(abstractDataRetriever instanceof AggregatedFeatureEventUnreducedScoreRetriever);
//    }
//
//    @Configuration
//    public static class AggregatedFeatureEventUnreducedScoreRetrieverTestConfig {
//
//
//        @Autowired
//        private AggregatedFeatureEventUnreducedScoreRetrieverFactory aggregatedFeatureEventUnreducedScoreRetrieverFactory;
//
//        @MockBean
//        private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
//        @MockBean
//        private AggregatedFeatureEventsReaderService aggregatedFeatureEventsReaderService;
//
//        @Bean
//        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
//            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
//            aggregatedFeatureEventUnreducedScoreRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
//            return dataRetrieverFactoryService;
//        }
//
//        @Bean
//        public AggregatedFeatureEventUnreducedScoreRetrieverFactory aggregatedFeatureEventUnreducedScoreRetrieverFactory() {
//            return new AggregatedFeatureEventUnreducedScoreRetrieverFactory();
//        }
//
//    }
//}
