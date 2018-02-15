package webapp.controller.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.PresidioEncryptionUtils;
import fortscale.utils.json.ObjectMapperProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.config.server.client.ConfigurationServerClientService;
import presidio.manager.api.records.ConfigurationBadParamDetails;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.webapp.controller.configuration.ConfigurationApiController;
import presidio.webapp.model.configuration.Configuration;
import presidio.webapp.model.configuration.ConfigurationResponse;
import presidio.webapp.service.ConfigurationManagerService;

import java.io.File;
import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * Created by efratn on 13/08/2017.
 */
@RunWith(SpringRunner.class)
public class ConfigurationApiControllerTest {

    private static String CONFIG_JSON_FILE_NAME = "valid_configuration.json";

    @Autowired
    private ApplicationContext ctx;

    @MockBean
    private ConfigurationManagerService configurationProcessingManager;

    @MockBean
    private ConfigurationServerClientService configServerClient;

    @Test
    public void putConfigurationInvalidConfiguration() throws IOException {
        ConfigurationApiController controller = new ConfigurationApiController(configurationProcessingManager, configServerClient, null, null, null);

        ObjectMapper mapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
        File from = ctx.getResource(CONFIG_JSON_FILE_NAME).getFile();
        JsonNode jsonBody = mapper.readTree(from);

        PresidioManagerConfiguration conf = configurationProcessingManager.presidioManagerConfigurationFactory(jsonBody);
        ConfigurationBadParamDetails error = new ConfigurationBadParamDetails("domain",
                "locationType",
                "reason",
                "location",
                "msg");
        ValidationResults validationResult = new ValidationResults(error);
        when(configurationProcessingManager.validateConfiguration(conf)).thenReturn(validationResult);
        Configuration configuration = mapper.readValue(from, Configuration.class);
        ResponseEntity<ConfigurationResponse> response = controller.configurationPut(configuration);
        Assert.isTrue(response.getStatusCode().equals(HttpStatus.BAD_REQUEST), "response HttpStatus should be BAD_REQUEST");
        Assert.isTrue(response.getBody().getError().size() == 1, "response error list size should be 1");
    }

    @Test
    public void putConfigurationConfiguration() throws Exception {
        final PresidioEncryptionUtils mockedPresidioEncryptionUtils = Mockito.mock(PresidioEncryptionUtils.class);
        when(mockedPresidioEncryptionUtils.encrypt(any())).thenReturn("mockedEncryptedPassword");

        ConfigurationApiController controller = new ConfigurationApiController(configurationProcessingManager, configServerClient, null, null, mockedPresidioEncryptionUtils);

        ObjectMapper mapper = ObjectMapperProvider.getInstance().getDefaultObjectMapper();
        File from = ctx.getResource(CONFIG_JSON_FILE_NAME).getFile();
        JsonNode jsonBody = mapper.readTree(from);

        PresidioManagerConfiguration conf = configurationProcessingManager.presidioManagerConfigurationFactory(jsonBody);
        ValidationResults validationResult = new ValidationResults();
        when(configurationProcessingManager.validateConfiguration(conf)).thenReturn(validationResult);
        when(configurationProcessingManager.applyConfiguration()).thenReturn(true);

        Configuration configuration = mapper.readValue(from, Configuration.class);
        ResponseEntity<ConfigurationResponse> response = controller.configurationPut(configuration);
        Assert.isTrue(response.getStatusCode().equals(HttpStatus.CREATED), "response HttpStatus should be CREATED");
        Assert.isTrue(response.getBody().getError().isEmpty(), "response error list should be empty");
    }

}
