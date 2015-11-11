package fortscale.ml.model;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.retriever.IDataRetriever;
import fortscale.ml.model.selector.EntitiesSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.time.TimestampUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

public class ModelBuilderManagerTest {
    @Mock
    ModelConf modelConf;
    @Mock
    private IModelBuildingScheduler scheduler;
    @Mock
    EntitiesSelector entitiesSelector;
    @Mock
    IDataRetriever dataRetriever;
    @Mock
    IModelBuilder modelBuilder;
    @Mock
    ModelStore modelStore;

    private ModelBuilderManager createProcessScenario(String[] entityIDs, Model[] entityModels, Object[] modelBuilderData, Boolean[] successes) {
        if (entityIDs != null) {
            // entity model scenario
            Mockito.when(modelConf.getEntitiesSelector()).thenReturn(entitiesSelector);
        } else {
            // global model scenario
            Mockito.when(modelConf.getEntitiesSelector()).thenReturn(null);
            entityIDs = new String[]{null};
        }
        Mockito.when(modelConf.getDataRetriever()).thenReturn(dataRetriever);
        Mockito.when(modelConf.getModelBuilder()).thenReturn(modelBuilder);
        Mockito.when(modelConf.getModelStore()).thenReturn(modelStore);

        Mockito.when(entitiesSelector.getEntities()).thenReturn(entityIDs);
        for (int i = 0; i < entityIDs.length; i++) {
            Mockito.when(dataRetriever.retrieve(entityIDs[i])).thenReturn(modelBuilderData[i]);
            Mockito.when(modelBuilder.build(modelBuilderData[i])).thenReturn(entityModels[i]);
            Mockito.when(modelStore.save(modelConf, entityIDs[i], entityModels[i])).thenReturn(successes[i]);
        }
        return new ModelBuilderManager(modelConf, scheduler);
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private void verifyModelManagerRegistered(ModelBuilderManager modelManager) {
        long expectedEpochtime = TimestampUtils.convertToSeconds(System.currentTimeMillis()) + modelConf.getBuildIntervalInSeconds();
        Mockito.verify(scheduler).register(Mockito.eq(modelManager), (long) AdditionalMatchers.eq((double) expectedEpochtime, 1));
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
        Mockito.when(modelConf.getBuildIntervalInSeconds()).thenReturn(60L);
        ModelBuilderManager modelManager = new ModelBuilderManager(modelConf, scheduler);
        verifyModelManagerRegistered(modelManager);
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
        String[] entityIDs = {"user1", "user2"};
        Model[] entityModels = {new Model() {}, new Model() {}};
        ModelBuilderManager modelManager = createProcessScenario(
                entityIDs,
                entityModels,
                new Object[]{new Object(), new Object()},
                new Boolean[]{true, true});
        modelManager.process(null);

        Mockito.verify(entitiesSelector).getEntities();
        for (int i = 0; i < entityIDs.length; i++) {
            Mockito.verify(modelStore).save(modelConf, entityIDs[i], entityModels[i]);
        }
        Mockito.verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldBuildAndStoreGlobalModel() {
        Model globalModel = new Model() {};
        ModelBuilderManager modelManager = createProcessScenario(
                null,
                new Model[]{globalModel},
                new Object[]{new Object()},
                new Boolean[]{true});
        modelManager.process(null);

        Mockito.verify(modelStore).save(modelConf, null, globalModel);
        Mockito.verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldRegisterItselfOnceFinishedProcessing() {
        ModelBuilderManager modelManager = createProcessScenario(
                null,
                new Model[]{new Model() {}},
                new Object[]{new Object()},
                new Boolean[]{true});
        Mockito.reset(scheduler);
        modelManager.process(null);
        verifyModelManagerRegistered(modelManager);
    }

    @Test
    public void shouldInformListenerOnModelBuildingStatus() {
        String modelConfName = "modelConfName";
        Mockito.when(modelConf.getName()).thenReturn(modelConfName);
        Boolean[] successes = {true, false};
        ModelBuilderManager modelManager = createProcessScenario(
                new String[]{"user1", "user2"},
                new Model[]{new Model() {}, new Model() {}},
                new Object[]{new Object(), new Object()},
                successes);
        IModelBuildingListener listener = Mockito.mock(IModelBuildingListener.class);
        modelManager.process(listener);

        for (boolean success: successes) {
            Mockito.verify(listener).modelBuildingStatus(modelConfName, null, success);
        }
        Mockito.verifyNoMoreInteractions(listener);
    }
}
