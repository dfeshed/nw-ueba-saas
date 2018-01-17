package fortscale.ml.model.store;

import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.TimeRange;
import fortscale.utils.store.StoreManager;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {MongodbTestConfig.class, ModelStoreConfig.class})

public class ModelStoreTest {
	@Autowired
	private MongoTemplate mongoTemplate;
	@Autowired
	private ModelStore modelStore;

	@Test
	public void should_create_collection_if_it_does_not_exist() {
		ModelConf modelConf = mock(ModelConf.class);
		when(modelConf.getName()).thenReturn("modelConfName");
		String collectionName = ModelStore.getCollectionName(modelConf);
		Assert.assertFalse(mongoTemplate.getCollectionNames().contains(collectionName));
		StoreManager storeManager = mock(StoreManager.class);
		modelStore.setStoreManager(storeManager);
		modelStore.save(modelConf, "sessionId", "contextId", mock(Model.class), new TimeRange(0, 0));
		Assert.assertTrue(mongoTemplate.getCollectionNames().contains(collectionName));
	}
}
