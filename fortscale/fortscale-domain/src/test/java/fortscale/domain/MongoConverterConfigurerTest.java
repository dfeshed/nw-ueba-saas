package fortscale.domain;

import fortscale.domain.core.dao.MongoDbRepositoryUtil;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Created by barak_schuster on 12/4/16.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class MongoConverterConfigurerTest {
    @Configuration
    @ImportResource(locations = "classpath*:META-INF/spring/fortscale-domain-light-context.xml")
    @ComponentScan(basePackageClasses = MongoDbRepositoryUtil.class,includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = MongoDbRepositoryUtil.class))
    public static class springConfig {

        @Bean
        public static TestPropertiesPlaceholderConfigurer mainProcessPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("mongo.host.name", "localhost");
            properties.put("mongo.host.port","27017");
            properties.put("mongo.db.name","test");
            properties.put("mongo.map.dot.replacement","#dot#");
            properties.put("mongo.map.dollar.replacement","#dlr#");
            properties.put("mongo.db.user", "");
            properties.put("mongo.db.password", "");

            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
    @Autowired
    private MongoTemplate mongoTemplate;

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
        mongoTemplate.insert(foo);
        Foo retrievedDocuments = mongoTemplate.findAll(Foo.class).get(0);

        Map<String, String> retrievedMap = retrievedDocuments.getMapToConvert();
        Assert.assertEquals(retrievedMap.get("."), "value1");
        Assert.assertEquals(retrievedMap.get("$"), "value2");

    }
}




