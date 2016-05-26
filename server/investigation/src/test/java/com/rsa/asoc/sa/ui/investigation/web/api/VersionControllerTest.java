package com.rsa.asoc.sa.ui.investigation.web.api;

import com.rsa.asoc.sa.ui.common.BuildInformation;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Test for {@link VersionController}.
 *
 * @author athielke
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration
@WebAppConfiguration
@TestPropertySource(properties = {
        "build.date:Wed May 25 16:50:25 EDT 2016",
        "build.commit:1234" })
public class VersionControllerTest {

    @Configuration
    @EnableAutoConfiguration
    static class Config {
        @Bean
        public BuildInformation buildInformation() {
            return new BuildInformation();
        }

        @Bean
        public VersionController versionController() {
            return new VersionController(buildInformation());
        }
    }

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private BuildInformation buildInformation;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testBuildInformation() throws Exception {
        mockMvc.perform(get("/api/info").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.version").value(buildInformation.getVersion()))
                .andExpect(jsonPath("$.commit").value(buildInformation.getCommit()))
                .andExpect(jsonPath("$.changeset").value(buildInformation.getChangeset()))
                .andExpect(jsonPath("$.date").value(buildInformation.getDate().getTime()));
    }
}
