import com.fasterxml.jackson.databind.JsonNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.webapp.service.ConfigurationProcessingManager;
import webapp.spring.ManagerWebappConfigurationTest;

/**
 * Created by efratn on 10/08/2017.
**/
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ManagerWebappConfigurationTest.class)
public class FotscaleManagerWebappTest {


    private ConfigurationProcessingManager configurationProcessingManager;
    private JsonNode goodPresidioConfiguration;

    @Test
    public void contextLoads() {
        Assert.notNull(configurationProcessingManager, "client service on sprint context cannot be null");
    }
}