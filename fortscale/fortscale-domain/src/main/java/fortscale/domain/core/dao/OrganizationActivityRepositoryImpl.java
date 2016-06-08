package fortscale.domain.core.dao;

import fortscale.domain.core.OrganizationActivityLocationDocument;
import fortscale.domain.core.UserActivityLocationDocument;
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

    public static final String COLLECTION_NAME = OrganizationActivityLocationDocument.COLLECTION_NAME;
    private static final Logger logger = Logger.getLogger(OrganizationActivityRepositoryImpl.class);
    private final MongoTemplate mongoTemplate;

    @Autowired
    public OrganizationActivityRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays, int limit) {
        List<OrganizationActivityLocationDocument> organizationActivityLocationDocuments;
        if (mongoTemplate.collectionExists(COLLECTION_NAME)) {
            Criteria startTimeCriteria = Criteria.where(UserActivityLocationDocument.START_TIME_FIELD_NAME).gte(TimestampUtils.convertToSeconds(getStartTime(timeRangeInDays)));
            Query query = new Query(startTimeCriteria);
            organizationActivityLocationDocuments = mongoTemplate.find(query, OrganizationActivityLocationDocument.class, COLLECTION_NAME);
        }
        else {
            final String errorMessage = String.format("Could not find collection '%s' in database", COLLECTION_NAME);
            logger.error(errorMessage);
            throw new RuntimeException(errorMessage);
        }

        return organizationActivityLocationDocuments;
    }

    private long getStartTime(int timeRangeInDays) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -timeRangeInDays);
        return calendar.getTime().getTime();
    }

    @Override
    public List<OrganizationActivityLocationDocument> findAll() {
        return mongoTemplate.findAll(OrganizationActivityLocationDocument.class);
    }


}
