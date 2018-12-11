package presidio.ade.domain.store.aggr;

import com.mongodb.DBCollection;
import fortscale.common.feature.MultiKeyFeature;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.store.StoreManager;
import fortscale.utils.store.StoreManagerAware;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.time.TimeRange;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.pagination.aggregated.AggregatedRecordPaginationService;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;
import presidio.ade.domain.store.ScoredDataReader;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;
import static presidio.ade.domain.record.AdeRecord.START_INSTANT_FIELD;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class AggregatedDataStoreMongoImpl implements AggregatedDataStore, StoreManagerAware,
        ScoredDataReader<ScoredFeatureAggregationRecord> {
    private static final String NULL_AGGREGATED_RECORD_PAGINATION_SERVICE = "pagination service must be set in order to read data in pages";

    private final MongoTemplate mongoTemplate;
    private final AggrDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private AggregatedRecordPaginationService aggregatedRecordPaginationService;
    private StoreManager storeManager;

    public AggregatedDataStoreMongoImpl(MongoTemplate mongoTemplate, AggrDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public <U extends AdeAggregationRecord> List<PageIterator<U>> read(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange) {
        Assert.notNull(aggregatedRecordPaginationService, NULL_AGGREGATED_RECORD_PAGINATION_SERVICE);
        return aggregatedRecordPaginationService.getPageIterators(aggregatedDataPaginationParamSet, timeRange);
    }

    @Override
    public <U extends AdeAggregationRecord> List<PageIterator<U>> read(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange, Double threshold) {
        Assert.notNull(aggregatedRecordPaginationService, NULL_AGGREGATED_RECORD_PAGINATION_SERVICE);
        return aggregatedRecordPaginationService.getPageIterators(aggregatedDataPaginationParamSet, timeRange, threshold);
    }

    @Override
    public Set<String> findDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet) {
        return doFindDistinctContextIds(timeRange, aggregatedDataPaginationParamSet, null);
    }

    @Override
    public Set<String> findDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Double threshold) {
        return doFindDistinctContextIds(timeRange, aggregatedDataPaginationParamSet, threshold);
    }

    @Override
    public <U extends AdeAggregationRecord> List<U> readRecords(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange) {
        return doReadRecords(timeRange, aggregatedDataPaginationParamSet, contextIds, null);
    }

    @Override
    public <U extends AdeAggregationRecord> List<U> readRecords(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange, Double threshold) {
        return doReadRecords(timeRange, aggregatedDataPaginationParamSet, contextIds, threshold);
    }

    @Override
    public void setAggregatedRecordPaginationService(AggregatedRecordPaginationService aggregatedRecordPaginationService) {
        this.aggregatedRecordPaginationService = aggregatedRecordPaginationService;
    }

    @Override
    public void store(List<? extends AdeAggregationRecord> records, AggregatedFeatureType aggregatedFeatureType, StoreMetadataProperties storeMetadataProperties) {
        Map<String, ? extends List<? extends AdeAggregationRecord>> featureNameToAggregationRecords = records.stream().collect(Collectors.groupingBy(AdeAggregationRecord::getFeatureName));

        featureNameToAggregationRecords.keySet().forEach(featureName -> {
            List<? extends AdeAggregationRecord> aggregationRecords = featureNameToAggregationRecords.get(featureName);
            AggrRecordsMetadata metadata = new AggrRecordsMetadata(featureName, aggregatedFeatureType);
            String collectionName = translator.toCollectionName(metadata);
            mongoDbBulkOpUtil.insertUnordered(aggregationRecords, collectionName);
            storeManager.registerWithTtl(getStoreName(), collectionName, storeMetadataProperties);
        });
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        // TODO
    }

    @SuppressWarnings("unchecked assignment")
    private Set<String> doFindDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Double threshold) {
        Date from = Date.from(timeRange.getStart());
        Date to = Date.from(timeRange.getEnd());
        Set<String> distinctContextIds = new HashSet<>();

        aggregatedDataPaginationParamSet.forEach(aggregatedDataPaginationParam -> {
            Query query = new Query(Criteria.where(AdeAggregationRecord.START_INSTANT_FIELD).gte(from).lt(to));
            query = updateThresholdCriteriaInQuery(threshold, aggregatedDataPaginationParam.getAggregatedFeatureType(), query);
            AggrRecordsMetadata metadata = getAggrRecordsMetadata(aggregatedDataPaginationParam);
            DBCollection collection = mongoTemplate.getCollection(translator.toCollectionName(metadata));
            distinctContextIds.addAll(collection.distinct(AdeAggregationRecord.CONTEXT_ID_FIELD, query.getQueryObject()));
        });

        return distinctContextIds;
    }

    @SuppressWarnings("unchecked assignment")
    private <U extends AdeAggregationRecord> List<U> doReadRecords(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, Double threshold) {
        Instant from = timeRange.getStart();
        Instant to = timeRange.getEnd();
        List<U> records = new ArrayList<>();

        aggregatedDataPaginationParamSet.forEach(aggregatedDataPaginationParam -> {
            Query query = new Query()
                    .addCriteria(Criteria.where(AdeAggregationRecord.START_INSTANT_FIELD).gte(from).lt(to))
                    .addCriteria(Criteria.where(AdeAggregationRecord.CONTEXT_ID_FIELD).in(contextIds));
            AggregatedFeatureType aggregatedFeatureType = aggregatedDataPaginationParam.getAggregatedFeatureType();
            query = updateThresholdCriteriaInQuery(threshold, aggregatedFeatureType, query);
            Class<? extends AdeAggregationRecord> resolvedClass = resolveClass(aggregatedFeatureType);
            Assert.notNull(resolvedClass, "adeEventType class must not be null");
            AggrRecordsMetadata metadata = getAggrRecordsMetadata(aggregatedDataPaginationParam);
            records.addAll(mongoTemplate.find(query, (Class<U>)resolvedClass, translator.toCollectionName(metadata)));
        });

        return records;
    }

    private Query updateThresholdCriteriaInQuery(Double threshold, AggregatedFeatureType aggregatedFeatureType, Query query) {
        if (threshold != null) {
            switch (aggregatedFeatureType) {
                case SCORE_AGGREGATION:
                    return query.addCriteria(Criteria.where(AdeAggregationRecord.FEATURE_VALUE_FIELD_NAME).gt(threshold));
                case FEATURE_AGGREGATION:
                    return query.addCriteria(Criteria.where(ScoredFeatureAggregationRecord.SCORE_FIELD_NAME).gt(threshold));
            }
        }

        return query;
    }

    private AggrRecordsMetadata getAggrRecordsMetadata(AggregatedDataPaginationParam aggregatedDataPaginationParam) {
        String featureName = aggregatedDataPaginationParam.getFeatureName();
        AggregatedFeatureType aggregatedFeatureType = aggregatedDataPaginationParam.getAggregatedFeatureType();
        return new AggrRecordsMetadata(featureName, aggregatedFeatureType);
    }

    private Class<? extends AdeAggregationRecord> resolveClass(AggregatedFeatureType aggregatedFeatureType) {
        Class<? extends AdeAggregationRecord> resolvedClass = null;

        if (aggregatedFeatureType.equals(AggregatedFeatureType.SCORE_AGGREGATION)) {
            resolvedClass = AdeAggregationRecord.class;
        } else if (aggregatedFeatureType.equals(AggregatedFeatureType.FEATURE_AGGREGATION)) {
            resolvedClass = ScoredFeatureAggregationRecord.class;
        }

        return resolvedClass;
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
    public void remove(String collectionName, Instant start, Instant end) {
        Query query = new Query(where(START_INSTANT_FIELD).gte(start).lt(end));
        mongoTemplate.remove(query, collectionName);
    }

    @Override
    public long countScoredRecords(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, int scoreThreshold, String adeEventType) {

        Query query = buildAggregationRecordsQuery(timeRange, contextFieldNameToValueMap, scoreThreshold);
        AggrRecordsMetadata metadata = buildAggregationRecordsMetadata(adeEventType);
        return mongoTemplate.count(query, ScoredFeatureAggregationRecord.class, translator.toCollectionName(metadata));
    }

    @Override
    public List<ScoredFeatureAggregationRecord> readScoredRecords(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, int scoreThreshold, String adeEventType,
            int skip, int limit) {

        Query query = buildAggregationRecordsQuery(timeRange, contextFieldNameToValueMap, scoreThreshold)
                .skip(skip).limit(limit);
        AggrRecordsMetadata metadata = buildAggregationRecordsMetadata(adeEventType);
        return mongoTemplate.find(query, ScoredFeatureAggregationRecord.class, translator.toCollectionName(metadata));
    }

    @Override
    public Instant readFirstStartInstant(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, int scoreThreshold, String adeEventType) {

        return timeRange.getStart();
    }

    @Override
    public Instant readLastStartInstant(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, int scoreThreshold, String adeEventType) {

        return timeRange.getEnd();
    }

    private static Query buildAggregationRecordsQuery(
            TimeRange timeRange, MultiKeyFeature contextFieldNameToValueMap, int scoreThreshold) {

        Query query = query(where(START_INSTANT_FIELD).gte(timeRange.getStart()).lt(timeRange.getEnd()));
        contextFieldNameToValueMap.getFeatureNameToValue().forEach((contextFieldName, contextFieldValue) -> {
            contextFieldName = String.format("context.%s", contextFieldName);
            query.addCriteria(where(contextFieldName).is(contextFieldValue));
        });
        query.addCriteria(where(ScoredFeatureAggregationRecord.SCORE_FIELD_NAME).gt(scoreThreshold));
        return query;
    }

    private static AggrRecordsMetadata buildAggregationRecordsMetadata(String adeEventType) {
        String aggregationRecordName = AdeAggregationRecord.getAggregationRecordName(adeEventType);
        return new AggrRecordsMetadata(aggregationRecordName, AggregatedFeatureType.FEATURE_AGGREGATION);
    }
}
