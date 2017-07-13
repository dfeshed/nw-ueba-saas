package presidio.ade.domain.store.aggr;

import fortscale.utils.logging.Logger;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import presidio.ade.domain.record.aggregated.AdeAggrRecord;
import presidio.ade.domain.store.AdeDataStoreCleanupParams;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by barak_schuster on 7/10/17.
 */
public class AggrDataStoreMongoImpl implements AggrDataStore {
    private static final Logger logger = Logger.getLogger(AggrDataStoreMongoImpl.class);

    private MongoTemplate mongoTemplate;
    private AggrDataAdeToCollectionNameTranslator translator;

    public AggrDataStoreMongoImpl(MongoTemplate mongoTemplate, AggrDataAdeToCollectionNameTranslator translator) {
        this.mongoTemplate = mongoTemplate;
        this.translator = translator;
    }

    @Override
    public void store(List<? extends AdeAggrRecord> records) {
        Map<String, ? extends List<? extends AdeAggrRecord>> featureToAggrList = records.stream().collect(Collectors.groupingBy(AdeAggrRecord::getFeatureName));

        featureToAggrList.keySet().forEach(
                feature ->
                {
                    AggrRecordsMetadata metadata = new AggrRecordsMetadata(feature);
                    String collectionName = getCollectionName(metadata);
                    List<? extends AdeAggrRecord> aggrRecords = featureToAggrList.get(feature);
                    mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED,collectionName).insert(aggrRecords).execute();
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
