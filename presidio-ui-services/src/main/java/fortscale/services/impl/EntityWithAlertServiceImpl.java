package fortscale.services.impl;


import fortscale.domain.core.Entity;
import fortscale.domain.rest.EntityRestFilter;
import fortscale.services.*;
import fortscale.services.cache.CacheHandler;
import fortscale.temp.HardCodedMocks;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by alexp on 09/08/2016.
 */
@Service("userWithAlertService") public class EntityWithAlertServiceImpl implements EntityWithAlertService {

    private static Logger logger = Logger.getLogger(EntityWithAlertService.class);

	private EntityService entityService;

	private AlertsService alertsService;

//	@Autowired()
//	@Qualifier("filterToUsersCache")
	private CacheHandler<EntityRestFilter, List<Entity>> filterToUsersCache;


	private List<String> fieldsRequired;

	public EntityWithAlertServiceImpl(EntityService entityService, AlertsService alertsService,
                                      CacheHandler<EntityRestFilter, List<Entity>> filterToUsersCache) {

		this.entityService = entityService;
		this.alertsService = alertsService;

		fieldsRequired = new ArrayList<>();
		fieldsRequired.add(Entity.ID_FIELD);
		fieldsRequired.add(Entity.usernameField);
		fieldsRequired.add(Entity.followedField);
		fieldsRequired.add(Entity.displayNameField);
	}



	@Override public List<Entity> findEntitiesByFilter(EntityRestFilter userRestFilter, PageRequest pageRequest, List<String> fieldsRequired, boolean fetchUserslerts) {
		List<Entity> result = new ArrayList<>();

		Set<String> relevantUsers = filterPreparations(userRestFilter);

		if (!shouldStop(userRestFilter, relevantUsers)) {
			result = entityService.findEntitiesByFilter(userRestFilter, pageRequest, relevantUsers, fieldsRequired,fetchUserslerts).getEntities();
		}

		return result;
	}

	private Set<String> filterPreparations(EntityRestFilter userRestFilter) {
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
	private boolean shouldStop(EntityRestFilter userRestFilter, Set<String> relevantUsers) {
		return (CollectionUtils.isNotEmpty(userRestFilter.getIndicatorTypes()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getAlertTypes()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getLocations()) ||
				CollectionUtils.isNotEmpty(userRestFilter.getUserIds()) )
				&& (CollectionUtils.isEmpty(relevantUsers));
	}


	@Override public int countEntitiesByFilter(EntityRestFilter userRestFilter) {
		Set<String> relevantUsers = filterPreparations(userRestFilter);

		if (shouldStop(userRestFilter, relevantUsers)) {
			return HardCodedMocks.DEFAULT_USER_COUNT;
		}

		return entityService.countEntitiesByFilter(userRestFilter, relevantUsers);
	}

	@Override
	public int updateTags(EntityRestFilter userRestFilter, Boolean addTag, List<String> tagNames) throws Exception {

		return 0;
	}

	@Override
	public int followEntitiesByFilter(EntityRestFilter userRestFilter, Boolean watch) {
		// Creating the filter
		entityService.updateWatched(userRestFilter, null, watch);

		return 0;
	}


}
