package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by efratn on 10/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = ConfigServerClientServiceConfiguration.class)
public class ConfigurationServiceClientServiceTest {

    @Autowired
    private ConfigurationServerClientService client;


    @Test
    public void contextLoadTest() {
        Assert.notNull(client, "client service on sprint context cannot be null");
    }

    @Test
    public void storeJsonConfigFile() {
        ObjectMapper mapper = new ObjectMapper();
        File from = new File("path to file");
        try {
            JsonNode masterJSON = mapper.readTree(from);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
