package webapp.controller.configuration;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.RestTemplateConfig;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import presidio.webapp.controller.configuration.ConfigurationApi;
import presidio.webapp.spring.ManagerWebappConfiguration;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {ManagerWebappConfiguration.class, RestTemplateConfig.class})
@Category(ModuleTestCategory.class)
public class ConfigurationApiModuleTest {
    private static final String CONFIGURATION_URI = "/configuration";

    private MockMvc configurationApiMVC;

    @Autowired
    private ConfigurationApi configurationApi;

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;


    private ObjectMapper objectMapper;


    @Before
    public void setup() {
        //starting up the webapp server
        configurationApiMVC = MockMvcBuilders.standaloneSetup(configurationApi).setMessageConverters(mappingJackson2HttpMessageConverter).build();
        this.objectMapper = ObjectMapperProvider.customJsonObjectMapper();
    }


}
