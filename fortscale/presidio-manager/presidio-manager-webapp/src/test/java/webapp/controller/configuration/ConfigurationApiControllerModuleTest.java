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
import org.springframework.test.web.client.ExpectedCount;
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

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
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
    private static final String PRESIDIO_APPLICATION_CONFIG_SERVER_URI = "http://localhost:8888/application-presidio";
    private static final String CONFIG_SERVER_WORKFLOWS_URI = "http://localhost:8888/workflows-default.json";
    private static final String CONFIG_SERVER_PRESIDIO_APPLICATION_JSON_URI = "http://localhost:8888/application-presidio-default.json";

    private MockRestServiceServer mockRestServiceServer;
    private MockMvc managerConfigurationMVC;

    @Autowired
    private ConfigurationApi configurationApi;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    private final String AIRFLOW_CONFIGURATION_RESPONSE =
            "{\"dataPipeline\":{\"schemas\":[],\"startTime\":\"2017-01-01T10:00:00Z\"},\"system\":{\"analystGroup\":\"presidio-soc-team-somecompany\",\"ldapUrl\":\"string\",\"password\":\"password\",\"smtpHost\":\"name.of-server.com:25\",\"username\":\"presidio@somecompany.dom\",\"realmName\":\"EXAMPLE.COM\"}}";
    private final String CONFIGURATION_PATCH_REQUEST =
            "[\n" +
                    "  {\n" +
                    "    \"op\": \"add\",\n" +
                    "    \"path\": \"/dataPipeline/schemas\",\n" +
                    "    \"value\": [\"AUTHENTICATION\"]\n" +
                    "  }\n" +
                    "]";
    private final String WORKFLOWS_CONFIG_RESPONSE = "{\"components\":{\"adapter\":{\"jvm_args\":{\"jar_path\":\"/home/presidio/presidio-core/bin/presidio-adapter-1.0.0-SNAPSHOT.jar\",\"main_class\":\"presidio.adapter.FortscaleAdapterApplication\"}},\"input\":{\"jvm_args\":{\"jar_path\":\"/home/presidio/presidio-core/bin/presidio-input-core-1.0.0-SNAPSHOT.jar\",\"main_class\":\"presidio.input.core.FortscaleInputCoreApplication\",\"xms\":70,\"xmx\":500}},\"output\":{\"jvm_args\":{\"jar_path\":\"/home/presidio/presidio-core/bin/presidio-output-processor-1.0.0-SNAPSHOT.jar\",\"main_class\":\"presidio.output.processor.FortscaleOutputProcessorApplication\",\"xms\":70,\"xmx\":500}}},\"dags\":{\"dags_configs\":[{\"args\":{\"command\":\"run\",\"data_sources\":\"\",\"hourly_smart_events_confs\":[\"userId_hourly\"]},\"dag_id\":\"full_flow\",\"schedule_interval\":\"timedelta(hours=1)\",\"start_date\":\"2017-01-01 08:00:00\"}],\"operators\":{\"default_jar_values\":{\"java_path\":\"/usr/bin/java\",\"jvm_args\":{\"jmx_enabled\":false,\"remote_debug_enabled\":false,\"remote_debug_suspend\":false,\"timezone\":\"-Duser.timezone=UTC\",\"xms\":100,\"xmx\":2048}}},\"tasks_instances\":{}},\"elasticsearch\":{\"clustername\":\"fortscale\",\"host\":\"localhost\",\"port\":\"9300\"},\"general\":{\"deployment\":{\"bins\":{\"base_path\":\"/home/presidio/presidio-core/bin\"}}},\"mongo\":{\"db\":{\"name\":\"presidio\",\"password\":\"iYTLjyA0VryKhpkvBrMMLQ==\",\"user\":\"presidio\"},\"host\":{\"name\":\"localhost\",\"port\":\"27017\"},\"map\":{\"dollar\":{\"replacement\":\"#dlr#\"},\"dot\":{\"replacement\":\"#dot#\"}}},\"monitoring\":{\"fixed\":{\"rate\":\"60000\"}},\"spring\":{\"aop\":{\"proxy\":{\"target\":{\"class\":\"true\"}}},\"autoconfigure\":{\"exclude\":\"org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration\"}}}";

    private ObjectMapper objectMapper;

    @Before
    public void setup() {
        managerConfigurationMVC = MockMvcBuilders.standaloneSetup(configurationApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();

        mockRestServiceServer = MockRestServiceServer.createServer(restTemplate);
        mockRestServiceServer.reset();
        mockRestServiceServer.expect(ExpectedCount.min(1),requestTo(CONFIG_SERVER_PRESIDIO_APPLICATION_JSON_URI))
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
        system.setRealmName("EXAMPLE.COM");
        system.analystGroup("presidio-soc-team-somecompany");
        system.setLdapUrl("string");
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
        MvcResult mvcResult = managerConfigurationMVC.perform(get(CONFIGURATION_URI))
                .andExpect(status().isOk())
                .andReturn();
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
        system.setRealmName("EXAMPLE.COM");
        system.analystGroup("presidio-soc-team-somecompany");
        system.setLdapUrl("string");
        system.setSmtpHost("name.of-server.com:25");
        system.setUsername("presidio@somecompany.dom");
        system.setPassword("password");
        expectedResponse.setSystem(system);
        expectedResponse.setDataPipeline(dataPipeline);

        mockRestServiceServer.expect(requestTo(PRESIDIO_APPLICATION_CONFIG_SERVER_URI))
                .andExpect(method(HttpMethod.PUT))
                .andExpect(MockRestRequestMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.dataPipeline.schemas[0]")
                        .value(equalTo(SchemasEnum.AUTHENTICATION.toString())))
                .andRespond(withSuccess());
        mockRestServiceServer.expect(requestTo(CONFIG_SERVER_WORKFLOWS_URI))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(WORKFLOWS_CONFIG_RESPONSE, MediaType.APPLICATION_JSON_UTF8));
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
            properties.put("presidio.workflows.config.path", "/tmp");
            properties.put("manager.security.securityConfPath","/tmp/httpd.conf");
            properties.put("manager.security.krb5ConfPath","/tmp/krb5.conf");
            properties.put("spring.cloud.config.username", "config");
            properties.put("spring.cloud.config.password", "secure");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
