package webapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.manager.airlfow.service.ConfigurationAirflowServcie;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.manager.api.records.ValidationResults;
import presidio.security.manager.service.ConfigurationSecurityService;
import presidio.webapp.service.ConfigurationManagerService;
import presidio.webapp.spring.ManagerWebappConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigurationProcessingMngServiceTest {


    private ConfigurationManagerService configurationManagerService;
    private JsonNode goodPresidioConfiguration;
    private JsonNode dataPipeLineWithThreeFields;
    private JsonNode dataPipeLineWithUnvalidSchema;


    @Test
    public void contextLoads() {
    }

    public void createDataPipeLineWithThreeFields() {
        ArrayNode arrayNode;
        JsonNode dataPipeline;
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(false));
        arrayNode = new ArrayNode(new JsonNodeFactory(false));
        String file = "file";
        arrayNode.add(file);
        objectNode.set("schemas", arrayNode);
        objectNode.set("badSchemas", arrayNode);
        TextNode textNode = new TextNode("2007-12-03T10:15:30.00Z");
        dataPipeline = objectNode.set("startTime", textNode);
        objectNode = new ObjectNode(new JsonNodeFactory(false));
        dataPipeLineWithThreeFields = objectNode.set("dataPipeline", dataPipeline);
    }

    public void createDataPipeLineWithUnvalidSchema() {
        ArrayNode arrayNode;
        JsonNode dataPipeline;
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(false));
        arrayNode = new ArrayNode(new JsonNodeFactory(false));
        String file = "badFile";
        arrayNode.add(file);
        objectNode.set("schemas", arrayNode);
        TextNode textNode = new TextNode("2007-12-03T10:15:30.00Z");
        dataPipeline = objectNode.set("startTime", textNode);
        objectNode = new ObjectNode(new JsonNodeFactory(false));
        dataPipeLineWithUnvalidSchema = objectNode.set("dataPipeline", dataPipeline);
    }


    public void createGoodPresidioConfiguration() {
        JsonNode system;
        ArrayNode arrayNode;
        JsonNode dataPipeline;
        ObjectNode objectNode = new ObjectNode(new JsonNodeFactory(false));

        TextNode textNode = new TextNode("presidio@somecompany.dom");
        objectNode.set("username", textNode);
        textNode = new TextNode("password");
        objectNode.set("password", textNode);
        textNode = new TextNode("presidio-admins-somecompany");
        objectNode.set("adminGroup", textNode);
        textNode = new TextNode("presidio-soc-team-somecompany");
        objectNode.set("analystGroup", textNode);
        textNode = new TextNode("name.of-server.com:25");
        objectNode.set("smtpHost", textNode);
        textNode = new TextNode("string");
        system = objectNode.set("kdcUrl", textNode);

        objectNode = new ObjectNode(new JsonNodeFactory(false));
        arrayNode = new ArrayNode(new JsonNodeFactory(false));
        String file = "file";
        arrayNode.add(file);
        objectNode.set("schemas", arrayNode);
        textNode = new TextNode("2007-12-03T10:15:30.00Z");
        dataPipeline = objectNode.set("startTime", textNode);

        objectNode = new ObjectNode(new JsonNodeFactory(false));
        objectNode.set("system", system);
        goodPresidioConfiguration = objectNode.set("dataPipeline", dataPipeline);
    }

    @Test
    public void validConfiguration() {
        createGoodPresidioConfiguration();
        configurationManagerService = new ConfigurationManagerService(new ConfigurationAirflowServcie(), new ConfigurationSecurityService());
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(goodPresidioConfiguration);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertEquals(validationResults.getErrorsList().size(), 0);
    }

    @Test
    public void unvalidConfigurationDataPipeLineWithUnvalidSchema() {
        createDataPipeLineWithUnvalidSchema();
        configurationManagerService = new ConfigurationManagerService(new ConfigurationAirflowServcie(), new ConfigurationSecurityService());
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(dataPipeLineWithUnvalidSchema);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertFalse(validationResults.getErrorsList().size() != 1);
    }

    @Test
    public void unvalidConfigurationDataPipeLineWithThreeFields() {
        createDataPipeLineWithThreeFields();
        configurationManagerService = new ConfigurationManagerService(new ConfigurationAirflowServcie(), new ConfigurationSecurityService());
        PresidioManagerConfiguration presidioManagerConfiguration = configurationManagerService.presidioManagerConfigurationFactory(dataPipeLineWithThreeFields);
        ValidationResults validationResults = configurationManagerService.validateConfiguration(presidioManagerConfiguration);
        Assert.assertFalse(validationResults.getErrorsList().size() != 1);
    }

    @Configuration
    @Import(value = {ManagerWebappConfiguration.class})
    @EnableSpringConfigured
    public static class springConfig {

    }

}
