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

import java.util.*;

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
		user1.put("username", "user1");
		Map<String, String> user2 = new HashMap<>();
		user2.put("id2", "user2");
		user2.put("username", "user2");
		entitiesMap.add(user1);
		entitiesMap.add(user2);

		when(usersDao.getUsersByPrefix(anyString(), any(PageRequest.class))).thenReturn(entitiesMap);

		Map<String, Integer> entitiesCount = new HashMap<>();
		entitiesCount.put("user1",3);
		entitiesCount.put("user2",1);
		when(usersDao.countUsersByDisplayName(any(Set.class))).thenReturn(entitiesCount);

		MvcResult result = mockMvc.perform(get("/api/entities?entity_name=user").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentType("application/json;charset=UTF-8"))
				.andReturn();

		//validate
		String user2UserNameExpectedJson = "\"username\":\"user2\"";

		String user2uniqueDisplayNameExpectedJson = "\"uniqueDisplayName\":\"user2\"";
		assertTrue(result.getResponse().getContentAsString().contains(user2UserNameExpectedJson));
		assertTrue(result.getResponse().getContentAsString().contains(user2uniqueDisplayNameExpectedJson));
		verify(usersDao).getUsersByPrefix(anyString(), any(PageRequest.class));
	}

}
