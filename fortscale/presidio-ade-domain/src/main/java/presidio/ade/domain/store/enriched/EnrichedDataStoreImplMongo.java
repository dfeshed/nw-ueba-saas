package presidio.ade.domain.store.enriched;

import fortscale.utils.logging.Logger;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.query.Query;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.enriched.EnrichedRecord;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EnrichedDataStoreImplMongo implements EnrichedDataStore {
	private static final Logger logger = Logger.getLogger(EnrichedDataStoreImplMongo.class);

	private final MongoTemplate mongoTemplate;
	private final EnrichedDataToCollectionNameTranslator translator;

	public EnrichedDataStoreImplMongo(MongoTemplate mongoTemplate, EnrichedDataToCollectionNameTranslator translator) {
		this.mongoTemplate = mongoTemplate;
		this.translator = translator;
	}

	@Override
	public void store(EnrichedRecordsMetadata recordsMetadata, List<? extends EnrichedRecord> records) {
		logger.info("storing by recordsMetadata={}", recordsMetadata);
		String collectionName = translator.toCollectionName(recordsMetadata);
//		mongoIndexCreator.ensureIndexes(collectionName, this);
		mongoTemplate.insert(records, collectionName);
	}

	@Override
	public void cleanup(EnrichedDataStoreCleanupParams cleanupParams) {
		logger.info("cleanup by cleanupParams={}", cleanupParams);
		Collection<String> collectionNames = translator.toCollectionNames(cleanupParams);
		Query cleanupQuery = toCleanupQuery(cleanupParams);

		for (String collectionName : collectionNames) {
			mongoTemplate.remove(cleanupQuery, collectionName);
		}
	}

	/**
	 * @param cleanupParams to build the remove query
	 * @return cleanup query by cleanup params
	 */
	private Query toCleanupQuery(EnrichedDataStoreCleanupParams cleanupParams) {
		return null;
	}

	public Set<Index> getIndexes() {
		Set<Index> indexSet = new HashSet<>();
		indexSet.add(new Index().on(AdeRecord.DATE_TIME_FIELD, Sort.Direction.ASC));
		return indexSet;
	}
}
