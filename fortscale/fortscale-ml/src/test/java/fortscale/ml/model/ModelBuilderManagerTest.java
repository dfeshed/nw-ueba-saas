package fortscale.ml.model;

import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.prevalance.field.ContinuousDataModel;
import fortscale.ml.model.retriever.EntityHistogramRetrieverConf;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.store.ModelStore;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;

import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class ModelBuilderManagerTest {
    private static ClassPathXmlApplicationContext testContextManager;

    private ModelConf modelConf;
    private IModelBuildingScheduler scheduler;
    private ContextSelector contextSelector;
    private ModelStore modelStore;

    @BeforeClass
    public static void setUpClass() {
        testContextManager = new ClassPathXmlApplicationContext("classpath*:META-INF/spring/model_builder_manager_test_context.xml");
    }

    @Before
    public void setUp() {
        modelConf = mock(ModelConf.class);
        scheduler = mock(IModelBuildingScheduler.class);
        contextSelector = mock(ContextSelector.class);
        modelStore = testContextManager.getBean(ModelStore.class);

        when(modelConf.getDataRetrieverConf()).thenReturn(mock(EntityHistogramRetrieverConf.class));
        reset(modelStore);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutModelConf() {
        new ModelBuilderManager(null, scheduler);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutScheduler() {
        new ModelBuilderManager(modelConf, null);
    }

    @Test
    public void shouldRegisterItselfInsideCtor() {
        ModelBuilderManager modelBuilderManager = new ModelBuilderManager(modelConf, scheduler);
        verify(scheduler, times(1)).register(eq(modelBuilderManager), anyLong());
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
        String[] ids = {"user1", "user2"};
        Model[] models = {new ContinuousDataModel(), new ContinuousDataModel()};
        boolean[] successes = {true, true};
        ModelBuilderManager modelBuilderManager = createProcessScenario(ids, models, successes);

        long sessionId = 1234;
        modelBuilderManager.process(null, sessionId);

        verify(contextSelector, times(1)).getContexts(0L, 0L);
        for (int i = 0; i < ids.length; i++) {
            verify(modelStore).save(eq(modelConf), eq(ids[i]), eq(models[i]), eq(sessionId));
        }
        verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldBuildAndStoreGlobalModel() {
        Model[] models = {new ContinuousDataModel()};
        boolean[] successes = {true};
        ModelBuilderManager modelBuilderManager = createProcessScenario(null, models, successes);

        long sessionId = 1234;
        modelBuilderManager.process(null, sessionId);

        verify(modelStore).save(eq(modelConf), isNull(String.class), eq(models[0]), eq(sessionId));
        verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldRegisterItselfOnceFinishedProcessing() {
        Model[] models = {new ContinuousDataModel()};
        boolean[] successes = {true};
        ModelBuilderManager modelBuilderManager = createProcessScenario(null, models, successes);

        modelBuilderManager.process(null, 1234);
        verify(scheduler, times(2)).register(eq(modelBuilderManager), anyLong());
    }

    @Test
    public void shouldInformListenerOnModelBuildingStatus() {
        String modelConfName = "testModelConf";
        when(modelConf.getName()).thenReturn(modelConfName);

        String[] entityIds = {"user1", "user2"};
        Model[] models = {new ContinuousDataModel(), new ContinuousDataModel()};
        boolean[] successes = {true, false};
        ModelBuilderManager modelManager = createProcessScenario(entityIds, models, successes);

        IModelBuildingListener listener = mock(IModelBuildingListener.class);
        modelManager.process(listener, 1234);

        for (int i = 0; i < entityIds.length; i++) {
            verify(listener).modelBuildingStatus(eq(modelConfName), eq(entityIds[i]), eq(successes[i]));
        }
        verifyNoMoreInteractions(listener);
    }

    private void mockBuild(String id, Model model, boolean success) {
        if (!success) {
            doThrow(Exception.class).when(modelStore).save(eq(modelConf), eq(id), eq(model), anyLong());
        }
    }

    private ModelBuilderManager createProcessScenario(String[] ids, Model[] models, boolean[] successes) {
        if (ids != null) {
            when(contextSelector.getContexts(0L, 0L)).thenReturn(Arrays.asList(ids));
            for (int i = 0; i < ids.length; i++) {
                mockBuild(ids[i], models[i], successes[i]);
            }
        } else {
            mockBuild(null, models[0], successes[0]);
            contextSelector = null;
        }

        ModelBuilderManager modelBuilderManager = new ModelBuilderManager(modelConf, scheduler);
        modelBuilderManager.setContextSelector(contextSelector);
        return modelBuilderManager;
    }
}
