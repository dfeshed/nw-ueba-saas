package fortscale.domain.core.dao;

import fortscale.domain.core.OrganizationActivityLocation;
import fortscale.domain.core.UserActivityLocation;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Calendar;
import java.util.List;

@Repository("OrganizationActivityRepository")
public class OrganizationActivityRepositoryImpl  implements OrganizationActivityRepository {

    private static final Logger logger = Logger.getLogger(OrganizationActivityRepositoryImpl.class);

    public static final String COLLECTION_NAME = OrganizationActivityLocation.COLLECTION_NAME;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public OrganizationActivityRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<OrganizationActivityLocation> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit) {
        List<OrganizationActivityLocation> organizationActivityLocations;
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            Criteria startTimeCriteria = Criteria.where(UserActivityLocation.START_TIME_FIELD_NAME).gte(TimestampUtils.convertToSeconds(getStartTime(timeRangeInDays)));
            Query query = new Query(startTimeCriteria);
            organizationActivityLocations = mongoTemplate.find(query, OrganizationActivityLocation.class, COLLECTION_NAME);
        }
        else {
            final String errorMessage = String.format("Could not find collection '%s' in database", COLLECTION_NAME);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        filterUnknownCountries(organizationActivityLocations);

        return organizationActivityLocations;
    }

    private void filterUnknownCountries(List<OrganizationActivityLocation> organizationActivityLocations) {
        organizationActivityLocations.forEach(a -> a.getLocations().getCountryHistogram().keySet().removeAll(ActivityLocationHelper.getUnknownCountryValues()));
    }

    private long getStartTime(int timeRangeInDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -timeRangeInDays);
        return calendar.getTime().getTime();
    }

    @Override
    public List<OrganizationActivityLocation> findAll() {
        return mongoTemplate.findAll(OrganizationActivityLocation.class);
    }


}
