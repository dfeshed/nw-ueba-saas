package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Severity;
import fortscale.domain.core.User;
import fortscale.domain.rest.UserFilter;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.*;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by alexp on 09/08/2016.
 */
@Service("userWithAlertService") public class UserWithAlertServiceImpl implements UserWithAlertService {

    private static Logger logger = Logger.getLogger(UserWithAlertService.class);

	@Autowired private UserService userService;

	@Autowired private AlertsService alertsService;

	@Autowired
	private UserActivityService userActivityService;

	@Autowired
	private UserScoreService userScoreService;

	@Override public List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest) {
		List<User> result = new ArrayList<>();
		Set<String> relevantUsers = getIntersectedUserNameList(userRestFilter);

		if (userRestFilter.getSeverity()!= null){
			Double[] range = userScoreService.getSeverityRange().get(userRestFilter.getSeverity());
			userRestFilter.setMinScore(range[0]);
			userRestFilter.setMaxScore(range[1]);
		}

		if (shouldStop(userRestFilter, relevantUsers)) {
			return result;
		}

		result = userService.findUsersByFilter(userRestFilter, pageRequest, relevantUsers);
		return result;
	}

	/**
	 * If one of the filters (anomaly type, alert type or location) was passed to the rest
	 * and the user name collection we got from it is empty there is no need to continue with the logic
	 * @param userRestFilter
	 * @param relevantUsers
	 * @return
	 */
	private boolean shouldStop(UserRestFilter userRestFilter, Set<String> relevantUsers) {
		return (CollectionUtils.isNotEmpty(userRestFilter.getAnomalyTypesAsSet()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getLocations()))
				&& (CollectionUtils.isEmpty(relevantUsers));
	}

	private Set<String> getIntersectedUserNameList(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = null;

		if (CollectionUtils.isNotEmpty(userRestFilter.getAnomalyTypesAsSet())
				|| CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes())) {
			relevantUsers = alertsService.getDistinctUserNamesByUserFilter(userRestFilter);
		}

		if (CollectionUtils.isNotEmpty(userRestFilter.getLocations())){
			Set<String> userNamesByUserLocation = userActivityService.getUserNamesByUserLocation(userRestFilter.getLocations());
			if (relevantUsers == null){
				relevantUsers = userNamesByUserLocation;
			}else{
				relevantUsers = new HashSet<>(CollectionUtils.intersection(relevantUsers, userNamesByUserLocation));
			}
		}

		return relevantUsers;
	}

	@Override public int countUsersByFilter(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = getIntersectedUserNameList(userRestFilter);

		if (shouldStop(userRestFilter, relevantUsers)) {
			return 0;
		}

		return userService.countUsersByFilter(userRestFilter, relevantUsers);
	}

    @Override
    public void recalculateNumberOfUserAlertsByUserName(String userName) {
        User user = userService.findByUsername(userName);
        updateAlertsCount(user);
    }

    private void updateAlertsCount(User user) {
        if (user != null) {
            List<Alert> alerts = alertsService.getOpenAlertsByUsername(user.getUsername());

            user.setAlertsCount(alerts.size());
            userService.saveUser(user);
        }else{
            logger.error("Got update alert count request for non existing user");
        }
    }

    @Override
    public void recalculateNumberOfUserAlertsByUserId(String userId) {
        User user = userService.getUserById(userId);
        updateAlertsCount(user);
    }
}
