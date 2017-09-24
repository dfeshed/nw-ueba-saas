package webapp.controller.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import presidio.webapp.controller.configuration.ConfigurationApi;
import presidio.webapp.model.configuration.*;
import presidio.webapp.spring.ManagerWebappConfiguration;

import java.time.Instant;
import java.util.Properties;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by barak_schuster on 9/24/17.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ConfigurationApiControllerModuleTest.springConfig.class)
@Category(ModuleTestCategory.class)
public class ConfigurationApiControllerModuleTest {

    private static final String CONFIGURATION_URI = "/configuration";
    private MockRestServiceServer mockRestServiceServer;
    private MockMvc managerConfigurationMVC;
    @Autowired
    private ConfigurationApi configurationApi;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    private final String AIRFLOW_CONFIGURATION_RESPONSE =
            "{\"dataPipeline\":{\"schemas\":[],\"startTime\":\"2017-01-01T10:00:00Z\"},\"system\":{\"adminGroup\":\"presidio-admins-somecompany\",\"analystGroup\":\"presidio-soc-team-somecompany\",\"kdcUrl\":\"string\",\"password\":\"password\",\"smtpHost\":\"name.of-server.com:25\",\"username\":\"presidio@somecompany.dom\"}}";
    private final String CONFIGURATION_PATCH_REQUEST =
            "[\n" +
                    "  {\n" +
                    "    \"op\": \"add\",\n" +
                    "    \"path\": \"/dataPipeline/schemas\",\n" +
                    "    \"value\": [\"AUTHENTICATION\"]\n" +
                    "  }\n" +
                    "]";
    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        managerConfigurationMVC = MockMvcBuilders.standaloneSetup(configurationApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();

        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        mockRestServiceServer.reset();
        mockRestServiceServer.expect(requestTo("http://localhost:8888/application-presidio-default.json"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(AIRFLOW_CONFIGURATION_RESPONSE, MediaType.APPLICATION_JSON_UTF8));
        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();
    }

    @After
    public void tearDown() {
        mockRestServiceServer.verify();
    }

    @Test
    public void shouldGetConfigurationFromAirflow() throws Exception {

        // init expected response
        SecuredConfiguration expectedResponse = new SecuredConfiguration();

        DataConfiguration dataPipeline = new DataConfiguration();
        Instant startTime = Instant.parse("2017-01-01T10:00:00Z");
        dataPipeline.setStartTime(startTime);
        SecuredSystemConfiguration system = new SecuredSystemConfiguration();
        system.adminGroup("presidio-admins-somecompany");
        system.analystGroup("presidio-soc-team-somecompany");
        system.setKdcUrl("string");
        system.setSmtpHost("name.of-server.com:25");
        system.setUsername("presidio@somecompany.dom");
        expectedResponse.setSystem(system);
        expectedResponse.setDataPipeline(dataPipeline);

        // get actual response
        SecuredConfiguration actualResponse = getActualConfiguration();

        // you know...
        Assert.assertEquals(expectedResponse, actualResponse);

    }

    private SecuredConfiguration getActualConfiguration() throws Exception {
        MvcResult mvcResult = managerConfigurationMVC.perform(get(CONFIGURATION_URI)).andExpect(status().isOk()).andReturn();
        String actualResponseStr = mvcResult.getResponse().getContentAsString();
        return objectMapper.readValue(actualResponseStr, SecuredConfiguration.class);
    }

    @Test
    public void shouldPatchDataPipelineConfiguration() throws Exception {
        // init expected response
        ModelConfiguration expectedResponse = new ModelConfiguration();

        DataConfiguration dataPipeline = new DataConfiguration();
        Instant startTime = Instant.parse("2017-01-01T10:00:00Z");
        dataPipeline.addSchemasItem(SchemasEnum.AUTHENTICATION);
        dataPipeline.setStartTime(startTime);
        SystemConfiguration system = new SystemConfiguration();
        system.adminGroup("presidio-admins-somecompany");
        system.analystGroup("presidio-soc-team-somecompany");
        system.setKdcUrl("string");
        system.setSmtpHost("name.of-server.com:25");
        system.setUsername("presidio@somecompany.dom");
        system.setPassword("password");
        expectedResponse.setSystem(system);
        expectedResponse.setDataPipeline(dataPipeline);

        mockRestServiceServer.expect(requestTo("http://localhost:8888/application-presidio")).andExpect(method(HttpMethod.PUT)).andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON)).andRespond(withSuccess());
        mockRestServiceServer.expect(requestTo("http://localhost:8888/workflows-default.json")).andExpect(method(HttpMethod.GET)).andRespond(withSuccess());
        // add schema with patch
        managerConfigurationMVC.perform(request(HttpMethod.PATCH, CONFIGURATION_URI)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(CONFIGURATION_PATCH_REQUEST)).andExpect(status().is(201)).andReturn();
    }


    @Configuration
    @Import(ManagerWebappConfiguration.class)
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer configurationApiControllerSpringTestPlaceholder() {
            Properties properties = new Properties();
            properties.put("spring.profiles.active", "default");
            properties.put("keytab.file.path", "");
            properties.put("manager.dags.dag_id.fullFlow.prefix", "full_flow");
            properties.put("manager.dags.state.buildingBaselineDuration", "P30D");
            properties.put("spring.cloud.config.uri", "http://localhost:8888");
            properties.put("spring.cloud.config.username", "config");
            properties.put("spring.cloud.config.password", "secure");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
