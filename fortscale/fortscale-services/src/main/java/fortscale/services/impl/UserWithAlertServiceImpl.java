package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.AlertsService;
import fortscale.services.UserService;
import fortscale.services.UserWithAlertService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by alexp on 09/08/2016.
 */
@Service("userWithAlertService") public class UserWithAlertServiceImpl implements UserWithAlertService {

	@Autowired private UserService userService;

	@Autowired private AlertsService alertsService;

	@Override public List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest) {
		Set<String> relevantUsers = getIntersectedUserNameList(userRestFilter);

		return userService.findUsersByFilter(userRestFilter, pageRequest, relevantUsers);
	}

	private Set<String> getIntersectedUserNameList(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = null;

		if (CollectionUtils.isNotEmpty(userRestFilter.getAnomalyTypesAsSet()) || CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes())) {
			relevantUsers = alertsService.getDistinctUserNamesByUserFilter(userRestFilter);
		}

		return relevantUsers;
	}

	@Override public int countUsersByFilter(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = getIntersectedUserNameList(userRestFilter);

		return userService.countUsersByFilter(userRestFilter, relevantUsers);
	}

	@Override public void recalculateNumberOfUserAlerts(String userName) {
		Set<Alert> alerts = alertsService.getOpenAlertsByUsername(userName);
		User user = userService.findByUsername(userName);

		user.setAlertsCount(alerts.size());
		userService.saveUser(user);
	}
}
