import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.webapp.service.ConfigurationManagerService;
import presidio.webapp.spring.ManagerWebappConfiguration;
import webapp.spring.ManagerWebappConfigurationTest;

/**
 * Created by efratn on 10/08/2017.
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ManagerWebappConfiguration.class)
public class FotscaleManagerWebappTest {

    @Autowired
    private ConfigurationManagerService configurationManagerService;

    @Test
    public void contextLoads() {
        Assert.notNull(configurationManagerService, "client service on sprint context cannot be null");
    }
}