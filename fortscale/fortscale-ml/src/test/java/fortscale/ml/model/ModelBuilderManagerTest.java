package fortscale.ml.model;


import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.listener.IModelBuildingListener;
import fortscale.ml.model.retriever.IDataRetriever;
import fortscale.ml.model.selector.ContextSelector;
import fortscale.ml.model.store.ModelStore;
import fortscale.utils.time.TimestampUtils;

public class ModelBuilderManagerTest {
    @Mock
    ModelConf modelConf;
    @Mock
    private IModelBuildingScheduler scheduler;
    @Mock
    ContextSelector entitiesSelector;
    @Mock
    IDataRetriever dataRetriever;
    @Mock
    IModelBuilder modelBuilder;
    @Mock
    ModelStore modelStore;
    @Mock
    BucketConfigurationService bucketConfigurationService;
    @Mock
    FeatureBucketConf featureBucketConf;

    private ModelBuilderManager createProcessScenario(String[] entityIDs, Model[] entityModels, Object[] modelBuilderData, Boolean[] successes) {
        Mockito.when(modelConf.getDataRetriever()).thenReturn(dataRetriever);
        Mockito.when(modelConf.getModelBuilder()).thenReturn(modelBuilder);
        Mockito.when(modelConf.getModelStore()).thenReturn(modelStore);

        if(entityIDs!=null){
	        Mockito.when(entitiesSelector.getContexts(0L,0L)).thenReturn(Arrays.asList(entityIDs));
	        for (int i = 0; i < entityIDs.length; i++) {
	        	mockBuild(entityIDs[i], modelBuilderData[i], entityModels[i], successes[i]);
	        }
        } else{
        	mockBuild(null, modelBuilderData[0], entityModels[0], successes[0]);
        	entitiesSelector = null;
        }
        
       
        ModelBuilderManager ret = new ModelBuilderManager(modelConf, scheduler);
        ret.setContextsSelector(entitiesSelector);
        return ret;
    }
    
    private void mockBuild(String contextId, Object modelBuilderData, Model entityModel, Boolean success){
    	Mockito.when(dataRetriever.retrieve(contextId)).thenReturn(modelBuilderData);
        Mockito.when(modelBuilder.build(modelBuilderData)).thenReturn(entityModel);
        if (!success) {
            Mockito.doThrow(Exception.class).when(modelStore).save(
                    Mockito.eq(modelConf),
                    Mockito.eq(contextId),
                    Mockito.eq(entityModel),
                    Mockito.anyLong());
        }
    }
    
//    private void mockSelector(){
//    	String bucketName = "bucketName1";
//        Mockito.when(modelConf.getContextSelectorConf()).thenReturn(new FeatureBucketContextSelectorConf(bucketName));
//        Mockito.when(bucketConfigurationService.getBucketConf(bucketName)).thenReturn(featureBucketConf);
//    }

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
        modelManager.setContextsSelector(entitiesSelector);
        verifyModelManagerRegistered(modelManager);
    }

    @Test
    public void shouldBuildAndStoreModelsForAllSelectedEntities() {
    	long sessionId = 1234;
    	String[] entityIDs = {"user1", "user2"};
        Model[] entityModels = {new Model() {}, new Model() {}};
        
        ModelBuilderManager modelManager = createProcessScenario(
                entityIDs,
                entityModels,
                new Object[]{new Object(), new Object()},
                new Boolean[]{true, true});        
        
        modelManager.process(null, sessionId);

        Mockito.verify(entitiesSelector).getContexts(0L,0L);
        for (int i = 0; i < entityIDs.length; i++) {
            Mockito.verify(modelStore).save(modelConf, entityIDs[i], entityModels[i], sessionId);
        }
        Mockito.verifyNoMoreInteractions(modelStore);        
    }

    @Test
    public void shouldBuildAndStoreGlobalModel() {
        long sessionId = 1234;
        Model globalModel = new Model() {};
        ModelBuilderManager modelManager = createProcessScenario(
                null,
                new Model[]{globalModel},
                new Object[]{new Object()},
                new Boolean[]{true});
        modelManager.process(null, sessionId);

        Mockito.verify(modelStore).save(modelConf, null, globalModel, sessionId);
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
        modelManager.process(null, 1234);
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
        modelManager.process(listener, 1234);

        Mockito.verify(listener).modelBuildingStatus(modelConfName, "user1", true);
        Mockito.verify(listener).modelBuildingStatus(modelConfName, "user2", false);
        Mockito.verifyNoMoreInteractions(listener);
    }
}
