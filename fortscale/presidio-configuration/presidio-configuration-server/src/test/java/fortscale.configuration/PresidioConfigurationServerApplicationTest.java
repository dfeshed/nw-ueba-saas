package fortscale.configuration;

import fortscale.configuration.resource.WritableResourceRepository;
import fortscale.configuration.spring.PresidioConfigServerConfiguration;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = PresidioConfigServerConfiguration.class)
public class PresidioConfigurationServerApplicationTest {

    @Autowired
    WritableResourceRepository repository;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(repository instanceof WritableResourceRepository);
    }

}
