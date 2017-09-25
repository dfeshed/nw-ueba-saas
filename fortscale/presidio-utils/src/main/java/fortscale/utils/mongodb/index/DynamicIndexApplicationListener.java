package fortscale.utils.mongodb.index;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.*;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver.IndexDefinitionHolder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.util.MongoDbErrorCodes;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * {@link Indexed} or {@link CompoundIndex} annotated fields are indexed when a new collection is created for annotated POJO
 * <p>
 * cache check is preformed: if POJO class is already indexed for that collection, no index will be recreated
 * <p>
 * This class is triggered right before elements are converted into mongo object and inserted
 * Created by barak_schuster on 6/1/17.
 */
public class DynamicIndexApplicationListener implements ApplicationListener<BeforeConvertEvent<Object>> {
    private static final Logger logger = Logger.getLogger(DynamicIndexApplicationListener.class);

    private final MongoTemplate mongoTemplate;
    private final MongoDbUtilService mongoDbUtilService;
    // cache that indicated if class is already successfully indexed
    private final Set<Pair<String, ? extends Class<?>>> collectionToClassIndexCache;
    private final MongoMappingContext mappingContext;
    private final MongoPersistentEntityIndexResolver indexResolver;
    private final MongoDbFactory mongoDbFactory;

    /**
     * @param mongoTemplate      you know...
     * @param mongoDbUtilService uses as existing collection cache
     * @param mappingContext     mapping context
     * @param mongoDbFactory     mongo DB factory
     */
    public DynamicIndexApplicationListener(MongoTemplate mongoTemplate, MongoDbUtilService mongoDbUtilService, MongoMappingContext mappingContext, MongoDbFactory mongoDbFactory) {
        this.mongoTemplate = mongoTemplate;
        this.mongoDbUtilService = mongoDbUtilService;
        this.collectionToClassIndexCache = new HashSet<>();
        this.mappingContext = mappingContext;
        this.indexResolver = new MongoPersistentEntityIndexResolver(mappingContext);
        this.mongoDbFactory = mongoDbFactory;
    }

    /**
     * @param event - may contain {@link Indexed} annotation that defines new index to be created
     */
    @Override
    public void onApplicationEvent(BeforeConvertEvent<Object> event) {
        String collectionName = event.getCollectionName();
        Class<?> sourceClass = event.getSource().getClass();

        if (!sourceClass.isAnnotationPresent(Document.class)) {
            return;
        }

        createCollectionIndexesForClass(collectionName, sourceClass);
    }

    public void createCollectionIndexesForClass(String collectionName, Class<?> sourceClass) {
        Document sourceClassDocumentAnnotation = sourceClass.getAnnotation(Document.class);

        // in this case it is not a dynamic collection - it is already predefined
        if (!sourceClassDocumentAnnotation.collection().isEmpty()) {
            return;
        }

        Pair<String, ? extends Class<?>> cacheKey = Pair.of(collectionName, sourceClass);

        // if collection is not indexed by this class annotations
        if (!collectionToClassIndexCache.contains(cacheKey)) {
            collectionToClassIndexCache.add(cacheKey);
            mongoDbUtilService.createCollectionIfNotExists(collectionName);
            List<IndexDefinitionHolder> dynamicCollectionIndexDefinitionHolders = resolveDynamicCollectionIndexes(collectionName, sourceClass);
            dynamicCollectionIndexDefinitionHolders.forEach(this::createIndex);
        }
    }

    private List<IndexDefinitionHolder> resolveDynamicCollectionIndexes(String collectionName, Class<?> sourceClass) {
        List<IndexDefinitionHolder> indexDefinitionHolders = indexResolver.resolveIndexForEntity(mappingContext.getPersistentEntity(sourceClass));
        Predicate<IndexDefinitionHolder> predicate = getIncludeCompoundIndexDefinitionOfPropertyPredicate(sourceClass.getAnnotation(DynamicIndexing.class));
        return indexDefinitionHolders.stream()
                .filter(predicate)
                .map(holder -> new IndexDefinitionHolder(holder.getPath(), holder.getIndexDefinition(), collectionName))
                .collect(Collectors.toList());
    }

    /**
     * Get a {@link Predicate} that receives an {@link IndexDefinitionHolder} and returns whether it should be included
     * or not, according to the {@link DynamicIndexing} options and the information in the {@link IndexDefinitionHolder}:
     * 1. If the {@link DynamicIndexing} is null, then by default compound index definitions of properties should be included,
     *    so the {@link Predicate} always returns true. This is also the case if the annotation exists, and the option is set to true.
     * 2. If the option is set to false, but the {@link IndexDefinition} is not a {@link CompoundIndexDefinition}, it should be included.
     * 3. If the option is set to false, and the {@link IndexDefinition} is a {@link CompoundIndexDefinition}, then the path is checked.
     *    If it is a root document path, the {@link CompoundIndexDefinition} should be included. If it is a property path,
     *    the {@link CompoundIndexDefinition} should not be included.
     *
     * @param dynamicIndexing the annotation that holds the dynamic indexing options
     * @return a function from {@link IndexDefinitionHolder} to {false, true}.
     */
    private Predicate<IndexDefinitionHolder> getIncludeCompoundIndexDefinitionOfPropertyPredicate(DynamicIndexing dynamicIndexing) {
        if (dynamicIndexing == null || dynamicIndexing.includeCompoundIndexDefinitionsOfProperties()) {
            return indexDefinitionHolder -> true;
        } else {
            return indexDefinitionHolder -> {
                if (!indexDefinitionHolder.getIndexDefinition().getClass().equals(CompoundIndexDefinition.class)) return true;
                // if the path is blank, then the index definition is of the root document (and not of a property)
                return StringUtils.isBlank(indexDefinitionHolder.getPath());
            };
        }
    }

    private void createIndex(IndexDefinitionHolder indexDefinitionHolder) {
        String collectionName = indexDefinitionHolder.getCollection();

        try {
            IndexDefinition indexDefinition = indexDefinitionHolder.getIndexDefinition();
            mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
        } catch (MongoException ex) {
            if (MongoDbErrorCodes.isDataIntegrityViolationCode(ex.getCode())) {
                DBObject existingIndex = fetchIndexInformation(indexDefinitionHolder);

                if (existingIndex != null) {
                    logger.debug("Cannot create index for '{}' in collection '{}' with keys '{}' and options '{}'. Index already defined as '{}'.",
                            indexDefinitionHolder.getPath(), collectionName, indexDefinitionHolder.getIndexKeys(), indexDefinitionHolder.getIndexOptions(), indexDefinitionHolder.getPath());
                    return;
                }

                throw new DataIntegrityViolationException(String.format("Cannot create index for '%s' in collection '%s' with keys '%s' and options '%s'.",
                        indexDefinitionHolder.getPath(), collectionName, indexDefinitionHolder.getIndexKeys().toString(), indexDefinitionHolder.getIndexOptions().toString()), ex);
            }

            RuntimeException exceptionToThrow = mongoDbFactory.getExceptionTranslator().translateExceptionIfPossible(ex);
            throw exceptionToThrow != null ? exceptionToThrow : ex;
        }
    }

    private DBObject fetchIndexInformation(IndexDefinitionHolder indexDefinition) {
        if (indexDefinition == null) {
            return null;
        }

        try {
            Object indexNameToLookUp = indexDefinition.getIndexOptions().get("name");

            for (DBObject index : mongoDbFactory.getDb().getCollection(indexDefinition.getCollection()).getIndexInfo()) {
                if (ObjectUtils.nullSafeEquals(indexNameToLookUp, index.get("name"))) {
                    return index;
                }
            }
        } catch (Exception e) {
            logger.debug(String.format("Failed to load index information for collection '%s'.", indexDefinition.getCollection()), e);
        }

        return null;
    }
}
