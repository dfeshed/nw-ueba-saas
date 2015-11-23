package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.ml.model.retriever.ContextHistogramRetrieverConf;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.store.ModelStore;
import junitparams.JUnitParamsRunner;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
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
        modelConf = mock(ModelConf.class);
        contextSelector = mock(ContextSelector.class);
        modelStore = testContextManager.getBean(ModelStore.class);

        when(modelConf.getDataRetrieverConf()).thenReturn(mock(ContextHistogramRetrieverConf.class));
        reset(modelStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutModelConf() {
        new ModelBuilderManager(null);
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
        DateTime currentEndTime = DateTime.now();
        DateTime previousEndTime = currentEndTime.minusDays(1);
        String[] ids = {"user1", "user2"};
        Model[] models = {new ContinuousDataModel(), new ContinuousDataModel()};
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
        DateTime currentEndTime = DateTime.now();
        Model[] models = {new ContinuousDataModel()};
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

        DateTime currentEndTime = DateTime.now();
        DateTime previousEndTime = currentEndTime.minusDays(1);
        String[] entityIds = {"user1", "user2"};
        Model[] models = {new ContinuousDataModel(), new ContinuousDataModel()};
        boolean[] successes = {true, false};

        IModelBuildingListener listener = mock(IModelBuildingListener.class);
        ModelBuilderManager modelManager = createProcessScenario(previousEndTime, currentEndTime, entityIds, models, successes);
        modelManager.process(listener, DEFAULT_SESSION_ID, previousEndTime, currentEndTime);

        for (int i = 0; i < entityIds.length; i++) {
            verify(listener).modelBuildingStatus(eq(modelConfName), eq(entityIds[i]), eq(currentEndTime), eq(successes[i]));
        }
        verifyNoMoreInteractions(listener);
    }

    private void mockBuild(String id, Model model, DateTime endTime, boolean success) {
        if (!success) {
            doThrow(Exception.class).when(modelStore).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(id), eq(model), eq(endTime));
        }
    }

    private ModelBuilderManager createProcessScenario(
            DateTime previousEndTime, DateTime currentEndTime, String[] ids, Model[] models, boolean[] successes) {

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
