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
    private ModelStore store;
    private String modelConfCollectionName;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        String modelConfName = "modelConfName";
        Mockito.when(modelConf.getName()).thenReturn(modelConfName);
        modelConfCollectionName = COLLECTION_NAME_PREFIX + modelConfName;
    }

    private void mockCollectionExistence(String collectionName, boolean shouldExist) {
        Mockito.when(mongoDbUtilService.collectionExists(collectionName)).thenReturn(shouldExist);
    }

    @Test
    public void shouldCreateCollectionIfDoesNotExist() {
        mockCollectionExistence(modelConfCollectionName, false);
        IndexOperations indexOperations = Mockito.mock(IndexOperations.class);
        Mockito.when(mongoTemplate.indexOps(modelConfCollectionName)).thenReturn(indexOperations);

        DateTime sessionStartTime = DateTime.now();
        DateTime sessionEndTime = sessionStartTime.plusDays(1);
        store.save(modelConf, "contextId", model, sessionStartTime, sessionEndTime);

        Mockito.verify(mongoDbUtilService).createCollection(modelConfCollectionName);
        Mockito.verify(indexOperations, Mockito.times(1)).ensureIndex(Mockito.any(IndexDefinition.class));
    }

    @Test
    public void shouldNotCreateCollectionIfDoesExist() {
        mockCollectionExistence(modelConfCollectionName, true);

        DateTime sessionStartTime = DateTime.now();
        DateTime sessionEndTime = sessionStartTime.plusDays(1);
        store.save(modelConf, "contextId", model, sessionStartTime, sessionEndTime);

        Mockito.verify(mongoDbUtilService, Mockito.times(0)).createCollection(modelConfCollectionName);
    }

    @Test
    public void shouldSaveTheModel() {
        mockCollectionExistence(modelConfCollectionName, true);
        ArgumentCaptor<ModelDAO> argumentCaptor = ArgumentCaptor.forClass(ModelDAO.class);
        Mockito.doNothing().when(mongoTemplate).save(argumentCaptor.capture(), Mockito.eq(modelConfCollectionName));

        String contextId = "contextId";
        DateTime sessionStartTime = DateTime.now();
        DateTime sessionEndTime = sessionStartTime.plusDays(1);
        store.save(modelConf, contextId, model, sessionStartTime, sessionEndTime);

        ModelDAO modelDAOArg = argumentCaptor.getValue();
        Assert.assertEquals(sessionStartTime, Whitebox.getInternalState(modelDAOArg, "sessionStartTime"));
        Assert.assertEquals(sessionEndTime, Whitebox.getInternalState(modelDAOArg, "sessionEndTime"));
        Assert.assertEquals(contextId, Whitebox.getInternalState(modelDAOArg, "contextId"));
        Assert.assertEquals(model, Whitebox.getInternalState(modelDAOArg, "model"));
        Assert.assertEquals(System.currentTimeMillis(), ((DateTime)Whitebox.getInternalState(modelDAOArg, "creationTime")).getMillis(), 1000);
    }
}
