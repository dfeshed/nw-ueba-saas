package presidio.ade.domain.store.accumulator.smart;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import fortscale.utils.store.record.StoreManagerMetadataProperties;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.accumulator.AccumulatedSmartRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;


public class SmartAccumulationDataStoreMongoImpl implements SmartAccumulationDataStore, StoreManagerAware {
    private static final Logger logger = Logger.getLogger(SmartAccumulationDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final SmartAccumulatedDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private StoreManager storeManager;

    public SmartAccumulationDataStoreMongoImpl(MongoTemplate mongoTemplate, SmartAccumulatedDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public void store(List<? extends AdeContextualAggregatedRecord> records, String configurationName, StoreManagerMetadataProperties storeManagerMetadataProperties) {
        logger.info("Store accumulated smart records");
        SmartAccumulatedRecordsMetaData metadata = new SmartAccumulatedRecordsMetaData(configurationName);
        String collectionName = getCollectionName(metadata);
        mongoDbBulkOpUtil.insertUnordered(records, collectionName);
        storeManager.registerWithTtl(getStoreName(), collectionName, storeManagerMetadataProperties);
    }

    /**
     * @param metadata metadata
     * @return collection name
     */
    protected String getCollectionName(SmartAccumulatedRecordsMetaData metadata) {
        return translator.toCollectionName(metadata);
    }


    @Override
    public List<AccumulatedSmartRecord> findAccumulatedEventsByContextIdAndStartTimeRange(String configurationName,
                                                                                          String contextId,
                                                                                          Instant startTimeFrom,
                                                                                          Instant startTimeTo) {
        logger.debug("getting accumulated events for smart record name={}", configurationName);

        SmartAccumulatedRecordsMetaData metadata = new SmartAccumulatedRecordsMetaData(configurationName);
        String collectionName = getCollectionName(metadata);

        Query query = new Query()
                .addCriteria(where(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD)
                        .is(contextId))
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD)
                        .gte(startTimeFrom)
                        .lt(startTimeTo));
        List<AccumulatedSmartRecord> accumulatedSmartRecords =
                mongoTemplate.find(query, AccumulatedSmartRecord.class, collectionName);


        logger.debug("found {} accumulated events", accumulatedSmartRecords.size());
        return accumulatedSmartRecords;
    }

    @Override
    public Set<String> findDistinctContextsByTimeRange(String configurationName, Instant startInstant, Instant endInstant) {

        logger.debug("finding distinct contexts by configurationName={} startTime={} endTime={}",
                configurationName, startInstant, endInstant);

        SmartAccumulatedRecordsMetaData metadata = new SmartAccumulatedRecordsMetaData(configurationName);
        String collectionName = getCollectionName(metadata);

        Query query = new Query();
        query.addCriteria(where(AdeRecord.START_INSTANT_FIELD).gte(Date.from(startInstant)).lt(Date.from(endInstant)));

        Set<String> distinctContexts = (Set<String>) mongoTemplate.getCollection(collectionName)
                .distinct(AdeContextualAggregatedRecord.CONTEXT_ID_FIELD, query.getQueryObject())
                .stream().collect(Collectors.toSet());

        logger.debug("found distinct contexts: {}", Arrays.toString(distinctContexts.toArray()));
        return distinctContexts;
    }


    @Override
    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    @Override
    public void remove(String collectionName, Instant until) {
        Query query = new Query()
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD).lt(until));
        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public void remove(String collectionName, Instant start, Instant end){
        Query query = new Query()
                .addCriteria(where(AdeRecord.START_INSTANT_FIELD).gte(start).lt(end));
        mongoTemplate.remove(query, collectionName);
    }
}
