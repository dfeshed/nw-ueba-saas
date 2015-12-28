package fortscale.domain.core.dao;
import com.mongodb.WriteResult;
import fortscale.domain.core.ApplicationConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import java.util.HashMap;
import java.util.List;


public class ApplicationConfigurationRepositoryImpl implements ApplicationConfigurationRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<ApplicationConfiguration> findAll() {
        return mongoTemplate.findAll(ApplicationConfiguration.class);
    }

    /**
     * Iterates through Map, and upserts each config item.
     *
     * @param configItems Map of config items.
     */
    public void updateConfigItems(HashMap<String, String> configItems) {

        for(String key: configItems.keySet()) {

            Query query = new Query();
            query.addCriteria(Criteria.where(ApplicationConfiguration.KEY_FIELD_NAME).is(key));
            Update update = new Update();
            update.set(ApplicationConfiguration.VALUE_FIELD_NAME, configItems.get(key));

            mongoTemplate.upsert(query, update, ApplicationConfiguration.class);
        }


    }

}
