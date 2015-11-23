package fortscale.ml.model.store;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;

public class ModelStoreTest {
    private static final String COLLECTION_NAME_PREFIX = "model_";

    @Mock
    private ModelConf modelConf;
    @Mock
    private Model model;
    @Mock
    private MongoTemplate mongoTemplate;
    @Mock
    private MongoDbUtilService mongoDbUtilService;
    @InjectMocks
    private ModelStore modelStore;
    private String collectionName;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        String modelConfName = "testModelConf";
        Mockito.when(modelConf.getName()).thenReturn(modelConfName);
        collectionName = COLLECTION_NAME_PREFIX + modelConfName;
    }

    private void mockCollectionExistence(String collectionName, boolean shouldExist) {
        Mockito.when(mongoDbUtilService.collectionExists(collectionName)).thenReturn(shouldExist);
    }

    @Test
    public void shouldCreateCollectionIfDoesNotExist() {
        mockCollectionExistence(collectionName, false);
        IndexOperations indexOperations = Mockito.mock(IndexOperations.class);
        Mockito.when(mongoTemplate.indexOps(collectionName)).thenReturn(indexOperations);
        DateTime endTime = DateTime.now();
        modelStore.save(modelConf, "contextId", model, endTime);
        Mockito.verify(mongoDbUtilService).createCollection(collectionName);
        Mockito.verify(indexOperations, Mockito.times(1)).ensureIndex(Mockito.any(IndexDefinition.class));
    }

    @Test
    public void shouldNotCreateCollectionIfDoesExist() {
        mockCollectionExistence(collectionName, true);
        DateTime endTime = DateTime.now();
        modelStore.save(modelConf, "contextId", model, endTime);
        Mockito.verify(mongoDbUtilService, Mockito.times(0)).createCollection(collectionName);
    }

    @Test
    public void shouldSaveTheModel() {
        mockCollectionExistence(collectionName, true);
        ArgumentCaptor<ModelDAO> argumentCaptor = ArgumentCaptor.forClass(ModelDAO.class);
        Mockito.doNothing().when(mongoTemplate).save(argumentCaptor.capture(), Mockito.eq(collectionName));
        String contextId = "contextId";
        DateTime endTime = DateTime.now();
        modelStore.save(modelConf, contextId, model, endTime);
        ModelDAO modelDAOArg = argumentCaptor.getValue();
        Assert.assertEquals(contextId, Whitebox.getInternalState(modelDAOArg, "contextId"));
        Assert.assertEquals(System.currentTimeMillis(), ((DateTime)Whitebox.getInternalState(modelDAOArg, "creationTime")).getMillis(), 1000);
        Assert.assertEquals(model, Whitebox.getInternalState(modelDAOArg, "model"));
        Assert.assertEquals(endTime, Whitebox.getInternalState(modelDAOArg, "endTime"));
    }
}
