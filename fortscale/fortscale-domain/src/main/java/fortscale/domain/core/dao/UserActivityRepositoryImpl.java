package fortscale.domain.core.dao;

import fortscale.domain.core.activities.*;
import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Repository("UserActivityRepository")
public class UserActivityRepositoryImpl extends UserActivityBaseRepository implements UserActivityRepositoryCustom {

    private static final Logger logger = Logger.getLogger(UserActivityRepositoryImpl.class);
    
    private static final String COLLECTION_NAME_LOCATION = UserActivityLocationDocument.COLLECTION_NAME;
    private static final String COLLECTION_NAME_NETWORK_AUTHENTICATION = UserActivityNetworkAuthenticationDocument.COLLECTION_NAME;
    private static final String COLLECTION_NAME_WORKING_HOURS = UserActivityWorkingHoursDocument.COLLECTION_NAME;
    public static final String COLLECTION_NAME_ORGANIZATION = OrganizationActivityLocationDocument.COLLECTION_NAME;
    public static final String COLLECTION_NAME_SOURCE_MACHINE = UserActivitySourceMachineDocument.COLLECTION_NAME;
    private static final String COLLECTION_NAME_TARGET_DEVICE = UserActivityTargetDeviceDocument.COLLECTION_NAME;
    private static final String COLLECTION_NAME_DATA_USAGE = UserActivityDataUsageDocument.COLLECTION_NAME;

    @Override
    public List<UserActivityLocationDocument> getUserActivityLocationEntries(String username, int timeRangeInDays) {
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_LOCATION,
				UserActivityLocationDocument.class);
    }

    @Override
    public List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String username,
			int timeRangeInDays) {
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_NETWORK_AUTHENTICATION,
				UserActivityNetworkAuthenticationDocument.class);
    }

    @Override
    public List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays) {
        return getUserActivityEntries(null, timeRangeInDays, COLLECTION_NAME_ORGANIZATION, OrganizationActivityLocationDocument.class);
    }

    public List<UserActivityWorkingHoursDocument> getUserActivityWorkingHoursEntries(String username, int timeRangeInDays) {
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_WORKING_HOURS, UserActivityWorkingHoursDocument.class);
    }
    
    @Override
    public List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String username,
			int timeRangeInDays) {
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_SOURCE_MACHINE,
				UserActivitySourceMachineDocument.class);
    }

    public List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String username, int timeRangeInDays){
        return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_TARGET_DEVICE, UserActivityTargetDeviceDocument.class);
    }

	@Override public Set<String> getUserIdByLocation(List<String> locations) {
		Query query = new Query();
		String fieldName = String.format("%s.%s.", UserActivityLocationDocument.LOCATIONS_FIELD_NAME, UserActivityLocationDocument.COUNTRY_HISTOGRAM_FIELD_NAME);

		if (CollectionUtils.isNotEmpty(locations)) {
			List<Criteria> locationsCriteriaList = new ArrayList<>();
			locations.stream().forEach(location -> locationsCriteriaList.add(new Criteria(fieldName + location).exists(true)));
			query.addCriteria(new Criteria().orOperator(locationsCriteriaList.toArray(new Criteria[locations.size()])));
		}

		List<String> distinctUserNames = mongoTemplate.getCollection(UserActivityLocationDocument.COLLECTION_NAME)
				.distinct(UserActivityLocationDocument.ENTITY_ID_FIELD_NAME, query.getQueryObject());
		return new HashSet<>(distinctUserNames);
	}

	@Override
	public List<UserActivityDataUsageDocument> getUserActivityDataUsageEntries(String username, int timeRangeInDays) {
		return getUserActivityEntries(username, timeRangeInDays, COLLECTION_NAME_DATA_USAGE,
				UserActivityDataUsageDocument.class);
	}

    @Override
    protected Logger getLogger() {
        return logger;
    }

}