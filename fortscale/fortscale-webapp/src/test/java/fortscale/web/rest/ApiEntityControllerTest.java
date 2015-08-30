package fortscale.web.rest;

import fortscale.domain.core.dao.UserRepositoryCustom;
import fortscale.services.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/spring/webapp-application-context-test"})
public class ApiEntityControllerTest {

	@Mock
	private UserService usersDao;

	@InjectMocks
	private ApiEntityController subject;

	private MockMvc mockMvc;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.mockMvc = MockMvcBuilders.standaloneSetup(subject).build();
	}

	@Test
	public void list_entities_by_prefix() throws Exception {
		List<Map<String, String>> entitiesMap = new ArrayList<>();
		Map<String, String> user1 = new HashMap<>();
		user1.put("id1", "user1");
		Map<String, String> user2 = new HashMap<>();
		user2.put("id2", "user2");

		entitiesMap.add(user1);
		entitiesMap.add(user2);

		when(usersDao.getUsersByPrefix(anyString(), any(PageRequest.class))).thenReturn(entitiesMap);

		MvcResult result = mockMvc.perform(get("/api/entities?entity_name=user").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		//validate
		assertTrue(result.getResponse().getContentAsString().contains("{\"id2\":\"user2\"}"));
		verify(usersDao).getUsersByPrefix(anyString(), any(PageRequest.class));
	}

}
