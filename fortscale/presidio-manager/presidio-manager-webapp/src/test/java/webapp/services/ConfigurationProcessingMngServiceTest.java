package webapp.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.manager.air.flow.service.ConfigurationProcessingServiceImpl;
import presidio.manager.api.records.PresidioManagerConfiguration;
import presidio.webapp.service.ConfigurationProcessingManager;
import presidio.webapp.spring.ManagerWebappConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ConfigurationProcessingMngServiceTest {


    private ConfigurationProcessingManager configurationProcessingManager;
    private JsonNode goodPresidioConfiguration;

    @Test
    public void contextLoads() {
        Assert.notNull(configurationProcessingManager, "configurationProcessingMng cannot be null on spring context");
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
        arrayNode= new ArrayNode(new JsonNodeFactory(false));
        arrayNode.add("file");
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
        configurationProcessingManager = new ConfigurationProcessingManager(new ConfigurationProcessingServiceImpl(), new ConfigurationProcessingServiceImpl());
        PresidioManagerConfiguration presidioManagerConfiguration = configurationProcessingManager.presidioManagerConfigurationFactory(goodPresidioConfiguration);
        configurationProcessingManager.validateConfiguration(presidioManagerConfiguration);
    }

    @Configuration
    @Import(value = {ManagerWebappConfiguration.class})
    @EnableSpringConfigured
    public static class springConfig {

    }

}
