package fortscale.utils.mongo.converter;

import com.mongodb.Mongo;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongoDbTestProperties;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by barak_schuster on 12/4/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MongoConverterConfigurerTest {
    @Configuration
    @Import(MongodbTestConfig.class)
    public static class springConfig {

        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            return new TestPropertiesPlaceholderConfigurer(MongoDbTestProperties.getProperties());
        }
    }
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private Mongo mongo;
    @Value("${mongo.db.name}")
    private String dbName;

    @Before
    public void setup()
    {
        if(mongoTemplate.collectionExists(Foo.class)) {
            mongoTemplate.dropCollection(Foo.class);
        }
    }

    @Document (collection = Foo.TEST_CONVERTER_COLLECTION)
    public static class Foo
    {
        public static final String TEST_CONVERTER_COLLECTION = "test_converter_collection";
        @Id
        private String id;
        private Map<String,String> mapToConvert;

        public Foo() {
            mapToConvert = new HashMap<>();
        }

        public Foo(Map<String, String> mapToConvert) {
            this.mapToConvert = mapToConvert;
        }

        public Foo(String id, Map<String, String> mapToConvert) {
            this.id = id;
            this.mapToConvert = mapToConvert;
        }

        public Map<String, String> getMapToConvert() {
            return mapToConvert;
        }

        public void setMapToConvert(Map<String, String> mapToConvert) {
            this.mapToConvert = mapToConvert;
        }
    }

    @Test
    public void should_convert_forbidden_keys()
    {
        Foo foo = new Foo();
        Map<String, String> mapToConvert = foo.getMapToConvert();
        mapToConvert.put(".","value1");
        mapToConvert.put("$","value2");
        mapToConvert.put("$$key$$","value3");
        mongoTemplate.insert(foo);
        Map<String, String> retrievedDocConvertedMap = (Map<String, String>) mongo.getDB(dbName).getCollection(Foo.TEST_CONVERTER_COLLECTION).findOne().get("mapToConvert");

        Set<String> retrievedMapKeys = retrievedDocConvertedMap.keySet();
        Assert.assertEquals(retrievedMapKeys.size(),mapToConvert.keySet().size());
        retrievedMapKeys.forEach(key -> {
            Assert.assertTrue(!key.contains("."));
            Assert.assertTrue(!key.startsWith("$"));
        });


        Foo retrievedDocuments = mongoTemplate.findAll(Foo.class).get(0);

        Map<String, String> retrievedMap = retrievedDocuments.getMapToConvert();
        Assert.assertEquals(retrievedMap.get("."), "value1");
        Assert.assertEquals(retrievedMap.get("$"), "value2");
        Assert.assertEquals(retrievedMap.get("$$key$$"),"value3");

    }
}




