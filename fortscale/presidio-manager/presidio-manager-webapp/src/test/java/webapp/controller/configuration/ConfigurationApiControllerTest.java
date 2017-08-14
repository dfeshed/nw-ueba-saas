package webapp.controller.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.webapp.controller.configuration.ConfigurationApiController;
import presidio.webapp.service.ConfigurationManagerService;
import presidio.webapp.spring.ManagerWebappConfiguration;

import java.io.File;
import java.io.IOException;

/**
 * Created by efratn on 13/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ManagerWebappConfiguration.class)
public class ConfigurationApiControllerTest {

    private static String CONFIG_JSON_FILE_NAME = "presidio_configuration_test.json";

    @MockBean
    private ConfigurationManagerService configurationProcessingManager;

    @MockBean
    private ConfigurationServerClientService configServerClient;

    @Autowired
    private ConfigurationApiController controller;

    @Test
    public void contextLoads() {
        Assert.notNull(controller, "client service on sprint context cannot be null");
    }

    @Test
    public void putConfiguration() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File from = new File(".//src//test//resources//" + CONFIG_JSON_FILE_NAME);
        JsonNode jsonBody = mapper.readTree(from);

        controller.configurationPut(jsonBody);
    }


}
