package fortscale.streaming.service.event;


import fortscale.aggregation.feature.event.*;
import fortscale.utils.mongodb.FIndex;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AggregatedEventPersistencyHandler implements EventPersistencyHandler, InitializingBean {
    private static final String COLLECTION_NAME_SEPERATOR = "__";
    
    @Autowired
    private EventPersistencyHandlerFactory eventPersistencyHandlerFactory;

    @Autowired
    private MongoTemplate mongoTemplate;
    
    @Autowired
    private AggrFeatureEventBuilderService aggrFeatureEventBuilderService;

    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Value("${streaming.event.field.type.aggr_event}")
    private String eventTypeFieldValue;
    @Value("${streaming.aggr_event.field.aggregated_feature_name}")
    private String aggrFeatureNameFieldName;
    
    private Set<String> collectionNames;


    @Override
    public void saveEvent(JSONObject event, String collectionPrefix) {
        String aggrFeatureName = (String) event.get(aggrFeatureNameFieldName);
        String collectionName = new StringBuilder(collectionPrefix).append(COLLECTION_NAME_SEPERATOR).append(eventTypeFieldValue).append(COLLECTION_NAME_SEPERATOR).append(aggrFeatureName).toString();
        AggrEvent aggrEvent = aggrFeatureEventBuilderService.buildEvent(event);

        if (!isCollectionExist(collectionName)) {
            AggregatedFeatureEventConf aggregatedFeatureEventConf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(aggrFeatureName);
            String strategyName = aggregatedFeatureEventConf.getRetentionStrategyName();
            if(strategyName==null) {
                String errorMsg = String.format("Aggregated feature conf doesn't have retention strategy. Feature name: %s", aggrFeatureName);
                throw new IllegalArgumentException(errorMsg);
            }
            AggrFeatureRetentionStrategy retentionStrategy = aggregatedFeatureEventsConfService.getAggrFeatureRetnetionStrategy(strategyName);
            if(retentionStrategy==null) {
                String errorMsg = String.format("No aggregated feature retention strategy with the name [%s] exists in the AggregatedFeatureEventConfService", strategyName);
                throw new IllegalArgumentException(errorMsg);
            }
            long retentionTimeInSeconds = retentionStrategy.getRetentionInSeconds();

            mongoTemplate.createCollection(collectionName);
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(AggrEvent.EVENT_FIELD_BUCKET_CONF_NAME, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(AggrEvent.EVENT_FIELD_START_TIME_UNIX, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new FIndex().expire(retentionTimeInSeconds, TimeUnit.SECONDS).on(AggrEvent.EVENT_FIELD_END_TIME, Sort.Direction.DESC));
            mongoTemplate.indexOps(collectionName).ensureIndex(new Index().on(AggrEvent.EVENT_FIELD_CONTEXT, Sort.Direction.DESC));
            collectionNames.add(collectionName);
        }

        mongoTemplate.save(aggrEvent, collectionName);
    }
    
    private boolean isCollectionExist(String collectionName){
		return collectionNames.contains(collectionName);
	}


	@Override
	public void afterPropertiesSet() throws Exception {
		eventPersistencyHandlerFactory.register(eventTypeFieldValue, this);
		collectionNames = new HashSet<>(mongoTemplate.getCollectionNames());
	}
}
