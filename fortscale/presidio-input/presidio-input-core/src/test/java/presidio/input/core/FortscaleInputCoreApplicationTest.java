package presidio.input.core;


import com.github.fakemongo.Fongo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.api.InputExecutionService;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.spring.InputCommandLineRunnerConfiguration;


@RunWith(SpringRunner.class)
@ContextConfiguration
public class FortscaleInputCoreApplicationTest {


    @Autowired
    private InputExecutionService processService;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(processService instanceof InputExecutionServiceImpl);
    }

    @Configuration
    @Import({InputCommandLineRunnerConfiguration.class})
    @EnableSpringConfigured
    public static class springConfig {
        private static final String FORTSCALE_TEST_DB = "fortscaleTestDb";


        private MongoDbFactory mongoDbFactory() {
            Fongo fongo = new Fongo(FORTSCALE_TEST_DB);

            return new SimpleMongoDbFactory(fongo.getMongo(), FORTSCALE_TEST_DB);
        }


        @Bean
        public MongoTemplate mongoTemplate() {
            return new MongoTemplate(mongoDbFactory());
        }

    }
}
