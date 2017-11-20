package presidio.ade.domain.store.aggr;

import com.mongodb.DBCollection;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import fortscale.utils.ttl.StoreManager;
import fortscale.utils.ttl.StoreManagerAware;
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

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class AggregatedDataStoreMongoImpl implements AggregatedDataStore, StoreManagerAware {
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
    public void store(List<? extends AdeAggregationRecord> records, AggregatedFeatureType aggregatedFeatureType) {
        Map<String, ? extends List<? extends AdeAggregationRecord>> featureNameToAggregationRecords = records.stream().collect(Collectors.groupingBy(AdeAggregationRecord::getFeatureName));

        featureNameToAggregationRecords.keySet().forEach(featureName -> {
            List<? extends AdeAggregationRecord> aggregationRecords = featureNameToAggregationRecords.get(featureName);
            AggrRecordsMetadata metadata = new AggrRecordsMetadata(featureName, aggregatedFeatureType);
            String collectionName = translator.toCollectionName(metadata);
            mongoDbBulkOpUtil.insertUnordered(aggregationRecords, collectionName);
            storeManager.registerWithTtl(getStoreName(), collectionName);
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

}
