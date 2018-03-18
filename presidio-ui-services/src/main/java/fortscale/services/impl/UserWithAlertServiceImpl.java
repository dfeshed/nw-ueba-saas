package fortscale.services.impl;


import fortscale.domain.core.User;
import fortscale.domain.rest.UserRestFilter;
import fortscale.services.*;
import fortscale.services.cache.CacheHandler;
import fortscale.temp.HardCodedMocks;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
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




	@Autowired()
	@Qualifier("filterToUsersCache")
	private CacheHandler<UserRestFilter, List<User>> filterToUsersCache;


	private List<String> fieldsRequired;

	public UserWithAlertServiceImpl() {
		fieldsRequired = new ArrayList<>();
		fieldsRequired.add(User.ID_FIELD);
		fieldsRequired.add(User.usernameField);
		fieldsRequired.add(User.followedField);
		fieldsRequired.add(User.displayNameField);
	}

	@Override public List<User> findUsersByFilter(UserRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired,boolean fetchUserslerts) {
		List<User> result = new ArrayList<>();

		Set<String> relevantUsers = filterPreparations(userRestFilter);

		if (!shouldStop(userRestFilter, relevantUsers)) {
			result = userService.findUsersByFilter(userRestFilter, pageRequest, relevantUsers, fieldsRequired,fetchUserslerts).getUsers();
		}

		return result;
	}

	private Set<String> filterPreparations(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = new HashSet<>();

		return relevantUsers;
	}


	/**
	 * If one of the filters (anomaly type, alert type, location, user ids or search value) was passed to the rest
	 * and the user ids collection we got from it is empty there is no need to continue with the logic
	 * @param userRestFilter
	 * @param relevantUsers
	 * @return
	 */
	private boolean shouldStop(UserRestFilter userRestFilter, Set<String> relevantUsers) {
		return (CollectionUtils.isNotEmpty(userRestFilter.getIndicatorTypes()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getLocations()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getUserIds()) )
				&& (CollectionUtils.isEmpty(relevantUsers));
	}


	@Override public int countUsersByFilter(UserRestFilter userRestFilter) {
		Set<String> relevantUsers = filterPreparations(userRestFilter);

		if (shouldStop(userRestFilter, relevantUsers)) {
			return HardCodedMocks.DEFAULT_USER_COUNT;
		}

		return userService.countUsersByFilter(userRestFilter, relevantUsers);
	}

	@Override
	public int updateTags(UserRestFilter userRestFilter, Boolean addTag, List<String> tagNames) throws Exception {

		return 0;
	}

	@Override
	public int followUsersByFilter(UserRestFilter userRestFilter, Boolean watch) {
		// Creating the filter
		userService.updateWatched(userRestFilter, null, watch);

		return 0;
	}


}
