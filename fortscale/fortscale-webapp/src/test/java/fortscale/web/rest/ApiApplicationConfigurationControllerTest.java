package fortscale.web.rest;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

}
