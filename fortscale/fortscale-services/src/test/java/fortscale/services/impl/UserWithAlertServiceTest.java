package fortscale.services.impl;

import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.User;
import fortscale.domain.rest.AlertRestFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.AlertsService;
import fortscale.services.UserActivityService;
import fortscale.services.UserService;
import junitparams.JUnitParamsRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;

/**
 * Created by alexp on 15/08/2016.
 */
@RunWith(JUnitParamsRunner.class) public class UserWithAlertServiceTest {

	@Mock private UserService userService;

	@Mock private AlertsService alertsService;

	@Mock private UserActivityService userActivityService;

	@InjectMocks private UserWithAlertServiceImpl userWithAlertService;

	@Before public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test public void testGetUsers_NoFilter() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, null)).thenReturn(users);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(1, result.size());
	}

	@Test public void testGetUsers_AlertTypeFilter_userFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();
		List<String> alertTypes = new ArrayList<>();
		alertTypes.add("anomalous_admin_activity_normalized_username_hourly");
		userRestFilter.setAlertTypes(alertTypes);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);
		Set<String> userNames = new HashSet<>();
		userNames.add("user");
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(1, result.size());
	}

	@Test public void testGetUsers_AlertTypeFilter_NoUserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();
		List<String> alertTypes = new ArrayList<>();
		alertTypes.add("anomalous_admin_activity_normalized_username_hourly");
		userRestFilter.setAlertTypes(alertTypes);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);
		Set<String> userNames = new HashSet<>();
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(0, result.size());
	}

	@Test public void testGetUsers_IndicatorTypeFilter_UserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();
		AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes = new AlertRestFilter.DataSourceAnomalyTypePairListWrapper();
		Set<DataSourceAnomalyTypePair> anomalyList = new HashSet<>();
		DataSourceAnomalyTypePair anomalyPair = new DataSourceAnomalyTypePair("oracle", "number_of_failed_oracle_hourly");
		anomalyList.add(anomalyPair);
		indicatorTypes.setAnomalyList(anomalyList);
		userRestFilter.setIndicatorTypes(indicatorTypes);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		userNames.add("user");
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(1, result.size());
	}

	@Test public void testGetUsers_IndicatorTypeFilter_NoUserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();
		AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes = new AlertRestFilter.DataSourceAnomalyTypePairListWrapper();
		Set<DataSourceAnomalyTypePair> anomalyList = new HashSet<>();
		DataSourceAnomalyTypePair anomalyPair = new DataSourceAnomalyTypePair("oracle", "number_of_failed_oracle_hourly");
		anomalyList.add(anomalyPair);
		indicatorTypes.setAnomalyList(anomalyList);
		userRestFilter.setIndicatorTypes(indicatorTypes);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(0, result.size());
	}

	@Test public void testGetUsers_IndicatorAndAlertTypeFilter_UserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();

		AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes = new AlertRestFilter.DataSourceAnomalyTypePairListWrapper();
		Set<DataSourceAnomalyTypePair> anomalyList = new HashSet<>();
		DataSourceAnomalyTypePair anomalyPair = new DataSourceAnomalyTypePair("oracle", "number_of_failed_oracle_hourly");
		anomalyList.add(anomalyPair);
		indicatorTypes.setAnomalyList(anomalyList);
		userRestFilter.setIndicatorTypes(indicatorTypes);

		List<String> alertTypes = new ArrayList<>();
		alertTypes.add("anomalous_admin_activity_normalized_username_hourly");
		userRestFilter.setAlertTypes(alertTypes);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		userNames.add("user");
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(1, result.size());
	}

	@Test public void testGetUsers_IndicatorAndAlertTypeFilter_NoUserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();

		AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes = new AlertRestFilter.DataSourceAnomalyTypePairListWrapper();
		Set<DataSourceAnomalyTypePair> anomalyList = new HashSet<>();
		DataSourceAnomalyTypePair anomalyPair = new DataSourceAnomalyTypePair("oracle", "number_of_failed_oracle_hourly");
		anomalyList.add(anomalyPair);
		indicatorTypes.setAnomalyList(anomalyList);
		userRestFilter.setIndicatorTypes(indicatorTypes);

		List<String> alertTypes = new ArrayList<>();
		alertTypes.add("anomalous_admin_activity_normalized_username_hourly");
		userRestFilter.setAlertTypes(alertTypes);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(0, result.size());
	}

	@Test public void testGetUsers_LocationFilter_UserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();

		List<String> locations = new ArrayList<>();
		locations.add("Italy");
		userRestFilter.setLocations(locations);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		userNames.add("user");
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		when(userActivityService.getUserNamesByUserLocation(locations)).thenReturn(userNames);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(1, result.size());
	}

	@Test public void testGetUsers_LocationFilter_NoUserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();

		List<String> locations = new ArrayList<>();
		locations.add("Italy");
		userRestFilter.setLocations(locations);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		when(userActivityService.getUserNamesByUserLocation(locations)).thenReturn(userNames);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(0, result.size());
	}

	@Test public void testGetUsers_IndicatorAlertTypeLocationFilter_UserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();

		AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes = new AlertRestFilter.DataSourceAnomalyTypePairListWrapper();
		Set<DataSourceAnomalyTypePair> anomalyList = new HashSet<>();
		DataSourceAnomalyTypePair anomalyPair = new DataSourceAnomalyTypePair("oracle", "number_of_failed_oracle_hourly");
		anomalyList.add(anomalyPair);
		indicatorTypes.setAnomalyList(anomalyList);
		userRestFilter.setIndicatorTypes(indicatorTypes);

		List<String> alertTypes = new ArrayList<>();
		alertTypes.add("anomalous_admin_activity_normalized_username_hourly");
		userRestFilter.setAlertTypes(alertTypes);

		List<String> locations = new ArrayList<>();
		locations.add("Italy");
		userRestFilter.setLocations(locations);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		userNames.add("user");
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		when(userActivityService.getUserNamesByUserLocation(locations)).thenReturn(userNames);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(1, result.size());
	}

	@Test public void testGetUsers_IndicatorAlertTypeLocationFilter_NoUserFound() {
		PageRequest pageRequest = new PageRequest(1, 10);
		UserRestFilter userRestFilter = new UserRestFilter();

		AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes = new AlertRestFilter.DataSourceAnomalyTypePairListWrapper();
		Set<DataSourceAnomalyTypePair> anomalyList = new HashSet<>();
		DataSourceAnomalyTypePair anomalyPair = new DataSourceAnomalyTypePair("oracle", "number_of_failed_oracle_hourly");
		anomalyList.add(anomalyPair);
		indicatorTypes.setAnomalyList(anomalyList);
		userRestFilter.setIndicatorTypes(indicatorTypes);

		List<String> alertTypes = new ArrayList<>();
		alertTypes.add("anomalous_admin_activity_normalized_username_hourly");
		userRestFilter.setAlertTypes(alertTypes);

		List<String> locations = new ArrayList<>();
		locations.add("Italy");
		userRestFilter.setLocations(locations);

		List<User> users = new ArrayList<>();
		User user = new User();
		users.add(user);

		Set<String> userNames = new HashSet<>();
		when(alertsService.getDistinctUserNamesByUserFilter(userRestFilter)).thenReturn(userNames);
		when(userService.findUsersByFilter(userRestFilter, pageRequest, userNames)).thenReturn(users);
		when(userActivityService.getUserNamesByUserLocation(locations)).thenReturn(userNames);
		List<User> result = userWithAlertService.findUsersByFilter(userRestFilter, pageRequest);

		Assert.assertEquals(0, result.size());
	}
}
