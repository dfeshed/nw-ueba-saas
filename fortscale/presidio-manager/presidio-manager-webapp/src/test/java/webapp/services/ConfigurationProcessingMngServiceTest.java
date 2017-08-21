package webapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.manager.airlfow.service.ConfigurationAirflowServcie;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.security.manager.service.ConfigurationSecurityService;
import presidio.webapp.service.ConfigurationManagerService;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
public class ConfigurationProcessingMngServiceTest {


    private ConfigurationManagerService configurationManagerService;
    private JsonNode goodPresidioConfiguration;
    private JsonNode presidioConfigurationOnlySystem;
    private JsonNode dataPipeLineWithThreeFields;
    private JsonNode dataPipeLineWithInvalidSchema;
    private JsonNode jsonWithGeneralError;


    @Before
    public void setNodes() {
        goodPresidioConfiguration = setJson("valid_configuration.json");
        presidioConfigurationOnlySystem = setJson("invalid_configuration_only_system.json");
        dataPipeLineWithThreeFields = setJson("invalid_configuration_data_pipe_line_with_three_fields.json");
        dataPipeLineWithInvalidSchema = setJson("invalid_configuratoin_bad_schema.json");
        jsonWithGeneralError = setJson("invalid_configuration_general_error.json");
        configurationManagerService = new ConfigurationManagerService(new ConfigurationAirflowServcie(), new ConfigurationSecurityService());
    }

    @Test
    public void contextLoads() {
        Assert.assertNotNull("configurationManagerService cannot be null on spring context", configurationManagerService);
    }

    @Test
    public void validConfiguration() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(goodPresidioConfiguration);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(0, validationResults.getErrorsList().size());
    }

    @Test
    public void generalErrorConfiguration() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(jsonWithGeneralError);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(2, validationResults.getErrorsList().size());
    }

    @Test
    public void invalidPresidioConfigurationOnlySystem() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(presidioConfigurationOnlySystem);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(1, validationResults.getErrorsList().size());
    }

    @Test
    public void invalidConfigurationDataPipeLineWithUnvalidSchema() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(dataPipeLineWithInvalidSchema);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(1, validationResults.getErrorsList().size());
    }

    @Test
    public void invalidConfigurationDataPipeLineWithThreeFields() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(dataPipeLineWithThreeFields);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(1, validationResults.getErrorsList().size());
    }

    private JsonNode setJson(String name) {
        ObjectMapper mapper = new ObjectMapper();
        File from = new File(".//src//test//resources//" + name);
        try {
            return mapper.readTree(from);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
