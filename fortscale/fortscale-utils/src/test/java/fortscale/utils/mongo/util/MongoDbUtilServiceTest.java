package fortscale.utils.mongo.util;

import fortscale.utils.mongodb.util.MongoDbUtilService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;

/**
 * Created by baraks on 12/4/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class MongoDbUtilServiceTest {

    @Test
    public void shouldNotThrowExceptionIfCollectionAlreadyExists() throws Exception {
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        MongoDbUtilService mongoDbUtilService = new MongoDbUtilService(mongoTemplate);
        String collectionName = "testCollection";
        Mockito.when(mongoTemplate.createCollection(collectionName)).thenThrow(new UncategorizedMongoDbException(": { \"serverUsed\" : \"localhost:27017\" , \"ok\" : 0.0 , \"errmsg\" : \"collection already exists\" , \"code\" : 48}",null));
        mongoDbUtilService.createCollection(collectionName);
        Assert.assertTrue(mongoDbUtilService.getCollections().contains(collectionName));
        Mockito.verify(mongoTemplate,times(1)).createCollection(collectionName);
    }

    @After
    public void validate() {
        validateMockitoUsage();
    }
}