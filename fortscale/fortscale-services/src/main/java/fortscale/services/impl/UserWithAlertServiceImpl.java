package fortscale.services.impl;

import fortscale.common.datastructures.activity.UserActivityData;
import fortscale.domain.core.Alert;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.core.activities.UserActivitySourceMachineDocument;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.*;
import fortscale.services.cache.CacheHandler;
import fortscale.services.util.UserDeviceUtils;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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

	@Autowired
	private UserDeviceUtils userDeviceUtils;

	@Autowired()
	@Qualifier("filterToUsersCache")
	private CacheHandler<UserRestFilter, List<User>> filterToUsersCache;

	List<String> fieldsRequired;

	public UserWithAlertServiceImpl() {
			fieldsRequired = new ArrayList<>();
			fieldsRequired.add(User.ID_FIELD);
			fieldsRequired.add(String.format("%s.%s", User.adInfoField, UserAdInfo.firstnameField));
			fieldsRequired.add(String.format("%s.%s", User.adInfoField, UserAdInfo.lastnameField));
			fieldsRequired.add(String.format("%s.%s", User.adInfoField, UserAdInfo.positionField));
			fieldsRequired.add(String.format("%s.%s", User.adInfoField, UserAdInfo.departmentField));
			fieldsRequired.add(User.usernameField);
	}

	@Override public List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired) {
		List<User> result = new ArrayList<>();

		Set<String> relevantUsers = filterPreparations(userRestFilter);

		if (shouldStop(userRestFilter, relevantUsers)) {
			return result;
		}

		result = userService.findUsersByFilter(userRestFilter, pageRequest, relevantUsers, fieldsRequired);
		return result;
	}

	private Set<String> filterPreparations(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = getIntersectedUserNameList(userRestFilter);

		if (userRestFilter.getSeverity()!= null){
			Double[] range = userScoreService.getSeverityRange().get(userRestFilter.getSeverity());
			userRestFilter.setMinScore(range[0]);
			userRestFilter.setMaxScore(range[1]);
		}
		return relevantUsers;
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
		Set<String> relevantUsers = filterPreparations(userRestFilter);

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

	@Override
	public List<UserActivityData.DeviceEntry> getUserActivitySourceMachineDocuments(User user) {
        List<UserActivitySourceMachineDocument> userSourceMachines;
        List<UserActivityData.DeviceEntry> deviceEntries = null;
        try {
            userSourceMachines = userActivityService.getUserActivitySourceMachineEntries(user.getId(),
                    Integer.MAX_VALUE);
            deviceEntries = userDeviceUtils.convertDeviceDocumentsResponse(userSourceMachines, 3);
        } catch (Exception ex) {
            logger.warn("failed to get user source machines");
            userSourceMachines = new ArrayList<>();
        }
        return deviceEntries;
    }

	@Override
	public List<User> findAndSaveUsersByFilter(UserRestFilter userRestFilter, String searchValue) {
		List<User> users = filterToUsersCache.get(userRestFilter);
		List<User> result = new ArrayList<>();

		if (users == null){
			users = findUsersByFilter(userRestFilter, null, fieldsRequired);
			filterToUsersCache.put(userRestFilter, users);
		}

		if (CollectionUtils.isNotEmpty(users)){
			List<User> firstNameResults = new ArrayList<>();
			List<User> lastNameResults = new ArrayList<>();
			List<User> usernameResults = new ArrayList<>();
			List<User> positionResults = new ArrayList<>();
			List<User> departmentResults = new ArrayList<>();

			users.forEach(user -> {
				if (StringUtils.isNotEmpty(user.getAdInfo().getFirstname())
						&& (user.getAdInfo().getFirstname().startsWith(searchValue))){
					firstNameResults.add(user);
				} else if (StringUtils.isNotEmpty(user.getAdInfo().getLastname())
						&& (user.getAdInfo().getLastname().startsWith(searchValue))){
					lastNameResults.add(user);
				} else if (StringUtils.isNotEmpty(user.getUsername())
						&& (user.getUsername().startsWith(searchValue))){
					usernameResults.add(user);
				} else if (StringUtils.isNotEmpty(user.getAdInfo().getPosition())
						&& (user.getAdInfo().getPosition().startsWith(searchValue))){
					positionResults.add(user);
				} else if(StringUtils.isNotEmpty(user.getAdInfo().getDepartment())
						&& (user.getAdInfo().getDepartment().startsWith(searchValue))){
					departmentResults.add(user);
				}
			});

			result.addAll(firstNameResults);
			result.addAll(lastNameResults);
			result.addAll(usernameResults);
			result.addAll(positionResults);
			result.addAll(departmentResults);
		}

		if (userRestFilter.getSize()!= null){
			int startFrom = 0;

			if (userRestFilter.getFromPage() != null && userRestFilter.getFromPage() > 1){
				startFrom = userRestFilter.getSize() * (userRestFilter.getFromPage() - 1);
			}
			return result.subList(startFrom, startFrom + userRestFilter.getSize());
		}

		return result;

	}

	@Override
	public CacheHandler getCache() {
		return filterToUsersCache;
	}

	@Override
	public void setCache(CacheHandler cache) {
		this.filterToUsersCache = cache;
	}

	@Override
	public void handleNewValue(String key, String value) throws Exception {
		if(value == null){
			getCache().remove(key);
		}
		else {
			getCache().putFromString(key, value);
		}
	}

}
