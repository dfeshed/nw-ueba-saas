package presidio.input.core;


import fortscale.common.shell.PresidioExecutionService;
import com.github.fakemongo.Fongo;
import fortscale.utils.test.mongodb.MongodbTestConfig;
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
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.spring.InputCoreConfiguration;


@RunWith(SpringRunner.class)
@ContextConfiguration
public class FortscaleInputCoreApplicationTest {


    @Autowired
    private PresidioExecutionService processService;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(processService instanceof InputExecutionServiceImpl);
    }

    @Configuration
    @Import({InputCoreConfiguration.class, MongodbTestConfig.class})
    @EnableSpringConfigured
    public static class springConfig {
        }


}
