package webapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.forwarder.manager.service.ConfigurationForwarderService;
import presidio.manager.airlfow.service.ConfigurationAirflowService;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.security.manager.service.ConfigurationSecurityService;
import presidio.webapp.service.ConfigurationDataPullingService;
import presidio.webapp.service.ConfigurationManagerService;

import java.io.File;
import java.io.IOException;

@RunWith(SpringRunner.class)
public class ConfigurationProcessingMngServiceTest {


    private ConfigurationManagerService configurationManagerService;
    private JsonNode goodPresidioConfiguration;
    private JsonNode presidioConfigurationOnlySystem;
    private JsonNode dataPipeLineWithThreeFields;
    private JsonNode forwarderBadParam;
    private JsonNode forwarderEnableTrueMissingSyslog;
    private JsonNode forwarderMissingOutputForwarder;
    private JsonNode forwarderFalseAndSyslogInOutputConfiguration;
    private JsonNode forwarderFalseAndWithoutSyslog;
    private JsonNode dataPipeLineWithInvalidSchema;
    private JsonNode dataPullingMissingConfiguration;
    private JsonNode dataPullingMissingSourceConfiguration;
    private JsonNode jsonWithGeneralError;
    @Autowired
    private ApplicationContext ctx;

    @Before
    public void setNodes() {
        goodPresidioConfiguration = setJson("valid_configuration.json");
        forwarderBadParam = setJson("invalid_forwarder_configuration_missing_params.json");
        presidioConfigurationOnlySystem = setJson("invalid_configuration_only_system.json");
        dataPipeLineWithThreeFields = setJson("invalid_configuration_data_pipe_line_with_three_fields.json");
        dataPipeLineWithInvalidSchema = setJson("invalid_configuration_bad_schema.json");
        jsonWithGeneralError = setJson("invalid_configuration_general_error.json");
        forwarderEnableTrueMissingSyslog = setJson("invalid_configuration_enableForwarding_true.json");
        forwarderMissingOutputForwarder = setJson("invalid_configuration_missing_outputForwarder.json");
        forwarderFalseAndSyslogInOutputConfiguration = setJson("invalid_configuration_false_and_syslog.json");
        forwarderFalseAndWithoutSyslog = setJson("valid_configuration_enableForwarder_false.json");
        dataPullingMissingConfiguration = setJson("invalid_configuration_missing_dataPulling.json");
        dataPullingMissingSourceConfiguration = setJson("invalid_configuration_missing_dataPulling_source.json");
        configurationManagerService = new ConfigurationManagerService(new ConfigurationAirflowService(null, "workflows", null, null), new ConfigurationSecurityService(null, null, "/tmp/httpdtest.conf", "/tmp/krb5test.conf", false), new ConfigurationForwarderService(), new ConfigurationDataPullingService());
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
        Assert.assertEquals(3, validationResults.getErrorsList().size());
    }

    @Test
    public void invalidPresidioConfigurationOnlySystem() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(presidioConfigurationOnlySystem);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(3, validationResults.getErrorsList().size());
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

//    @Test
//    public void invalidConfigurationForwarder() {
//        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(forwarderBadParam);
//        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
//        Assert.assertEquals(2, validationResults.getErrorsList().size());
//    }

//    @Test
//    public void forwarderEnableTrueMissingSyslog() {
//        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(forwarderEnableTrueMissingSyslog);
//        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
//        Assert.assertEquals(1, validationResults.getErrorsList().size());
//    }

    @Test
    public void forwarderMissingOutputForwarder() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(forwarderMissingOutputForwarder);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(1, validationResults.getErrorsList().size());
    }

    @Test
    public void dataPullingMissingConfiguration() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(dataPullingMissingConfiguration);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(1, validationResults.getErrorsList().size());
    }

    @Test
    public void dataPullingMissingConfiguration_missingSourceProperty() {
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(dataPullingMissingSourceConfiguration);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(1, validationResults.getErrorsList().size());
    }

//    @Test
//    public void forwarderFalseAndSyslogInOutputConfiguration() {
//        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(forwarderFalseAndSyslogInOutputConfiguration);
//        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
//        Assert.assertEquals(1, validationResults.getErrorsList().size());
//    }

//    @Test
//    public void forwarderFalseAndWithoutSyslog() {
//        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(forwarderFalseAndWithoutSyslog);
//        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
//        Assert.assertEquals(0, validationResults.getErrorsList().size());
//    }

    private JsonNode setJson(String name) {
        ObjectMapper mapper = new ObjectMapper();


        try {
            File from = ctx.getResource(name).getFile();
            return mapper.readTree(from);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
