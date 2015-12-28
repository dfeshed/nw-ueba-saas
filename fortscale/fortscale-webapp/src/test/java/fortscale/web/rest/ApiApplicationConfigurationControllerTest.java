package fortscale.web.rest;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.services.ApplicationConfigurationService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.junit.Assert.assertTrue;


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
        List<ApplicationConfiguration> applicationConfigurationList = new ArrayList<ApplicationConfiguration>();

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
                .contains("{\"key\":\"test\",\"value\":\"test\"},{\"key\":\"test2\",\"value\":\"test2\"}]"));

    }
}