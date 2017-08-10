package presidio.config.server.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.config.server.spring.ConfigServerClientServiceConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by efratn on 10/08/2017.
 */
@RunWith(SpringRunner.class)
@SpringBootTest()
@ContextConfiguration(classes = ConfigurationServiceClientServiceTest.SpringConfig.class)
@Ignore
//@ContextConfiguration(classes = {ConfigServerClientServiceConfiguration.class})
public class ConfigurationServiceClientServiceTest {

    private static String CONFIG_JSON_FILE_NAME = "presidio_configuration_test.json";

    @Autowired
    private ConfigurationServerClientService client;

    @Configuration
    @Import(ConfigServerClientServiceConfiguration.class)
    @EnableSpringConfigured
    public static class SpringConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer AdeManagerSdkTestPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("spring.cloud.config.uri", "http://localhost:8888");
            properties.put("spring.cloud.config.username", "config");
            properties.put("spring.cloud.config.password", "secure");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }

    @Test
    public void contextLoadTest() {
        Assert.notNull(client, "client service on sprint context cannot be null");
    }

    @Test
    public void storeJsonConfigFile() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        File from = new File(".//src//test//resources//" + CONFIG_JSON_FILE_NAME);
        JsonNode json = mapper.readTree(from);

        client.storeConfigurationFile(CONFIG_JSON_FILE_NAME, json);
    }


}
