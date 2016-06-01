package fortscale.collection.jobs.activity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

/**
 * @author gils
 * 31/05/2016
 */
@Configurable(preConstruction = true)
@Component
public abstract class UserActivityBaseHandler implements UserActivityHandler {
    static final String CONTEXT_ID_FIELD_NAME = "contextId";

    final static String CONTEXT_ID_USERNAME_PREFIX = "normalized_username###";
    static int CONTEXT_ID_USERNAME_PREFIX_LENGTH;

    final static int MONGO_READ_WRITE_BULK_SIZE = 1000;

    @Autowired
    protected MongoTemplate mongoTemplate;

    @Autowired
    protected UserActivityConfigurationService userActivityConfigurationService;

    static {
        CONTEXT_ID_USERNAME_PREFIX_LENGTH = CONTEXT_ID_USERNAME_PREFIX.length();
    }
}
