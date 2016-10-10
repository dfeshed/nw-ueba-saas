package fortscale.services.impl;

import fortscale.domain.core.Alert;
import fortscale.domain.core.Tag;
import fortscale.domain.core.User;
import fortscale.domain.core.UserAdInfo;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.*;
import fortscale.services.cache.CacheHandler;
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

	@Autowired()
	@Qualifier("filterToUsersCache")
	private CacheHandler<UserRestFilter, List<User>> filterToUsersCache;

	@Autowired
	private TagService tagService;

	private List<String> fieldsRequired;

	public UserWithAlertServiceImpl() {
		fieldsRequired = new ArrayList<>();
		fieldsRequired.add(User.ID_FIELD);
		fieldsRequired.add(User.getAdInfoField(UserAdInfo.firstnameField));
		fieldsRequired.add(User.getAdInfoField(UserAdInfo.lastnameField));
		fieldsRequired.add(User.getAdInfoField(UserAdInfo.positionField));
		fieldsRequired.add(User.getAdInfoField(UserAdInfo.departmentField));
		fieldsRequired.add(User.getAdInfoField(UserAdInfo.objectGUIDField));
		fieldsRequired.add(User.usernameField);
		fieldsRequired.add(User.followedField);
		fieldsRequired.add(User.displayNameField);
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
		Set<String> relevantUsers = getIntersectedUserIdList(userRestFilter);

		calculateScoreRange(userRestFilter);

		return relevantUsers;
	}

	/**
	 * Getting the min and max score for user according to the severity required
	 * @param userRestFilter
	 */
	private void calculateScoreRange(UserRestFilter userRestFilter) {
		if (userRestFilter.getSeverity()!= null){
			Double[] range = userScoreService.getSeverityRange().get(userRestFilter.getSeverity());
			userRestFilter.setMinScore(range[0]);
			userRestFilter.setMaxScore(range[1]);
		}
	}

	/**
	 * If one of the filters (anomaly type, alert type, location or user ids ) was passed to the rest
	 * and the user ids collection we got from it is empty there is no need to continue with the logic
	 * @param userRestFilter
	 * @param relevantUsers
	 * @return
	 */
	private boolean shouldStop(UserRestFilter userRestFilter, Set<String> relevantUsers) {
		return (CollectionUtils.isNotEmpty(userRestFilter.getAnomalyTypesAsSet()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getLocations()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getUserIds()))
				&& (CollectionUtils.isEmpty(relevantUsers));
	}

	private Set<String> getIntersectedUserIdList(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = null;

		if (CollectionUtils.isNotEmpty(userRestFilter.getAnomalyTypesAsSet())
				|| CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes())) {
			relevantUsers = alertsService.getDistinctUserIdByUserFilter(userRestFilter);
		}

		if (CollectionUtils.isNotEmpty(userRestFilter.getLocations())){
			Set<String> userIdByUserLocation = userActivityService.getUserIdByUserLocation(userRestFilter.getLocations());
			if (relevantUsers == null){
				relevantUsers = userIdByUserLocation;
			}else{
				relevantUsers = new HashSet<>(CollectionUtils.intersection(relevantUsers, userIdByUserLocation));
			}
		}

		if (CollectionUtils.isNotEmpty(userRestFilter.getUserIds())){
			if (relevantUsers == null){
				relevantUsers = new HashSet<>(userRestFilter.getUserIds());
			}else{
				relevantUsers = new HashSet<>(CollectionUtils.intersection(relevantUsers, userRestFilter.getUserIds()));
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
	public List<User> findFromCacheUsersByFilter(UserRestFilter userRestFilter) {
		// Update the min and max score on the filter according to the severity required
		if (userRestFilter.getSeverity() != null) {
			calculateScoreRange(userRestFilter);
		}

		List<User> users = filterToUsersCache.get(userRestFilter);
		List<User> result = new ArrayList<>();

		if (users == null){
			users = findUsersByFilter(userRestFilter, null, fieldsRequired);
			filterToUsersCache.put(userRestFilter, users);
		}

		String searchValue = userRestFilter.getSearchValue().toLowerCase();

		if (CollectionUtils.isNotEmpty(users)){
			List<User> firstNameResults = new ArrayList<>();
			List<User> lastNameResults = new ArrayList<>();
			List<User> displayNameResults = new ArrayList<>();
			List<User> usernameResults = new ArrayList<>();
			List<User> positionResults = new ArrayList<>();
			List<User> departmentResults = new ArrayList<>();

			users.forEach(user -> {
				if (StringUtils.isNotEmpty(user.getAdInfo().getFirstname())
						&& (user.getAdInfo().getFirstname().toLowerCase().startsWith(searchValue))){
					firstNameResults.add(user);
				} else if (StringUtils.isNotEmpty(user.getAdInfo().getLastname())
						&& (user.getAdInfo().getLastname().toLowerCase().startsWith(searchValue))){
					lastNameResults.add(user);
				} else if (StringUtils.isNotEmpty(user.getDisplayName()) && (user.getDisplayName().toLowerCase().startsWith(searchValue))){
					displayNameResults.add(user);
				} else if (StringUtils.isNotEmpty(user.getUsername())
						&& (user.getUsername().toLowerCase().startsWith(searchValue))){
					usernameResults.add(user);
				} else if (StringUtils.isNotEmpty(user.getAdInfo().getPosition())
						&& (user.getAdInfo().getPosition().toLowerCase().startsWith(searchValue))){
					positionResults.add(user);
				} else if(StringUtils.isNotEmpty(user.getAdInfo().getDepartment())
						&& (user.getAdInfo().getDepartment().toLowerCase().startsWith(searchValue))){
					departmentResults.add(user);
				}
			});

			result.addAll(firstNameResults);
			result.addAll(lastNameResults);
			result.addAll(displayNameResults);
			result.addAll(usernameResults);
			result.addAll(positionResults);
			result.addAll(departmentResults);
		}

		// Extracting only the required users according to page size and number
		if (userRestFilter.getSize()!= null && CollectionUtils.isNotEmpty(result)){
			int startFrom = 0;

			if (userRestFilter.getFromPage() != null && userRestFilter.getFromPage() > 1){
				startFrom = userRestFilter.getSize() * (userRestFilter.getFromPage() - 1);
			}

			int endIndex = startFrom + userRestFilter.getSize();

			if (endIndex > result.size()){
				endIndex = result.size();
			}

			return result.subList(startFrom, endIndex);
		}

		return result;
	}

	@Override
	public int updateTags(UserRestFilter userRestFilter, Boolean addTag, List<String> tagNames) throws Exception {

		// Create tag if needed
		if (addTag) {
			for (String tag : tagNames) {
				//if there's no such tag in the system
				if (tagService.getTag(tag) == null) {
					//try to add the new tag
					if (!tagService.addTag(new Tag(tag))) {
						//if failed
						throw new Exception("failed to add new tag - " + tag);
					}
				}
			}
		}

		// Creating the filter
		Set<String> relevantUsers = filterPreparations(userRestFilter);
		if (shouldStop(userRestFilter, relevantUsers)) {
			return 0;
		}

		userService.updateTags(userRestFilter, addTag, tagNames, relevantUsers);
		return countUsersByFilter(userRestFilter);
	}
}
