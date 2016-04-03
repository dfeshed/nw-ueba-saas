package fortscale.services.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.dao.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Set;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

public class UserTagsCacheServiceImplTest {

	private String setTagsAsJson = "[\"service\",\"admin\"]";
	private Set<String> setTags;
	private ObjectMapper mapper = new ObjectMapper();

	@Mock
	private UserRepository userRepository;;

	@InjectMocks
	private UserTagsCacheServiceImpl userTagsCacheService ;

	@Before
	public void init()
	{
		MockitoAnnotations.initMocks(this);
		try {
			setTags = mapper.readValue(setTagsAsJson, Set.class);
		}
		catch (Exception e)
		{
			if (e!=null)
				return;
		}

		when(userRepository.getUserTags("testUser")).thenReturn(setTags);

	}

	@Test
	public void testGetUserTags() throws Exception {

		assertTrue(userTagsCacheService.getUserTags("testUser").containsAll(setTags));

	}

	@Test
	public void testAddUserTags() throws Exception {

		userTagsCacheService.addUserTags("testUser",setTags);
		assertTrue(userTagsCacheService.getUserTags("testUser").containsAll(setTags));



	}
}
