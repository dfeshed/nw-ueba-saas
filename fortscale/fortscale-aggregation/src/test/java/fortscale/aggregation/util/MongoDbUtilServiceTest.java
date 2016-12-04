package fortscale.aggregation.util;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.UncategorizedMongoDbException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by baraks on 12/4/2016.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration

public class MongoDbUtilServiceTest {

    @Configuration
    public static class springConfig {
        @Bean
        public MongoTemplate mongoTemplate()
        {
            return Mockito.mock(MongoTemplate.class);
        }
        @Bean
        public MongoDbUtilService mongoDbUtilService()
        {
            return new MongoDbUtilService();
        }
    }

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private MongoDbUtilService mongoDbUtilService;

    @Test
    public void shouldNotThrowExceptionIfCollectionAlreadyExists() throws Exception {
        String collectionName = "testCollection";
        Mockito.when(mongoTemplate.createCollection(collectionName)).thenThrow(new UncategorizedMongoDbException(": { \"serverUsed\" : \"localhost:27017\" , \"ok\" : 0.0 , \"errmsg\" : \"collection already exists\" , \"code\" : 48}",null));
        mongoDbUtilService.createCollection(collectionName);
        Assert.assertTrue(mongoDbUtilService.getCollections().contains(collectionName));
    }

}