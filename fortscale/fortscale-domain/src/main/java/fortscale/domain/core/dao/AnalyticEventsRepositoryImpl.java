package fortscale.domain.core.dao;


import fortscale.domain.core.AnalyticEvent;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public class AnalyticEventsRepositoryImpl implements AnalyticEventsRepositoryCustom {

    private static final Logger logger = Logger.getLogger(AnalyticEventsRepositoryImpl.class);

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * Stores all analytic events
     *
     * @param analyticEvents A list of analytic events
     */
    public void insertAnalyticEvents(List<AnalyticEvent> analyticEvents) {
        try {
            mongoTemplate.insert(analyticEvents, AnalyticEvent.collectionName);
        } catch (Exception ex) {
            logger.error("Failed to insert analytic events {}", analyticEvents, ex);
            throw ex;
        }
    }
}
