package fortscale.ml.model;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.retriever.ModelBuilderDataRetriever;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.selector.FeatureBucketContextSelectorConf;

public class ModelBuilderManagerTest {
    @Test
    public void shouldCalcNextRunTimeAsCurrentTimePlusBuildInterval() {
        ModelConf modelConf = Mockito.mock(ModelConf.class);
        long buildIntervalInSeconds = 123;
        Mockito.when(modelConf.getBuildIntervalInSeconds()).thenReturn(buildIntervalInSeconds);

        ModelBuilderManager modelManager = new ModelBuilderManager(modelConf);
        long currentTimeInSeconds = 100;
        modelManager.calcNextRunTime(currentTimeInSeconds);

        Assert.assertEquals(currentTimeInSeconds + buildIntervalInSeconds, modelManager.getNextRunTimeInSeconds());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfConstructedWithoutModelConf() {
        new ModelBuilderManager(null);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailIfGetNextRunTimeIsCalledBeforeCalc() {
        ModelConf modelConf = Mockito.mock(ModelConf.class);

        ModelBuilderManager modelManager = new ModelBuilderManager(modelConf);
        modelManager.getNextRunTimeInSeconds();
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
        ModelConf modelConf = Mockito.mock(ModelConf.class);
        ContextSelector entitiesSelector = Mockito.mock(ContextSelector.class);
        Mockito.when(modelConf.getContextSelectorConf()).thenReturn(new FeatureBucketContextSelectorConf("featurebucketconfname1"));
        ModelBuilderDataRetriever modelBuilderDataRetriever = Mockito.mock(ModelBuilderDataRetriever.class);
        Mockito.when(modelConf.getModelBuilderDataRetriever()).thenReturn(modelBuilderDataRetriever);
        IModelBuilder modelBuilder = Mockito.mock(IModelBuilder.class);
        Mockito.when(modelConf.getModelBuilder()).thenReturn(modelBuilder);
        ModelStore modelStore = Mockito.mock(ModelStore.class);
        Mockito.when(modelConf.getModelStore()).thenReturn(modelStore);

        String[] entityIDs = {"user1", "user2"};
        ModelBuilderData[] modelBuilderDatas = {new ModelBuilderData() {}, new ModelBuilderData() {}};
        Model[] entityModels = {new Model(), new Model()};
        Mockito.when(entitiesSelector.getContexts(0L,0L)).thenReturn(Arrays.asList(entityIDs));
        for (int i = 0; i < entityIDs.length; i++) {
            Mockito.when(modelBuilderDataRetriever.retrieve(entityIDs[i])).thenReturn(modelBuilderDatas[i]);
            Mockito.when(modelBuilder.build(modelBuilderDatas[i])).thenReturn(entityModels[i]);
        }

        ModelBuilderManager modelManager = new ModelBuilderManager(modelConf);
        modelManager.process();

        Mockito.verify(entitiesSelector).getContexts(0L,0L);
        for (int i = 0; i < entityIDs.length; i++) {
            Mockito.verify(modelStore).save(modelConf, entityIDs[i], entityModels[i]);
        }
        Mockito.verifyNoMoreInteractions(modelStore);
    }

    @Test
    public void shouldBuildAndStoreGlobalModel() {
        ModelConf modelConf = Mockito.mock(ModelConf.class);
        Mockito.when(modelConf.getContextSelectorConf()).thenReturn(null);
        ModelBuilderDataRetriever modelBuilderDataRetriever = Mockito.mock(ModelBuilderDataRetriever.class);
        Mockito.when(modelConf.getModelBuilderDataRetriever()).thenReturn(modelBuilderDataRetriever);
        IModelBuilder modelBuilder = Mockito.mock(IModelBuilder.class);
        Mockito.when(modelConf.getModelBuilder()).thenReturn(modelBuilder);
        ModelStore modelStore = Mockito.mock(ModelStore.class);
        Mockito.when(modelConf.getModelStore()).thenReturn(modelStore);

        ModelBuilderData modelBuilderData = new ModelBuilderData() {};
        Model globalModel = new Model();
        Mockito.when(modelBuilderDataRetriever.retrieve(null)).thenReturn(modelBuilderData);
        Mockito.when(modelBuilder.build(modelBuilderData)).thenReturn(globalModel);

        ModelBuilderManager modelManager = new ModelBuilderManager(modelConf);
        modelManager.process();

        Mockito.verify(modelStore).save(modelConf, null, globalModel);
        Mockito.verifyNoMoreInteractions(modelStore);
    }
}
