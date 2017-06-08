package fortscale.ml.model.store;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import fortscale.utils.mongodb.util.MongoDbUtilServiceConfig;
import fortscale.utils.monitoring.stats.config.NullStatsServiceConfig;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.Set;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {
        MongodbTestConfig.class,
        MongoDbUtilServiceConfig.class,
        ModelStoreConfig.class,
        NullStatsServiceConfig.class
})
public class ModelStoreTest {
    private static final String COLLECTION_NAME_PREFIX = "model_";
    private static final String DEFAULT_MODEL_CONF_NAME = "testModelConf";
    private static final String DEFAULT_SESSION_ID = "testSessionId";

    @MockBean
    private ModelConf modelConf;
    @Mock
    private Model model;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ModelStore modelStore;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(modelConf.getName()).thenReturn(DEFAULT_MODEL_CONF_NAME);
    }


    @Test
    public void shouldCreateCollectionIfDoesNotExist() {
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        String collectionName = modelStore.getCollectionName(modelConf);
        Assert.assertTrue(!collectionNames.contains(collectionName));
        modelStore.save(modelConf, DEFAULT_SESSION_ID, "contextId", model, new Date(), new Date());
        collectionNames = mongoTemplate.getCollectionNames();
        Assert.assertTrue(collectionNames.contains(collectionName));

    }

}
