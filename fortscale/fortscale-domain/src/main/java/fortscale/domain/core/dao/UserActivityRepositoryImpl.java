package fortscale.domain.core.dao;

import fortscale.domain.core.UserActivity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserActivityRepositoryImpl implements UserActivityRepository {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public UserActivityRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<LocationEntry> getLocationEntries(int timeRangeInDays, int limit) {
        Query query = new Query();
        query.addCriteria(Criteria.where().is(tag.getName()));

        mongoTemplate.g
    }

    @Override
    public List<UserActivity> findAll() {
        return mongoTemplate.findAll(UserActivity.class);
    }
}
