package fortscale.services.impl;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.cache.CacheHandler;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class UsernameServiceTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private EventScoreDAO loginDAO;
	@Mock
	private EventScoreDAO sshDAO;
	@Mock
	private EventScoreDAO vpnDAO;
//	@Mock
//	private EventScoreDAO amtDAO;
	@Mock
	private CacheHandler<String, String> usernameToUserIdCache;

	@InjectMocks
	private UsernameService usernameService;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void should_check_log_username_sets_and_username_to_user_id_cache_were_updated_correctly() {
		// Arrange
		int numOfUsers = 8500;
		int pageSize = 256;

		try { usernameService.afterPropertiesSet(); } catch (Exception e) {}
		List<User> listOfUsers = new ArrayList<>(numOfUsers);
		usernameService.setPageSize(pageSize);

		for (int i = 0; i < numOfUsers; i++) {
			User user = new User();
			user.setUsername("user" + i);
			for (LogEventsEnum value : LogEventsEnum.values())
				user.addLogUsername(value.name(), getDataSourceUsername(value, user));
			listOfUsers.add(user);
		}

		when(userRepository.count()).thenReturn((long)numOfUsers);
		int numOfPages = ((numOfUsers - 1) / pageSize) + 1;
		for (int i = 0; i < numOfPages; i++) {
			PageRequest pageRequest = new PageRequest(i, pageSize);
			int firstIndex = i * pageSize;
			int lastIndex = Math.min((i + 1) * pageSize, numOfUsers);
			List<User> subList = listOfUsers.subList(firstIndex, lastIndex);
			when(userRepository.findAllExcludeAdInfo(pageRequest)).thenReturn(subList);
		}

		when(loginDAO.getTableName()).thenReturn(LogEventsEnum.login.name());
		when(sshDAO.getTableName()).thenReturn(LogEventsEnum.ssh.name());
		when(vpnDAO.getTableName()).thenReturn(LogEventsEnum.vpn.name());
//		when(amtDAO.getTableName()).thenReturn(LogEventsEnum.amt.name());

		// Act
		usernameService.update();

		// Assert
		verify(usernameToUserIdCache, times(1)).clear();
		when(userRepository.findOne(any(String.class))).thenReturn(null);
		for (User user : listOfUsers) {
			verify(usernameToUserIdCache, times(1)).put(user.getUsername(), user.getId());
			for (LogEventsEnum value : LogEventsEnum.values()) {
				if (value != LogEventsEnum.amt && value != LogEventsEnum.amtsession) { // TODO remove!
					assertTrue(usernameService.isLogUsernameExist(value, getDataSourceUsername(value, user), user.getId()));
				}
			}
		}
	}

	private String getDataSourceUsername(LogEventsEnum value, User user) {
		return value.getId() + user.getUsername();
	}
}
