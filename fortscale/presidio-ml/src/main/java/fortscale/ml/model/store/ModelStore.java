package fortscale.ml.model.store;

import com.mongodb.*;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.logging.Logger;
import fortscale.utils.pagination.ContextIdToNumOfItems;
import fortscale.utils.store.StoreManagerAware;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import fortscale.utils.store.StoreManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.index.CompoundIndexDefinition;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ModelStore implements ModelReader, StoreManagerAware {
    private static final Logger logger = Logger.getLogger(ModelStore.class);
    private static final String COLLECTION_NAME_PREFIX = "model_";
    private static final String ModelDAO_FEILD_NAME = "ModelDAO";

    private MongoTemplate mongoTemplate;
    private StoreManager storeManager;
    private Duration ttlOldestAllowedModel;
    private int modelAggregationPaginationSize;
    private int modelQueryPaginationSize;

    public ModelStore(MongoTemplate mongoTemplate, Duration ttlOldestAllowedModel, int modelAggregationPaginationSize, int modelQueryPaginationSize) {
        this.mongoTemplate = mongoTemplate;
        this.ttlOldestAllowedModel = ttlOldestAllowedModel;
        this.modelAggregationPaginationSize = modelAggregationPaginationSize;
        this.modelQueryPaginationSize = modelQueryPaginationSize;
    }

    /**
     * @param modelConf the {@link ModelConf}
     * @param sessionId the session ID
     * @param instant   the {@link Instant}
     * @return the latest end instant across all models created from the given model
     *         conf with the given session ID that is less than the given instant
     */
    public Instant getLatestEndInstantLt(ModelConf modelConf, String sessionId, Instant instant) {
        Query query = new Query()
                .addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId))
                .addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).lt(instant))
                .with(new Sort(Direction.DESC, ModelDAO.END_TIME_FIELD));
        ModelDAO modelDao = mongoTemplate.findOne(query, ModelDAO.class, getCollectionName(modelConf));
        return modelDao == null ? null : modelDao.getEndTime();
    }

    @SuppressWarnings("unchecked")
    public List<String> getContextIdsWithModels(ModelConf modelConf, String sessionId, Instant endInstant) {
        Query query = new Query()
                .addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId))
                .addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).is(Date.from(endInstant)));
        return mongoTemplate
                .getCollection(getCollectionName(modelConf))
                .distinct(ModelDAO.CONTEXT_ID_FIELD, query.getQueryObject());
    }

    public List<String> getDistinctNumOfContextIds(ModelConf modelConf, Instant endInstant) {
        Query query = new Query()
                .addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).is(Date.from(endInstant)));
        return mongoTemplate.getCollection(getCollectionName(modelConf))
                .distinct(ModelDAO.CONTEXT_ID_FIELD, query.getQueryObject());
    }

    public void save(ModelConf modelConf, String sessionId, String contextId, Model model, TimeRange timeRange,
                     StoreMetadataProperties storeMetadataProperties,
                     Map<String, String> contextFieldNameToValueMap) {
        ModelDAO modelDao = new ModelDAO(sessionId, contextId, model,
                timeRange.getStart(), timeRange.getEnd(),
                contextFieldNameToValueMap);
        save(modelConf, modelDao, storeMetadataProperties);
    }

    public void save(ModelConf modelConf, ModelDAO modelDao, StoreMetadataProperties storeMetadataProperties) {
        String collectionName = getCollectionName(modelConf);
        mongoTemplate.insert(modelDao, collectionName);
        storeManager.registerWithTtl(getStoreName(), collectionName, storeMetadataProperties);
    }

    @Override
    public Set<String> getAllSubContextsWithLatestEndTimeLte(ModelConf modelConf, String contextFieldName, Instant eventEpochtime) {
        String collectionName = getCollectionName(modelConf);
        Aggregation agg = newAggregation(
                match(where(ModelDAO.END_TIME_FIELD).lte(Date.from(eventEpochtime))),
                Aggregation.group(ModelDAO.getContextFieldNamePath(contextFieldName)),
                Aggregation.project(contextFieldName).and("_id").as(contextFieldName).andExclude("_id")
        );
        AggregationResults<DBObject> aggrResult = mongoTemplate.aggregate(agg, collectionName, DBObject.class);

        Set<String> ret = aggrResult.getMappedResults().stream()
                .map(result -> (String)result.get(contextFieldName))
                .collect(Collectors.toSet());

        return ret;
    }

    public Collection<ModelDAO> getAllContextsModelDaosWithLatestEndTimeLte(ModelConf modelConf, Instant eventEpochtime) {
        return getAllContextsModelDaosWithLatestEndTimeLte(modelConf, null, null, eventEpochtime);
    }

    public Collection<ModelDAO> getAllContextsModelDaosWithLatestEndTimeLte(ModelConf modelConf, String contextFieldName,
                                                                            String contextValue, Instant eventEpochtime) {
        String collectionName = getCollectionName(modelConf);
        List<ModelDAO> queryResults;
        Map<String, ModelDAO> contextIdToModelDaoMap = new HashMap<>();
        int pageIndex = 0;
        Date latestEndDate = Date.from(eventEpochtime);
        do{
            Query query = new Query();
            if(contextFieldName != null){
                query.addCriteria(Criteria.where(ModelDAO.getContextFieldNamePath(contextFieldName)).is(contextValue));
            }
            query.addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).lte(latestEndDate))
                    .with(new Sort(Direction.ASC, ModelDAO.END_TIME_FIELD))
                    .skip(pageIndex*modelQueryPaginationSize)
                    .limit(modelQueryPaginationSize);
            queryResults = mongoTemplate.find(query, ModelDAO.class, collectionName);
            for(ModelDAO modelDAO: queryResults){
                //the models are ordered by time so we don't have to check if the map contains a model with larger time.
                contextIdToModelDaoMap.put(modelDAO.getContextId(), modelDAO);
            }
            pageIndex++;
        } while (queryResults.size() == modelQueryPaginationSize);
        return contextIdToModelDaoMap.values();
    }

    public List<ModelDAO> getLatestBeforeEventTimeAfterOldestAllowedModelDaoSortedByEndTimeDesc(
            ModelConf modelConf, String contextId, Instant eventTime, Instant oldestAllowedModelTime, int limit) {

        String collectionName = getCollectionName(modelConf);
        Query query = new Query()
                .addCriteria(Criteria.where(ModelDAO.CONTEXT_ID_FIELD).is(contextId))
                .addCriteria(Criteria.where(ModelDAO.END_TIME_FIELD).lte(eventTime).gte(oldestAllowedModelTime))
                .with(new Sort(Direction.DESC, ModelDAO.END_TIME_FIELD))
                .limit(limit);
        logger.debug("Fetching latest model DAO for contextId = {} eventTime = {} collection = {}.", contextId, eventTime, collectionName);
        return mongoTemplate.find(query, ModelDAO.class, collectionName);
    }

    public static String getCollectionName(ModelConf modelConf) {
        return COLLECTION_NAME_PREFIX + modelConf.getName();
    }


    @Override
    public void setStoreManager(StoreManager storeManager) {
        this.storeManager = storeManager;
    }

    @Override
    public void remove(String collectionName, Instant until) {

        //remove models that older than ttlOldestAllowedModel
        removeOldestModels(collectionName, until);

        removeContextIdOldModels(collectionName, until);
    }

    @Override
    public void remove(String collectionName, Instant start, Instant end){

    }


    /**
     * Remove models of contextIds, where contextId has at least one model that greater than until instant.
     * <p>
     * Try aggregate all the contextIds and remove there models,
     * if aggregate method throw an exception that total size of the result set exceeds the BSON Document Size, get contexts and remove models with pagination.
     *
     * @param collectionName collectionName
     * @param until          until instant
     */
    private void removeContextIdOldModels(String collectionName, Instant until) {
        try {
            Aggregation agg = newAggregation(
                    match(where(ModelDAO.END_TIME_FIELD).gt(Date.from(until))),
                    Aggregation.group(ModelDAO.CONTEXT_ID_FIELD),
                    Aggregation.project(ModelDAO.CONTEXT_ID_FIELD).and("_id").as(ModelDAO.CONTEXT_ID_FIELD).andExclude("_id")
            );
            AggregationResults<DBObject> aggrResult = mongoTemplate.aggregate(agg, collectionName, DBObject.class);
            removeContextIdsModels(collectionName, until, aggrResult);

        } catch (InvalidDataAccessApiUsageException ex) {
            AggregationResults<DBObject> aggrResult;

            long limit = modelAggregationPaginationSize;
            long skip = 0;
            do {
                Aggregation agg = newAggregation(
                        match(where(ModelDAO.END_TIME_FIELD).gt(Date.from(until))),
                        Aggregation.group(ModelDAO.CONTEXT_ID_FIELD),
                        Aggregation.project(ModelDAO.CONTEXT_ID_FIELD).and("_id").as(ModelDAO.CONTEXT_ID_FIELD).andExclude("_id"),
                        Aggregation.sort(Direction.ASC, ModelDAO.CONTEXT_ID_FIELD),
                        Aggregation.skip(skip),
                        Aggregation.limit(limit)
                );
                skip = skip + modelAggregationPaginationSize;
                aggrResult = mongoTemplate.aggregate(agg, collectionName, DBObject.class);
                removeContextIdsModels(collectionName, until, aggrResult);
            } while (!aggrResult.getMappedResults().isEmpty());

        }
    }

    /**
     * Remove models of context ids
     *
     * @param collectionName collectionName
     * @param until          until instant
     * @param aggrResult     context ids result
     */
    private void removeContextIdsModels(String collectionName, Instant until, AggregationResults<DBObject> aggrResult) {
        List<DBObject> results = aggrResult.getMappedResults();

        if (!results.isEmpty()) {
            List<String> contextIds = results.stream()
                    .map(result -> (String)result.get(ModelDAO.CONTEXT_ID_FIELD))
                    // Old global models should only be removed in the first cleanup step, regardless of
                    // the context ID. Therefore null context IDs should not be added to the list, otherwise
                    // more recent global models will also be removed in this cleanup step unintentionally.
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            if (!contextIds.isEmpty()) {
                Criteria contextCriteria = where(ModelDAO.CONTEXT_ID_FIELD).in(contextIds);
                Criteria dateCriteria = where(ModelDAO.END_TIME_FIELD).lte(Date.from(until));
                Query query = new Query(contextCriteria).addCriteria(dateCriteria);
                mongoTemplate.remove(query, collectionName);
            }
        }
    }


    public List<ModelDAO> readRecords(ModelConf modelConf, Instant eventEpochTime, Set<String> contextIds, int numOfItemsToSkip, int numOfItemsToRead) {
        String collectionName = getCollectionName(modelConf);
        Date latestEndDate = Date.from(eventEpochTime);

        Aggregation agg = newAggregation(
                match(where(ModelDAO.END_TIME_FIELD).lte(latestEndDate)),
                match(where(ModelDAO.CONTEXT_ID_FIELD).in(contextIds)),
                Aggregation.sort(Direction.ASC, ModelDAO.END_TIME_FIELD),
                Aggregation.skip((long)numOfItemsToSkip),
                Aggregation.limit((long)numOfItemsToRead),
                Aggregation.group(ModelDAO.CONTEXT_ID_FIELD).last("$$ROOT").as(ModelDAO_FEILD_NAME),
                Aggregation.project(ModelDAO_FEILD_NAME).andExclude("_id")
        );

        AggregationResults<DBObject> result = mongoTemplate.aggregate(agg, collectionName, DBObject.class);
        return result.getMappedResults().stream()
                .map(e -> mongoTemplate.getConverter().read(ModelDAO.class,(BasicDBObject) e.get(ModelDAO_FEILD_NAME)) )
                .collect(Collectors.toList());
    }

    /**
     * Remove models that older than ttlOldestAllowedModel
     *
     * @param collectionName collectionName
     * @param until          until instant
     */
    private void removeOldestModels(String collectionName, Instant until) {
        Query oldestModelQuery = new Query()
                .addCriteria(where(ModelDAO.END_TIME_FIELD).lte(until.minus(ttlOldestAllowedModel)));
        mongoTemplate.remove(oldestModelQuery, collectionName);
    }

    /**
     * Validate that the query fields indexed in the store.
     */
    public void ensureContextAndDateTimeIndex(ModelConf modelConf, List<String> contexts){
        String collectionName = getCollectionName(modelConf);

        // Used by the readRecords method (no sorting)
        DBObject indexOptions = new BasicDBObject(); // Keeps entries ordered
        for(String context: contexts) {
            indexOptions.put(ModelDAO.getContextFieldNamePath(context), 1);
        }
        indexOptions.put(ModelDAO.START_TIME_FIELD, 1);
        CompoundIndexDefinition indexDefinition = new CompoundIndexDefinition(indexOptions);
        String indexName = String.join("_", contexts) + "_" + ModelDAO.START_TIME_FIELD;
        indexDefinition.named(indexName);
        mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
    }
}
