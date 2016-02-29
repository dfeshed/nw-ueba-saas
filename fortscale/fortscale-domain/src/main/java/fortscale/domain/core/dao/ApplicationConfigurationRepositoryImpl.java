package fortscale.domain.core.dao;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Map;

public class ApplicationConfigurationRepositoryImpl implements ApplicationConfigurationRepositoryCustom {

    private static final Logger logger = Logger.getLogger(ApplicationConfigurationRepositoryImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Iterates through Map, and upserts each config item.
     *
     * @param configItems Map of config items.
     */
    public void updateConfigItems(Map<String, String> configItems) {
        for(String key: configItems.keySet()) {
            Query query = new Query();
            query.addCriteria(Criteria.where(ApplicationConfiguration.KEY_FIELD_NAME).is(key));
            Update update = new Update();
            update.set(ApplicationConfiguration.VALUE_FIELD_NAME, configItems.get(key));
            mongoTemplate.upsert(query, update, ApplicationConfiguration.class);
        }
    }

    @Override
    public void insertConfigItems(Map<String, String> configItems) {
        for(String key: configItems.keySet()) {
            ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(key, configItems.get(key));
            try {
                mongoTemplate.insert(applicationConfiguration);
            } catch (DuplicateKeyException ex) {
                //ignore duplicate key errors
            } catch (Exception ex) {
                logger.error("failed to insert config item {}={} - {}", key, configItems.get(key), ex);
            }
        }
    }


    @Override
    public void insertConfigItem(String key, String value) {
        ApplicationConfiguration applicationConfiguration = new ApplicationConfiguration(key, value);
        try {
            mongoTemplate.insert(applicationConfiguration);
        } catch (DuplicateKeyException ex) {
            //ignore duplicate key errors
        } catch (Exception ex) {
            logger.error("failed to insert config item {}={} - {}", key, value, ex);
        }
    }

}