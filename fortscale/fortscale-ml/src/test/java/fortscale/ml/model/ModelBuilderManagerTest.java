package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.IModelBuilderConf;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.listener.ModelBuildingStatus;
import fortscale.ml.model.retriever.AbstractDataRetriever;
import fortscale.ml.model.retriever.AbstractDataRetrieverConf;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.ContextSelectorConf;
import fortscale.ml.model.store.ModelStore;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Arrays;
import java.util.Date;

import static org.mockito.Mockito.*;

public class ModelBuilderManagerTest {
    private static ClassPathXmlApplicationContext testContextManager;
    private static final String DEFAULT_SESSION_ID = "testSessionId";

    private ModelConf modelConf;
    private ContextSelector selector;
    private AbstractDataRetriever retriever;
    private IModelBuilder builder;
    private ModelStore store;
    private ModelService modelService;

    @BeforeClass
    public static void setUpClass() {
        testContextManager = new ClassPathXmlApplicationContext(
                "classpath*:META-INF/spring/model_builder_manager_test_context.xml");
    }

    @Before
    public void setUp() {
        modelConf = mock(ModelConf.class);
        selector = mock(ContextSelector.class);
        retriever = mock(AbstractDataRetriever.class);
        builder = mock(IModelBuilder.class);

        // ModelStore is auto wired in ModelBuilderManager
        store = testContextManager.getBean(ModelStore.class);
        reset(store);

        modelService = mock(ModelService.class);
        when(modelService.getContextSelector(any(ContextSelectorConf.class))).thenReturn(selector);
        when(modelService.getDataRetriever(any(AbstractDataRetrieverConf.class))).thenReturn(retriever);
        when(modelService.getModelBuilder(any(IModelBuilderConf.class))).thenReturn(builder);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutModelConf() {
        new ModelBuilderManager(null, modelService);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutModelService() {
        new ModelBuilderManager(modelConf, null);
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
        Date previousEndTime = new Date(1420070400000L);
        Date currentEndTime = new Date(1420156800000L);
        String[] ids = {"user1", "user2"};
        Model[] models = {mock(Model.class), mock(Model.class)};
        boolean[] successes = {true, true};

        ModelBuilderManager manager = createProcessScenario(previousEndTime, currentEndTime, ids, models, successes);
        manager.process(null, DEFAULT_SESSION_ID, previousEndTime, currentEndTime);

        verify(selector).getContexts(eq(previousEndTime), eq(currentEndTime));
        verify(builder, times(ids.length)).build(any());
        for (int i = 0; i < ids.length; i++) {
            verify(retriever).retrieve(eq(ids[i]), eq(currentEndTime));
            verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(ids[i]), eq(models[i]), eq(currentEndTime));
        }

        verifyNoMoreInteractions(selector, retriever, builder, store);
    }

    @Test
    public void shouldBuildAndStoreGlobalModel() {
        Date currentEndTime = new Date(1420156800000L);
        Model[] models = {mock(Model.class)};
        boolean[] successes = {true};

        ModelBuilderManager manager = createProcessScenario(null, currentEndTime, null, models, successes);
        manager.process(null, DEFAULT_SESSION_ID, null, currentEndTime);

        verify(retriever).retrieve(isNull(String.class), eq(currentEndTime));
        verify(builder).build(any());
        verify(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), isNull(String.class), eq(models[0]), eq(currentEndTime));

        verifyNoMoreInteractions(selector, retriever, builder, store);
    }

    @Test
    public void shouldInformListenerOnModelBuildingStatus() {
        String modelConfName = "testModelConf";
        when(modelConf.getName()).thenReturn(modelConfName);

        Date previousEndTime = new Date(1420070400000L);
        Date currentEndTime = new Date(1420156800000L);
        String[] ids = {"user1", "user2"};
        Model[] models = {mock(Model.class), mock(Model.class)};
        boolean[] successes = {true, false};

        IModelBuildingListener listener = mock(IModelBuildingListener.class);
        ModelBuilderManager manager = createProcessScenario(previousEndTime, currentEndTime, ids, models, successes);
        manager.process(listener, DEFAULT_SESSION_ID, previousEndTime, currentEndTime);

        for (int i = 0; i < ids.length; i++) {
            ModelBuildingStatus status = successes[i] ? ModelBuildingStatus.SUCCESS : ModelBuildingStatus.STORE_FAILURE;
            verify(listener).modelBuildingStatus(
                    eq(modelConfName), eq(DEFAULT_SESSION_ID), eq(ids[i]), eq(currentEndTime), eq(status));
        }

        verifyNoMoreInteractions(listener);
    }

    private void mockBuild(String id, Date endTime, Model model, boolean success) {
        Object data = mock(Object.class);
        when(retriever.retrieve(eq(id), eq(endTime))).thenReturn(data);
        when(builder.build(eq(data))).thenReturn(model);

        if (!success) {
            doThrow(Exception.class).when(store).save(eq(modelConf), eq(DEFAULT_SESSION_ID), eq(id), eq(model), eq(endTime));
        }
    }

    private ModelBuilderManager createProcessScenario(
            Date previousEndTime, Date currentEndTime, String[] ids, Model[] models, boolean[] successes) {

        if (ids != null) {
            when(modelConf.getContextSelectorConf()).thenReturn(mock(ContextSelectorConf.class));
            when(selector.getContexts(eq(previousEndTime), eq(currentEndTime))).thenReturn(Arrays.asList(ids));

            for (int i = 0; i < ids.length; i++) {
                mockBuild(ids[i], currentEndTime, models[i], successes[i]);
            }
        } else {
            mockBuild(null, currentEndTime, models[0], successes[0]);
        }

        return new ModelBuilderManager(modelConf, modelService);
    }
}
