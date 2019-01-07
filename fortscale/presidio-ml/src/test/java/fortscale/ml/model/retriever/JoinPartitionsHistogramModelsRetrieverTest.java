package fortscale.ml.model.retriever;

import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.ml.model.*;
import fortscale.ml.model.metrics.MaxContinuousModelRetrieverMetricsContainer;
import fortscale.ml.model.retriever.factories.AccumulatedAggregatedFeatureValueRetrieverFactory;
import fortscale.ml.model.retriever.factories.JoinPartitionsHistogramModelsRetrieverFactory;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.factory.FactoryService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.store.accumulator.AggregationEventsAccumulationDataReader;

import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
public class JoinPartitionsHistogramModelsRetrieverTest {

    @Autowired
    public FactoryService<AbstractDataRetriever> factoryService;
    @Autowired
    ModelStore modelStore;
    @Autowired
    private AccumulatedAggregatedFeatureValueRetrieverFactory accumulatedAggregatedFeatureValueRetrieverFactory;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Test
    public void test() {
        String mainModelConfName = "multi_context_model";
        String secondaryModelConfName = "user_model";
        Instant endInstant = Instant.parse("2018-07-01T00:00:00Z");
        Date endDate = Date.from(endInstant);

        createMocks(endInstant, mainModelConfName, secondaryModelConfName);
        JoinPartitionsHistogramModelsRetrieverConf joinPartitionsHistogramModelsRetrieverConf = new JoinPartitionsHistogramModelsRetrieverConf(7776000, Collections.emptyList(), mainModelConfName, secondaryModelConfName);
        JoinPartitionsHistogramModelsRetriever retriever = (JoinPartitionsHistogramModelsRetriever) factoryService.getProduct(joinPartitionsHistogramModelsRetrieverConf);
        ModelBuilderData modelBuilderData = retriever.retrieve(null, endDate);
        assertResults(modelBuilderData);
    }


    /**
     * assert results
     *
     * @param modelBuilderData modelBuilderData
     */
    private void assertResults(ModelBuilderData modelBuilderData) {
        List<Model> results = (List<Model>) modelBuilderData.getData();
        for (Model res : results) {
            Assert.assertTrue(res instanceof ContinuousDataModel);
            ContinuousDataModel continuousDataModel = (ContinuousDataModel) res;
            Assert.assertTrue(continuousDataModel.getMean() > 0);
            Assert.assertTrue(continuousDataModel.getN() > 0);
            Assert.assertTrue(continuousDataModel.getSd() > 0);
        }
    }


    /**
     * create mocks
     * notice: single context models have higher resolution than multi context models
     *
     * @param endInstant             endInstant
     * @param mainModelConfName      mainModelConfName
     * @param secondaryModelConfName secondaryModelConfName
     */
    private void createMocks(Instant endInstant, String mainModelConfName, String secondaryModelConfName) {
        Instant startInstant = endInstant.minus(Duration.ofDays(2));
        long numOfPartitions = 5;
        Duration instantStep = Duration.ofHours(1);
        long resolutionInSeconds = 3600;

        //mock for modelConfService, mainModelConf and secondaryModelConf
        ModelConfService modelConfService = mock(ModelConfService.class);
        DynamicModelConfServiceContainer.setModelConfService(modelConfService);
        ModelConf mainModelConf = mock(ModelConf.class);
        ModelConf secondaryModelConf = mock(ModelConf.class);
        when(modelConfService.getModelConf(mainModelConfName)).thenReturn(mainModelConf);
        when(modelConfService.getModelConf(secondaryModelConfName)).thenReturn(secondaryModelConf);

        //mock for models of secondaryModelConf - single context model
        int numOfContexts = 3;
        String secondaryContextName = "user";
        List<String> secondaryContextIds = createSingleContextIds(secondaryContextName, numOfContexts);
        Collection<ModelDAO> modelDaoList = createSecondaryModels(startInstant, endInstant, numOfPartitions, instantStep, resolutionInSeconds * 3, secondaryContextIds);
        when(modelStore.getAllContextsModelDaosWithLatestEndTimeLte(secondaryModelConf, endInstant)).thenReturn(modelDaoList);


        //mock for main models - multi context models
        int pageSize = 2;
        int numOfContext1 = 2;
        int numOfContext2 = 3;
        Set<String> multiContextIds = createMultiContextIds(numOfContext1, numOfContext2);
        int totalAmountOfPages = numOfContext1 * numOfContext2 / pageSize;
        List<ModelDAO> models = createMainModels(startInstant, endInstant, multiContextIds,
                instantStep, resolutionInSeconds * 2, mainModelConf, modelStore, numOfPartitions, pageSize, totalAmountOfPages);
        when(modelStore.getDistinctNumOfContextIds(mainModelConf, endInstant)).thenReturn(new ArrayList<>(multiContextIds));
        when(modelStore.readRecords(mainModelConf, endInstant, multiContextIds, 0, JoinPartitionsHistogramModelsRetrieverConf.PRIOR_MODEL_PAGINATION_PAGE_SIZE)).thenReturn(models);

        //mocks for secondaryContextName
        AccumulatedAggregatedFeatureValueRetrieverConf dataRetrieverConf = mock(AccumulatedAggregatedFeatureValueRetrieverConf.class);
        when(secondaryModelConf.getDataRetrieverConf()).thenReturn(dataRetrieverConf);
        when(dataRetrieverConf.getFactoryName()).thenReturn(AccumulatedAggregatedFeatureValueRetrieverConf.ACCUMULATED_AGGREGATED_FEATURE_VALUE_RETRIEVER);
        AggregatedFeatureEventConf aggregatedFeatureEventConf = mock(AggregatedFeatureEventConf.class);
        when(aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf("test")).thenReturn(aggregatedFeatureEventConf);
        when(dataRetrieverConf.getAggregatedFeatureEventConfName()).thenReturn("test");
        FeatureBucketConf featureBucketConf = mock(FeatureBucketConf.class);
        when(aggregatedFeatureEventConf.getBucketConf()).thenReturn(featureBucketConf);
        when(featureBucketConf.getContextFieldNames()).thenReturn(Collections.singletonList(secondaryContextName));
    }


    /**
     * Create  List<ModelDAO> with multiContext
     * each model represent multiContext with new event every 2 hours between startInstant to endInstant
     *
     * @param startInstant        startInstant
     * @param endInstant          endInstant
     * @param multiContextIds     multiContextIds
     * @param instantStep         instantStep
     * @param resolutionInSeconds resolutionInSeconds
     * @param mainModelConf       mainModelConf
     * @param modelStore          modelStore
     * @param numOfPartitions     numOfPartitions
     * @param pageSize            pageSize
     * @param totalAmountOfPages  totalAmountOfPages
     * @return List<ModelDAO>
     */
    private List<ModelDAO> createMainModels(Instant startInstant, Instant endInstant, Set<String> multiContextIds,
                                            Duration instantStep, long resolutionInSeconds, ModelConf mainModelConf, ModelStore modelStore, long numOfPartitions, int pageSize, int totalAmountOfPages) {
        List<ModelDAO> modelDaoList = new ArrayList<>();
        for (String contextId : multiContextIds) {
            Map<Long, Double> instantToValue = new HashMap<>();
            Instant start = startInstant;
            while (start.isBefore(endInstant)) {
                instantToValue.put(start.getEpochSecond(), 1D);
                start = start.plus(Duration.ofHours(2));
            }

            Model model = new PartitionsDataModel(instantToValue, resolutionInSeconds, instantStep, numOfPartitions);
            ModelDAO modelDao = new ModelDAO("test_session", contextId, model, startInstant, endInstant,null);
            modelDaoList.add(modelDao);
        }
        ;
        return modelDaoList;
    }


    /**
     * Create user models with single context
     * each model represent user with new event every hour between startInstant to endInstant
     *
     * @param startInstant        startInstant
     * @param endInstant          endInstant
     * @param numOfPartitions     numOfPartitions
     * @param instantStep         instantStep
     * @param resolutionInSeconds resolutionInSeconds
     * @return Collection<ModelDAO>
     */
    private Collection<ModelDAO> createSecondaryModels(Instant startInstant, Instant endInstant, long numOfPartitions, Duration instantStep, long resolutionInSeconds, List<String> contextIds) {
        List<ModelDAO> modelDaoList = new ArrayList<>();

        for (String contextId : contextIds) {
            Map<Long, Double> instantToValue = new HashMap<>();
            Instant start = startInstant;
            while (start.isBefore(endInstant)) {
                instantToValue.put(start.getEpochSecond(), 1D);
                start = start.plus(Duration.ofHours(1));
            }
            Model model = new PartitionsDataModel(instantToValue, resolutionInSeconds, instantStep, numOfPartitions);
            ModelDAO modelDao = new ModelDAO("test_session", contextId, model, startInstant, endInstant, null);
            modelDaoList.add(modelDao);
        }
        return modelDaoList;
    }

    /**
     * @param numOfContexts numOfContexts
     * @return contextId list
     */
    private List<String> createSingleContextIds(String contextName, int numOfContexts) {
        String testContextId = contextName + "#testUser";
        List<String> contextIds = new ArrayList<>();
        for (int i = 1; i <= numOfContexts; i++) {
            contextIds.add(testContextId + i);
        }
        return contextIds;
    }


    private Set<String> createMultiContextIds(int numOfContext1, int numOfContext2) {
        String testContext1 = "user#testUser";
        String testContext2 = "#procrss#testProcess";
        Set<String> contextIds = new HashSet<>();
        for (int i = 1; i <= numOfContext1; i++) {
            for (int j = 1; j <= numOfContext2; j++) {
                contextIds.add(testContext1 + i + testContext2 + j);
            }
        }
        return contextIds;
    }


    @Configuration
    public static class JoinModelRetrieverTestConfig {

        @Autowired
        private JoinPartitionsHistogramModelsRetrieverFactory joinPartitionsHistogramModelsRetrieverFactory;

        @Autowired
        private AccumulatedAggregatedFeatureValueRetrieverFactory accumulatedAggregatedFeatureValueRetrieverFactory;

        @MockBean
        ModelStore modelStore;
        @MockBean
        private AggregationEventsAccumulationDataReader aggregationEventsAccumulationDataReader;
        @MockBean
        private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
        @MockBean
        private MaxContinuousModelRetrieverMetricsContainer maxContinuousModelRetrieverMetricsContainer;

        @Bean
        public FactoryService<AbstractDataRetriever> dataRetrieverFactoryService() {
            FactoryService<AbstractDataRetriever> dataRetrieverFactoryService = new FactoryService<>();
            joinPartitionsHistogramModelsRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
            accumulatedAggregatedFeatureValueRetrieverFactory.registerFactoryService(dataRetrieverFactoryService);
            return dataRetrieverFactoryService;
        }

        @Bean
        public JoinPartitionsHistogramModelsRetrieverFactory joinModelRetrieverFactory() {
            return new JoinPartitionsHistogramModelsRetrieverFactory();
        }

        @Bean
        public AccumulatedAggregatedFeatureValueRetrieverFactory accumulatedAggregatedFeatureValueRetrieverFactory() {
            return new AccumulatedAggregatedFeatureValueRetrieverFactory();
        }
    }
}
