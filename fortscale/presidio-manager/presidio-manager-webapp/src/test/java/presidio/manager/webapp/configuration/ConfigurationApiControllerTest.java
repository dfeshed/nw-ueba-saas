package presidio.manager.webapp.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import presidio.config.server.client.ConfigurationServerClientService;

import static java.lang.System.getProperty;
import static java.util.Collections.singletonList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class ConfigurationApiControllerTest {
    private final ConfigurationServerClientService configurationServerClientService = mock(
            ConfigurationServerClientService.class);
    private final ConfigurationApi configurationApi = new ConfigurationApiController(
            singletonList("default"),
            null,
            "workflows",
            getProperty("user.dir"),
            configurationServerClientService);

    @Test
    public void invalid_data_pipeline_schemas() {
        testInvalidOrNullConfiguration("schemas");
    }

    @Test
    public void invalid_data_pipeline_start_time() {
        testInvalidOrNullConfiguration("startTime");
    }

    @Test
    public void invalid_data_pulling_source() {
        testInvalidOrNullConfiguration("source");
    }

    @Test
    public void invalid_output_forwarding_enable_forwarding() {
        testInvalidOrNullConfiguration("enableForwarding");
    }

    @Test
    public void invalid_ui_integration_admin_server() {
        testInvalidOrNullConfiguration("adminServer");
    }

    @Test
    public void invalid_ui_integration_broker_id() {
        testInvalidOrNullConfiguration("brokerId");
    }

    @Test
    public void null_data_pipeline() {
        testInvalidOrNullConfiguration("dataPipeline");
    }

    @Test
    public void null_data_pulling() {
        testInvalidOrNullConfiguration("dataPulling");
    }

    @Test
    public void null_ui_integration() {
        testInvalidOrNullConfiguration("uiIntegration");
    }

    @Test
    public void valid_with_output_forwarding() {
        testValidConfiguration(true);
    }

    @Test
    public void valid_without_output_forwarding() {
        testValidConfiguration(false);
    }

    private String loadResource() {
        // Get the name of the calling method, which should be the name of the resource (the JSON file).
        String name = Thread.currentThread().getStackTrace()[3].getMethodName();

        try {
            return IOUtils.toString(getClass().getClassLoader().getResourceAsStream(name + ".json"));
        } catch (Exception e) {
            throw new RuntimeException(String.format("Could not load the resource named %s.", name), e);
        }
    }

    private void testInvalidOrNullConfiguration(String key) {
        ResponseEntity<ConfigurationResponse> response = configurationApi.configurationPut(loadResource());
        Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        Assert.assertTrue(response.getBody().getMessage().contains(key));
    }

    private void testValidConfiguration(boolean enableForwarding) {
        reset(configurationServerClientService);
        // Throw a runtime exception before reaching the updateWorkflowsConfiguration method.
        when(configurationServerClientService.storeConfigurationFile(eq("application-presidio"), any(JsonNode.class)))
                .thenThrow(new RuntimeException("myException"));

        try {
            configurationApi.configurationPut(loadResource());
        } catch (RuntimeException e) {
            Assert.assertEquals("myException", e.getMessage());
        }

        ArgumentCaptor<JsonNode> argumentCaptor = ArgumentCaptor.forClass(JsonNode.class);
        verify(configurationServerClientService).storeConfigurationFile(eq("application-presidio"), argumentCaptor.capture());
        JsonNode jsonNode = argumentCaptor.getValue().get("outputForwarding").get("enableForwarding");
        Assert.assertTrue(jsonNode.isBoolean());
        Assert.assertEquals(enableForwarding, jsonNode.asBoolean());
    }
}
