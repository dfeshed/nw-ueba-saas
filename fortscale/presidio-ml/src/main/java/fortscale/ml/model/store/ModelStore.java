package fortscale.ml.model.store;

import com.mongodb.*;
import fortscale.ml.model.Model;
import fortscale.ml.model.ModelConf;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.TtlService;
import fortscale.utils.ttl.TtlServiceAware;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.StreamUtils;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.match;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.newAggregation;
import static org.springframework.data.mongodb.core.query.Criteria.where;

public class ModelStore implements TtlServiceAware {
    private static final Logger logger = Logger.getLogger(ModelStore.class);
    private static final String COLLECTION_NAME_PREFIX = "model_";

    private MongoTemplate mongoTemplate;
    private TtlService ttlService;
    private Duration ttlOldestAllowedModel;
    private int modelPaginationSize;

    public ModelStore(MongoTemplate mongoTemplate, Duration ttlOldestAllowedModel, int modelPaginationSize) {
        this.mongoTemplate = mongoTemplate;
        this.ttlOldestAllowedModel = ttlOldestAllowedModel;
        this.modelPaginationSize = modelPaginationSize;
    }

    /**
     * @param modelConf the {@link ModelConf}
     * @param sessionId the session ID
     * @return the latest end time across all models created from the given model conf with the given session ID
     */
    public Instant getLatestEndTime(ModelConf modelConf, String sessionId) {
        Query query = new Query()
                .addCriteria(Criteria.where(ModelDAO.SESSION_ID_FIELD).is(sessionId))
                .with(new Sort(Direction.DESC, ModelDAO.END_TIME_FIELD));
        ModelDAO modelDao = mongoTemplate.findOne(query, ModelDAO.class, getCollectionName(modelConf));
        return modelDao == null ? null : modelDao.getEndTime();
    }

    public void save(ModelConf modelConf, String sessionId, String contextId, Model model, TimeRange timeRange) {
        ModelDAO modelDao = new ModelDAO(sessionId, contextId, model, timeRange.getStart(), timeRange.getEnd());
        save(modelConf, modelDao);
    }

    public void save(ModelConf modelConf, ModelDAO modelDao) {
        String collectionName = getCollectionName(modelConf);
        mongoTemplate.insert(modelDao, collectionName);
        ttlService.save(getStoreName(), collectionName);
    }

    public List<ModelDAO> getAllContextsModelDaosWithLatestEndTimeLte(ModelConf modelConf, Instant eventEpochtime) {
        String modelDaosGroupName = "modelDaos";
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(new Criteria(ModelDAO.END_TIME_FIELD).lte(Date.from(eventEpochtime))),
                Aggregation.group(ModelDAO.CONTEXT_ID_FIELD).push(Aggregation.ROOT).as(modelDaosGroupName));
        String collectionName = getCollectionName(modelConf);
        AggregationResults<DBObject> results = mongoTemplate.aggregate(aggregation, collectionName, DBObject.class);
        return StreamUtils.createStreamFromIterator(results.iterator())
                .map(modelDaoDbObjects -> {
                    ModelDAO[] modelDaos = mongoTemplate.getConverter().read(ModelDAO[].class, (DBObject) modelDaoDbObjects.get(modelDaosGroupName));
                    return Arrays.stream(modelDaos).max(Comparator.comparing(ModelDAO::getEndTime)).get();
                })
                .collect(Collectors.toList());
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
        List<ModelDAO> queryResult = mongoTemplate.find(query, ModelDAO.class, collectionName);

        return queryResult;
    }

    public static String getCollectionName(ModelConf modelConf) {
        return COLLECTION_NAME_PREFIX + modelConf.getName();
    }


    @Override
    public void setTtlService(TtlService ttlService) {
        this.ttlService = ttlService;
    }

    @Override
    public void remove(String collectionName, Instant until) {

        //remove models that older than ttlOldestAllowedModel
        removeOldestModels(collectionName, until);

        removeContextIdOldModels(collectionName, until);
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

        } catch (MongoException ex) {
            AggregationResults<DBObject> aggrResult;

            long limit = modelPaginationSize;
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
                skip = skip + modelPaginationSize;
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
        if (!aggrResult.getMappedResults().isEmpty()) {

            List<String> contextIds = new ArrayList<>();
            for (DBObject result : results) {
                contextIds.add((String) result.get(ModelDAO.CONTEXT_ID_FIELD));
            }

            Criteria contextCriteria = where(ModelDAO.CONTEXT_ID_FIELD).in(contextIds);
            Criteria dateCriteria = where(ModelDAO.END_TIME_FIELD).lte(Date.from(until));
            Query query = new Query(contextCriteria).addCriteria(dateCriteria);
            mongoTemplate.remove(query, collectionName);
        }
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

}
