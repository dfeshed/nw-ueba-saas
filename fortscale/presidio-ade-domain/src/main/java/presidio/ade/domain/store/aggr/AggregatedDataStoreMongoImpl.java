package presidio.ade.domain.store.aggr;

import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbBulkOpUtil;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.aggregated.AdeContextualAggregatedRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggregatedDataStoreMongoImpl implements AggregatedDataStore {
    private static final Logger logger = Logger.getLogger(AggregatedDataStoreMongoImpl.class);

    private final MongoTemplate mongoTemplate;
    private final ScoreAggrDataToCollectionNameTranslator translator;
    private final MongoDbBulkOpUtil mongoDbBulkOpUtil;

    public AggregatedDataStoreMongoImpl(MongoTemplate mongoTemplate, ScoreAggrDataToCollectionNameTranslator translator, MongoDbBulkOpUtil mongoDbBulkOpUtil) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
        this.mongoDbBulkOpUtil = mongoDbBulkOpUtil;
    }

    @Override
    public void store(List<? extends AdeContextualAggregatedRecord> records) {
        Map<String, ? extends List<? extends AdeContextualAggregatedRecord>> featureToAggrList = records.stream().collect(Collectors.groupingBy(AdeContextualAggregatedRecord::getFeatureName));

        featureToAggrList.keySet().forEach(
                feature ->
                {
                    AggrRecordsMetadata metadata = new AggrRecordsMetadata(feature);
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
}
