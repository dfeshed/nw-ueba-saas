package fortscale.ml.model.store;

import fortscale.aggregation.util.MongoDbUtilService;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ModelStoreTest {
    private static final String COLLECTION_NAME_PREFIX = "model_";
    private static final String DEFAULT_MODEL_CONF_NAME = "testModelConf";
    private static final String DEFAULT_SESSION_ID = "testSessionId";

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
        when(modelConf.getName()).thenReturn(DEFAULT_MODEL_CONF_NAME);
        collectionName = COLLECTION_NAME_PREFIX + DEFAULT_MODEL_CONF_NAME;
    }

    private void mockCollectionExistence(String collectionName, boolean shouldExist) {
        when(mongoDbUtilService.collectionExists(collectionName)).thenReturn(shouldExist);
    }

    @Test
    public void shouldCreateCollectionIfDoesNotExist() {
        mockCollectionExistence(collectionName, false);
        IndexOperations indexOperations = mock(IndexOperations.class);
        when(mongoTemplate.indexOps(collectionName)).thenReturn(indexOperations);

        modelStore.save(modelConf, DEFAULT_SESSION_ID, "contextId", model, new Date());
        verify(mongoDbUtilService).createCollection(collectionName);
        verify(indexOperations, times(2)).ensureIndex(any(IndexDefinition.class));
    }

    @Test
    public void shouldNotCreateCollectionIfDoesExist() {
        mockCollectionExistence(collectionName, true);

        modelStore.save(modelConf, DEFAULT_SESSION_ID, "contextId", model, new Date());
        verify(mongoDbUtilService, times(0)).createCollection(collectionName);
        verify(mongoTemplate, times(0)).indexOps(anyString());
    }

    @Test
    public void shouldSaveTheModel() {
        mockCollectionExistence(collectionName, true);
        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);

        String contextId = "contextId";
        Date endTime = new Date();
        modelStore.save(modelConf, DEFAULT_SESSION_ID, contextId, model, endTime);

        Query expectedQuery = new Query();
        expectedQuery.addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(DEFAULT_SESSION_ID));
        expectedQuery.addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId));
        Update expectedUpdate = new Update();
        expectedUpdate.set(ModelDAO.MODEL_FIELD, model);
        expectedUpdate.set(ModelDAO.END_TIME_FIELD, endTime);

        verify(mongoTemplate, times(1)).upsert(queryCaptor.capture(), updateCaptor.capture(), eq(collectionName));
        Assert.assertEquals(expectedQuery, queryCaptor.getValue());
        Assert.assertEquals(Whitebox.getInternalState(expectedUpdate, "modifierOps"), Whitebox.getInternalState(updateCaptor.getValue(), "modifierOps"));
    }
}
