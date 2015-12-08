package fortscale.ml.model;

import fortscale.aggregation.feature.Feature;
import fortscale.aggregation.feature.bucket.FeatureBucket;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketsReaderService;
import fortscale.aggregation.feature.util.GenericHistogram;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.listener.ModelBuildingStatus;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.store.ModelStore;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class ModelBuilderManagerTest {
    private static final String DEFAULT_SESSION_ID = "testSessionId";
    private static ClassPathXmlApplicationContext testContextManager;

    private ModelConf modelConf;
    private ContextSelector contextSelector;
    private ModelStore modelStore;

    @BeforeClass
    public static void setUpClass() {
        testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/model_builder_manager_test_context.xml");
    }

    @Before
    public void setUp() {
        // Mock retriever conf
        ContextHistogramRetrieverConf retrieverConf = mock(ContextHistogramRetrieverConf.class);
        String featureName = "myFeature";
        when(retrieverConf.getFeatureName()).thenReturn(featureName);

        // Mock model conf
        modelConf = mock(ModelConf.class);
        when(modelConf.getDataRetrieverConf()).thenReturn(retrieverConf);

        // Mock selector
        contextSelector = mock(ContextSelector.class);

        // Create default feature bucket
        GenericHistogram histogram = new GenericHistogram();
        histogram.add(100.0, 1.0);
        Map<String, Feature> aggregatedFeatures = new HashMap<>();
        aggregatedFeatures.put(featureName, new Feature(featureName, histogram));
        FeatureBucket featureBucket = new FeatureBucket();
        featureBucket.setStartTime(1420070400);
        featureBucket.setEndTime(1420074000);
        featureBucket.setAggregatedFeatures(aggregatedFeatures);

        // Mock reader service
        FeatureBucketsReaderService readerService = testContextManager.getBean(FeatureBucketsReaderService.class);
        when(readerService.getFeatureBucketsByContextIdAndTimeRange(any(FeatureBucketConf.class), anyString(), anyLong(), anyLong()))
                .thenReturn(Arrays.asList(featureBucket));

        // Mock store
        modelStore = testContextManager.getBean(ModelStore.class);
        reset(modelStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutModelConf() {
        new ModelBuilderManager(null);
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
        Date currentEndTime = new Date(1420156800000L);
        Date previousEndTime = new Date(1420070400000L);
        String[] ids = {"user1", "user2"};
        Model[] models = {getDefaultModel(), getDefaultModel()};
        boolean[] successes = {true, true};

        ModelBuilderManager modelBuilderManager = createProcessScenario(previousEndTime, currentEndTime, ids, models, successes);
        modelBuilderManager.process(null, DEFAULT_SESSION_ID, previousEndTime, currentEndTime);

        verify(contextSelector, times(1)).getContexts(previousEndTime, currentEndTime);
        for (int i = 0; i < ids.length; i++) {
            verify(modelStore).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(ids[i]), eq(models[i]), eq(currentEndTime));
        }
        verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldBuildAndStoreGlobalModel() {
        Date currentEndTime = new Date(1420156800000L);
        Model[] models = {getDefaultModel()};
        boolean[] successes = {true};

        ModelBuilderManager modelBuilderManager = createProcessScenario(null, currentEndTime, null, models, successes);
        modelBuilderManager.process(null, DEFAULT_SESSION_ID, null, currentEndTime);

        verify(modelStore).save(eq(modelConf), eq(DEFAULT_SESSION_ID), isNull(String.class), eq(models[0]), eq(currentEndTime));
        verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldInformListenerOnModelBuildingStatus() {
        String modelConfName = "testModelConf";
        when(modelConf.getName()).thenReturn(modelConfName);

        Date currentEndTime = new Date(1420156800000L);
        Date previousEndTime = new Date(1420070400000L);
        String[] entityIds = {"user1", "user2"};
        Model[] models = {getDefaultModel(), getDefaultModel()};
        boolean[] successes = {true, false};

        IModelBuildingListener listener = mock(IModelBuildingListener.class);
        ModelBuilderManager modelManager = createProcessScenario(previousEndTime, currentEndTime, entityIds, models, successes);
        modelManager.process(listener, DEFAULT_SESSION_ID, previousEndTime, currentEndTime);

        for (int i = 0; i < entityIds.length; i++) {
            ModelBuildingStatus status = successes[i] ? ModelBuildingStatus.SUCCESS : ModelBuildingStatus.STORE_FAILURE;
            verify(listener).modelBuildingStatus(eq(modelConfName), eq(DEFAULT_SESSION_ID), eq(entityIds[i]), eq(currentEndTime), eq(status));
        }
        verifyNoMoreInteractions(listener);
    }

    private Model getDefaultModel() {
        ContinuousDataModel model = new ContinuousDataModel();
        model.setParameters(1, 100, 0);
        return model;
    }

    private void mockBuild(String id, Model model, Date endTime, boolean success) {
        if (!success) {
            doThrow(Exception.class).when(modelStore).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(id), eq(model), eq(endTime));
        }
    }

    private ModelBuilderManager createProcessScenario(
            Date previousEndTime, Date currentEndTime, String[] ids, Model[] models, boolean[] successes) {

        if (ids != null) {
            when(contextSelector.getContexts(previousEndTime, currentEndTime)).thenReturn(Arrays.asList(ids));
            for (int i = 0; i < ids.length; i++) {
                mockBuild(ids[i], models[i], currentEndTime, successes[i]);
            }
        } else {
            mockBuild(null, models[0], currentEndTime, successes[0]);
            contextSelector = null;
        }

        ModelBuilderManager modelBuilderManager = new ModelBuilderManager(modelConf);
        modelBuilderManager.setContextSelector(contextSelector);
        return modelBuilderManager;
    }
}
