package fortscale.utils.mongodb.index;

import fortscale.utils.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.BasicMongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.*;
import java.util.stream.Collectors;

import static fortscale.utils.mongodb.index.DynamicIndexingConverters.convertCompoundIndexToIndexDefinition;
import static fortscale.utils.mongodb.index.DynamicIndexingConverters.convertIndexDefinitionToIndexInfo;
import static fortscale.utils.mongodb.index.DynamicIndexingConverters.convertToNamelessIndexInfo;

/**
 * This service is responsible for creating indexes dynamically in Mongo DB
 * collections, according to the annotations of the corresponding documents.
 *
 * @author Lior Govrin
 */
public class DynamicIndexingService {
	private static final Logger logger = Logger.getLogger(DynamicIndexingService.class);
	private static final String DEFAULT_ID_INDEX_NAME = "_id_";

	// A set of all the <document, collection name> pairs whose indexes were checked
	private final Set<Pair<Class<?>, String>> documentAndCollectionNamePairsChecked;
	private final MongoMappingContext mappingContext;
	private final MongoPersistentEntityIndexResolver indexResolver;
	private final MongoTemplate mongoTemplate;

	/**
	 * C'tor.
	 *
	 * @param mappingContext {@link MongoMappingContext}
	 * @param mongoTemplate  {@link MongoTemplate}
	 */
	public DynamicIndexingService(MongoMappingContext mappingContext, MongoTemplate mongoTemplate) {
		this.documentAndCollectionNamePairsChecked = new HashSet<>();
		this.mappingContext = mappingContext;
		this.indexResolver = new MongoPersistentEntityIndexResolver(mappingContext);
		this.mongoTemplate = mongoTemplate;
	}

	/**
	 * Ensure that all the indexes defined by the annotations of the
	 * given document also exist in the collection with the given name.
	 *
	 * @param document       the class whose annotations should be scanned
	 * @param collectionName the name of the collection whose indexes should be checked
	 */
	public void ensureDynamicIndexesExist(Class<?> document, String collectionName) {
		Pair<Class<?>, String> documentAndCollectionNamePair = Pair.of(document, collectionName);

		if (!documentAndCollectionNamePairsChecked.contains(documentAndCollectionNamePair)) {
			// Resolve the Spring and Presidio index annotations
			Map<String, Pair<IndexDefinition, IndexInfo>> resolvedIndexAnnotations = new HashMap<>();
			resolvedIndexAnnotations.putAll(resolveSpringIndexAnnotations(document));
			resolvedIndexAnnotations.putAll(resolvePresidioIndexAnnotations(document));
			// Load the index info of the collection from Mongo
			Map<String, IndexInfo> loadedIndexInfoFromMongo = loadIndexInfoFromMongo(collectionName);
			ensureIndexInfoInMongoIsUpToDate(resolvedIndexAnnotations, loadedIndexInfoFromMongo, collectionName);
			documentAndCollectionNamePairsChecked.add(documentAndCollectionNamePair);
		}
	}

	/**
	 * @return a map from the index name to the index definition and the index info (two different representations
	 *         of the same index), containing all the indexes defined by Spring annotations for the given document.
	 *         Note: For comparison, the nameless versions of index info are returned.
	 */
	private Map<String, Pair<IndexDefinition, IndexInfo>> resolveSpringIndexAnnotations(Class<?> document) {
		BasicMongoPersistentEntity<?> persistentEntity = mappingContext.getPersistentEntity(document);
		Map<String, Pair<IndexDefinition, IndexInfo>> indexNameToDefinitionAndInfoPairMap = new HashMap<>();
		indexResolver.resolveIndexForEntity(persistentEntity).forEach(indexDefinitionHolder -> {
			IndexDefinition indexDefinition = indexDefinitionHolder.getIndexDefinition();
			IndexInfo indexInfo = convertIndexDefinitionToIndexInfo(indexDefinition);
			IndexInfo namelessIndexInfo = convertToNamelessIndexInfo(indexInfo);
			indexNameToDefinitionAndInfoPairMap.put(indexInfo.getName(), Pair.of(indexDefinition, namelessIndexInfo));
		});
		return indexNameToDefinitionAndInfoPairMap;
	}

	/**
	 * @return a map from the index name to the index definition and the index info (two different representations
	 *         of the same index), containing all the indexes defined by Presidio annotations for the given document.
	 *         Note: For comparison, the nameless versions of index info are returned.
	 */
	private Map<String, Pair<IndexDefinition, IndexInfo>> resolvePresidioIndexAnnotations(Class<?> document) {
		boolean inheritFromSuperclass = true;
		Map<String, Pair<IndexDefinition, IndexInfo>> indexNameToDefinitionAndInfoPairMap = new HashMap<>();

		while (inheritFromSuperclass && document != null) {
			DynamicIndexing dynamicIndexing = document.getAnnotation(DynamicIndexing.class);
			if (dynamicIndexing != null) Arrays.stream(dynamicIndexing.compoundIndexes()).forEach(compoundIndex -> {
				IndexDefinition indexDefinition = convertCompoundIndexToIndexDefinition(compoundIndex);
				IndexInfo indexInfo = convertIndexDefinitionToIndexInfo(indexDefinition);
				IndexInfo namelessIndexInfo = convertToNamelessIndexInfo(indexInfo);
				indexNameToDefinitionAndInfoPairMap.put(indexInfo.getName(), Pair.of(indexDefinition, namelessIndexInfo));
			});
			inheritFromSuperclass = dynamicIndexing == null || dynamicIndexing.inheritFromSuperclass();
			document = document.getSuperclass();
		}

		return indexNameToDefinitionAndInfoPairMap;
	}

	/**
	 * @return a map from the index name to the index info, containing all the
	 *         indexes defined in Mongo for the collection with the given name.
	 *         Note: For comparison, the nameless versions of index info are returned.
	 */
	private Map<String, IndexInfo> loadIndexInfoFromMongo(String collectionName) {
		return mongoTemplate.indexOps(collectionName).getIndexInfo().stream()
				.filter(indexInfo -> !indexInfo.getName().equals(DEFAULT_ID_INDEX_NAME))
				.collect(Collectors.toMap(IndexInfo::getName, DynamicIndexingConverters::convertToNamelessIndexInfo));
	}

	/**
	 * Ensure that all the indexes defined by annotations are also defined in Mongo, and add to the collection any
	 * missing indexes. If a certain index is defined both by an annotation and in Mongo, but with different names,
	 * the index in Mongo is not modified, and the name is left as is.
	 */
	private void ensureIndexInfoInMongoIsUpToDate(
			Map<String, Pair<IndexDefinition, IndexInfo>> resolvedIndexAnnotations,
			Map<String, IndexInfo> loadedIndexInfoFromMongo,
			String collectionName) {

		resolvedIndexAnnotations.forEach((indexName, indexDefinitionAndInfoPair) -> {
			// If the collection doesn't have an identical index, it should be created
			if (!loadedIndexInfoFromMongo.containsValue(indexDefinitionAndInfoPair.getRight())) {
				logger.info("Adding missing index named {} to collection {}: {}.",
						indexName, collectionName, indexDefinitionAndInfoPair.getRight());
				IndexOperations indexOperations = mongoTemplate.indexOps(collectionName);

				// If the collection has another index with the same name, it should be dropped
				if (loadedIndexInfoFromMongo.containsKey(indexName)) {
					logger.warn("Dropping another index that is also named {} from collection {}: {}.",
							indexName, collectionName, loadedIndexInfoFromMongo.get(indexName));
					indexOperations.dropIndex(indexName);
				}

				// Add the missing index to the collection
				indexOperations.ensureIndex(indexDefinitionAndInfoPair.getLeft());
			}
		});
	}
}
