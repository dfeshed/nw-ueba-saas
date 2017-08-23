package presidio.ade.domain.store.aggr;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import fortscale.utils.pagination.PageIterator;
import fortscale.utils.time.TimeRange;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.Assert;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.pagination.aggregated.AggregatedRecordPaginationService;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggregatedDataStoreMongoImpl implements AggregatedDataStore {
    private static final Logger logger = Logger.getLogger(AggregatedDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final AggrDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;
    private AggregatedRecordPaginationService aggregatedRecordPaginationService;

    public AggregatedDataStoreMongoImpl(MongoTemplate mongoTemplate, AggrDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    public void setAggregatedRecordPaginationService(AggregatedRecordPaginationService aggregatedRecordPaginationService) {
        this.aggregatedRecordPaginationService = aggregatedRecordPaginationService;
    }

    @Override
    public void store(List<? extends AdeAggregationRecord> records, AggregatedFeatureType aggregatedFeatureType) {
        Map<String, ? extends List<? extends AdeContextualAggregatedRecord>> featureToAggrList = records.stream().collect(Collectors.groupingBy(AdeAggregationRecord::getFeatureName));

        featureToAggrList.keySet().forEach(
                feature ->
                {
                    AggrRecordsMetadata metadata = new AggrRecordsMetadata(feature, aggregatedFeatureType);
                    String collectionName = getCollectionName(metadata);
                    List<? extends AdeContextualAggregatedRecord> aggrRecords = featureToAggrList.get(feature);
                    mongoDbBulkOpUtil.insertUnordered(aggrRecords,collectionName);
                }
        );
    }

    protected String getCollectionName(AggrRecordsMetadata metadata) {
        return translator.toCollectionName(metadata);
    }

    @Override
    public void cleanup(AdeDataStoreCleanupParams cleanupParams) {
        // todo
    }


    @Override
    public <U extends AdeAggregationRecord> List<PageIterator<U>> read(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, TimeRange timeRange) {
        Assert.notNull(aggregatedRecordPaginationService,"pagination service must be set in order to read data in pages");
        List<PageIterator<U>> pageIterators = aggregatedRecordPaginationService.getPageIterators(aggregatedDataPaginationParamSet, timeRange);
        return pageIterators;
    }

    @Override
    public Set<String> findDistinctContextIds(TimeRange timeRange, Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet) {
        Date from = Date.from(timeRange.getStart());
        Date to = Date.from(timeRange.getEnd());
        Set<String> distinctContextIds = new HashSet<>();

        aggregatedDataPaginationParamSet.forEach(aggregatedDataPaginationParam -> {
                    AggrRecordsMetadata metadata = getAggrRecordsMetadata(aggregatedDataPaginationParam);
                    String collectionName = translator.toCollectionName(metadata);
                    List<String> distinctContextIdsForFeature = findDistinctContextIds(from, to, collectionName);
                    distinctContextIds.addAll(distinctContextIdsForFeature);
                }
        );
        return distinctContextIds;
    }

    private AggrRecordsMetadata getAggrRecordsMetadata(AggregatedDataPaginationParam aggregatedDataPaginationParam) {
        String featureName = aggregatedDataPaginationParam.getFeatureName();
        AggregatedFeatureType aggregatedFeatureType = aggregatedDataPaginationParam.getAggregatedFeatureType();
        return new AggrRecordsMetadata(featureName, aggregatedFeatureType);
    }

    @SuppressWarnings("unchecked assignment")
    private List<String> findDistinctContextIds(Date from, Date to, String collectionName) {
        DBCollection collection = mongoTemplate.getCollection(collectionName);
        Criteria dateTimeCriteria = Criteria.where(AdeAggregationRecord.START_INSTANT_FIELD).gte(from).lt(to);
        DBObject query = new Query(dateTimeCriteria).getQueryObject();
        List<String> distinctContextIds = collection.distinct(AdeAggregationRecord.CONTEXT_ID_FIELD, query);
        return distinctContextIds;
    }

    @Override
    @SuppressWarnings("unchecked assignment")
    public <U extends AdeAggregationRecord> List<U> readRecords(Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet, Set<String> contextIds, TimeRange timeRange) {
        Instant from = timeRange.getStart();
        Instant to = timeRange.getEnd();
        List<U> result = new ArrayList<>();
        aggregatedDataPaginationParamSet.forEach(aggregatedDataPaginationParam -> {
            AggrRecordsMetadata aggrRecordsMetadata = getAggrRecordsMetadata(aggregatedDataPaginationParam);
            String collectionName = translator.toCollectionName(aggrRecordsMetadata);
            Criteria dateTimeCriteria = Criteria.where(AdeAggregationRecord.START_INSTANT_FIELD).gte(from).lt(to);
            Criteria contextIdsCriteria = Criteria.where(AdeAggregationRecord.CONTEXT_ID_FIELD).in(contextIds);
            Query query = new Query(dateTimeCriteria).addCriteria(contextIdsCriteria);
            AggregatedFeatureType aggregatedFeatureType = aggregatedDataPaginationParam.getAggregatedFeatureType();
            Class<? extends AdeAggregationRecord> clz = resolveClass(aggregatedFeatureType);
            Assert.notNull(clz,"adeEventType class must not be null");
            List<U> featureDocuments = mongoTemplate.find(query, (Class<U>) clz, collectionName);
            result.addAll(featureDocuments);
        });
        return result;
    }

    private Class<? extends AdeAggregationRecord> resolveClass(AggregatedFeatureType aggregatedFeatureType) {
        Class<? extends AdeAggregationRecord> clz = null;
        if(aggregatedFeatureType.equals(AggregatedFeatureType.SCORE_AGGREGATION))
        {
            clz = AdeAggregationRecord.class;
        }
        else if(aggregatedFeatureType.equals(AggregatedFeatureType.FEATURE_AGGREGATION))
        {
            clz = ScoredFeatureAggregationRecord.class;
        }
        return clz;
    }
}
