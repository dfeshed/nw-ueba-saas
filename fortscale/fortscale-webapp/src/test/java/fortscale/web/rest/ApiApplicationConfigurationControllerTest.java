package fortscale.web.rest;

import fortscale.domain.ad.AdConnection;
import fortscale.domain.core.ApplicationConfiguration;
import fortscale.entity.event.IEntityEventSender;
import fortscale.services.ApplicationConfigurationService;
import fortscale.utils.EncryptionUtils;
import net.minidev.json.JSONObject;
import org.junit.*;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ApiApplicationConfigurationControllerTest {


    @Mock
    private ApplicationConfigurationService applicationConfigurationService;

    @InjectMocks
    private ApiApplicationConfigurationController controller;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

    }

    @After
    public void tearDown() throws Exception {

    }

    private MockMvc mockMvc;


    @Test
    public void testGetConfigurations() throws Exception {
        // set up alerts repository mocked behavior
        List<ApplicationConfiguration> applicationConfigurationList = new ArrayList<>();

        applicationConfigurationList.add(new ApplicationConfiguration("test", "test"));
        applicationConfigurationList.add(new ApplicationConfiguration("test2", "test2"));


        when(applicationConfigurationService.getApplicationConfiguration()).thenReturn(applicationConfigurationList);

        // perform rest call to the controller
        MvcResult result = mockMvc.perform(get("/api/application_configuration").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andReturn();

        //validate
        assertTrue(result.getResponse().getContentAsString()
                .contains("{\"id\":null,\"key\":\"test\",\"value\":\"test\"},{\"id\":null,\"key\":\"test2\",\"value\":\"test2\"}]"));

    }

    @Test
    public void testUpdateConfigItems() throws Exception {

        // When provided with a bad JSON it should return ResponseEntity with BAD_REQUEST
        String badJSON = "{bad json";
        assert (controller.updateConfigItems(badJSON).getStatusCode() == HttpStatus.BAD_REQUEST);

        // When provided with JSON that has no "items" list, it should return ResponseEntity with BAD_REQUEST
        String noItemsJSON = "{\"noItems\": []}";
        assert (controller.updateConfigItems(noItemsJSON).getStatusCode() == HttpStatus.BAD_REQUEST);

        // When provided with JSON that has "items" that is not a list, it should return ResponseEntity with BAD_REQUEST
        String itemsNoList = "{\"items\": {}}";
        assert (controller.updateConfigItems(itemsNoList).getStatusCode() == HttpStatus.BAD_REQUEST);

        // When provided with JSON that has "items" with objects without "key" property, it should return ResponseEntity with BAD_REQUEST
        String itemsNoKey = "{\"items\": [{}]}";
        assert (controller.updateConfigItems(itemsNoKey).getStatusCode() == HttpStatus.BAD_REQUEST);

        // When provided with JSON that has "items" with objects without "key" property, it should return ResponseEntity with BAD_REQUEST
        String itemsNoValue = "{\"items\": [{\"key\": \"some key\"}]}";
        assert (controller.updateConfigItems(itemsNoValue).getStatusCode() == HttpStatus.BAD_REQUEST);

        // When provided with valid body, the configItems Map sent to applicationConfigurationService should be valid
        Map<String, String> configItems = new HashMap<>();
        configItems.put("someKey", "some value");
        String validString = "{\"items\": [{\"key\": \"someKey\", \"value\": \"some value\"}]}";
        controller.updateConfigItems(validString);
        verify(applicationConfigurationService).updateConfigItems(configItems);

        // When provided with valid body, the REST should return no-content
        mockMvc.perform(post("/api/application_configuration").content(validString))
                .andExpect(status().isNoContent());

    }

    @Test
    public void testActiveDirectoryUpdate(){
        List<AdConnection> activeDirectoryConfigurations = new ArrayList<>();


        final String DOMAIN_PASSWORD="password";
        final String DC_NAME1="aaa";
        final String DC_NAME2="bbb";
        final String DOMAIN_BASE_SEARCH="search";
        final String DOMAIN_USER_NAME="user@user.com";
        final String ENCRYPTED_PASSWORD = "8bGagpbfO0hLMjKwrIc5SA==";




        AdConnection settings = new AdConnection();
        settings.setDomainPassword(DOMAIN_PASSWORD);
        settings.setDcs(Arrays.asList(DC_NAME1,DC_NAME2));
        settings.setDomainBaseSearch(DOMAIN_BASE_SEARCH);
        settings.setDomainUser(DOMAIN_USER_NAME);
        activeDirectoryConfigurations.add(settings);

        controller.updateActiveDirectory(activeDirectoryConfigurations);

        ArgumentCaptor<String> argumentKey = ArgumentCaptor.forClass(String.class);

        Class<List<AdConnection>> adConnectionListClass = (Class<List<AdConnection>>) Collections.<AdConnection>emptyList().getClass();
        ArgumentCaptor<List<AdConnection>> argumentValue = ArgumentCaptor.forClass(adConnectionListClass);

        verify(applicationConfigurationService, times(1)).updateConfigItemAsObject(argumentKey.capture(), argumentValue.capture());

        Assert.assertEquals("system.activeDirectory.settings",argumentKey.getValue());
        Assert.assertEquals(1,argumentValue.getValue().size()); //Check that we have 1 connection in string

        AdConnection argumentConnection1 = argumentValue.getValue().get(0);

        Assert.assertEquals(2,argumentConnection1.getDcs().size());

        Assert.assertEquals(DOMAIN_BASE_SEARCH,argumentConnection1.getDomainBaseSearch());

        Assert.assertEquals(DOMAIN_USER_NAME,argumentConnection1.getDomainUser());
        Assert.assertEquals(ENCRYPTED_PASSWORD,argumentConnection1.getDomainPassword());


    }

    @Test
    public void testActiveDirectoryUpdate_encrypt_password(){

        final String DOMAIN_PASSWORD="password";
        final String DC_NAME1="aaa";
        final String DC_NAME2="bbb";
        final String DOMAIN_BASE_SEARCH="search";
        final String DOMAIN_USER_NAME="user@domain.com";
        final String ENCRYPTED_PASSWORD = "8bGagpbfO0hLMjKwrIc5SA==";

        AdConnection oldSettings = new AdConnection();
        oldSettings.setDomainPassword(DOMAIN_PASSWORD+"1111");
        oldSettings.setDomainUser(DOMAIN_USER_NAME);

        Mockito.when(applicationConfigurationService.getApplicationConfigurationAsObjects("system.activeDirectory.settings", AdConnection.class)).thenReturn(Arrays.asList(oldSettings));


        AdConnection settings = new AdConnection();
        settings.setDomainPassword(DOMAIN_PASSWORD);
        settings.setDcs(Arrays.asList(DC_NAME1,DC_NAME2));
        settings.setDomainBaseSearch(DOMAIN_BASE_SEARCH);
        settings.setDomainUser(DOMAIN_USER_NAME);
        List<AdConnection> activeDirectoryConfigurations = new ArrayList<>();
        activeDirectoryConfigurations.add(settings);

        controller.updateActiveDirectory(activeDirectoryConfigurations);

        ArgumentCaptor<String> argumentKey = ArgumentCaptor.forClass(String.class);

        Class<List<AdConnection>> adConnectionListClass = (Class<List<AdConnection>>) Collections.<AdConnection>emptyList().getClass();
        ArgumentCaptor<List<AdConnection>> argumentValue = ArgumentCaptor.forClass(adConnectionListClass);

        verify(applicationConfigurationService, times(1)).updateConfigItemAsObject(argumentKey.capture(), argumentValue.capture());

        AdConnection argumentConnection1 = argumentValue.getValue().get(0);

        Assert.assertEquals(ENCRYPTED_PASSWORD,argumentConnection1.getDomainPassword());


    }

    @Test
    public void testActiveDirectoryUpdate_do_not_encrypt_password(){


        final String DC_NAME1="aaa";
        final String DC_NAME2="bbb";
        final String DOMAIN_BASE_SEARCH="search";
        final String DOMAIN_USER_NAME="user@domain.com";
        final String ENCRYPTED_PASSWORD = "ENCRYPTED_PASSWORD";

        AdConnection oldSettings = new AdConnection();
        oldSettings.setDomainPassword(ENCRYPTED_PASSWORD);
        oldSettings.setDomainUser(DOMAIN_USER_NAME);

        Mockito.when(applicationConfigurationService.getApplicationConfigurationAsObjects("system.activeDirectory.settings", AdConnection.class)).thenReturn(Arrays.asList(oldSettings));


        AdConnection settings = new AdConnection();
        settings.setDomainPassword(ENCRYPTED_PASSWORD);
        settings.setDcs(Arrays.asList(DC_NAME1,DC_NAME2));
        settings.setDomainBaseSearch(DOMAIN_BASE_SEARCH);
        settings.setDomainUser(DOMAIN_USER_NAME);
        List<AdConnection> activeDirectoryConfigurations = new ArrayList<>();
        activeDirectoryConfigurations.add(settings);

        controller.updateActiveDirectory(activeDirectoryConfigurations);

        ArgumentCaptor<String> argumentKey = ArgumentCaptor.forClass(String.class);

        Class<List<AdConnection>> adConnectionListClass = (Class<List<AdConnection>>) Collections.<AdConnection>emptyList().getClass();
        ArgumentCaptor<List<AdConnection>> argumentValue = ArgumentCaptor.forClass(adConnectionListClass);

        verify(applicationConfigurationService, times(1)).updateConfigItemAsObject(argumentKey.capture(), argumentValue.capture());

        AdConnection argumentConnection1 = argumentValue.getValue().get(0);

        Assert.assertEquals(ENCRYPTED_PASSWORD,argumentConnection1.getDomainPassword());


    }

}
