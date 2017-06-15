package fortscale.utils.mongodb.index;

import com.mongodb.DBObject;
import com.mongodb.MongoException;
import fortscale.utils.logging.Logger;
import fortscale.utils.mongodb.util.MongoDbUtilService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.context.ApplicationListener;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.IndexDefinition;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.MongoPersistentEntityIndexResolver;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.data.mongodb.util.MongoDbErrorCodes;
import org.springframework.util.ObjectUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
     * @param mappingContext
     * @param mongoDbFactory
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
        Document sourceClassDocumentAnnotation = sourceClass.getAnnotation(Document.class);
        // in this case it is not a dynamic collection - it is already predefined
        if(!sourceClassDocumentAnnotation.collection().isEmpty())
        {
            return;
        }

        Pair<String, ? extends Class<?>> cacheKey = Pair.of(collectionName, sourceClass);
        // if collection is not indexed by this class annotations
        if (!collectionToClassIndexCache.contains(cacheKey)) {
            collectionToClassIndexCache.add(cacheKey);
            mongoDbUtilService.createCollectionIfNotExists(collectionName);
            List<MongoPersistentEntityIndexResolver.IndexDefinitionHolder> dynamicCollectionIndexDefinitionHolders = resolveDynamicCollectionIndexes(collectionName, sourceClass);
            dynamicCollectionIndexDefinitionHolders.forEach(this::createIndex);
        }
    }

    private List<MongoPersistentEntityIndexResolver.IndexDefinitionHolder> resolveDynamicCollectionIndexes(String collectionName, Class<?> sourceClass) {
        List<MongoPersistentEntityIndexResolver.IndexDefinitionHolder> indexDefinitionHolders = indexResolver.resolveIndexForEntity(mappingContext.getPersistentEntity(sourceClass));
        List<MongoPersistentEntityIndexResolver.IndexDefinitionHolder> dynamicCollectionIndexDefinitionHolders = new LinkedList<>();
        indexDefinitionHolders.forEach(indexDefinitionHolder -> {
            MongoPersistentEntityIndexResolver.IndexDefinitionHolder dynamicIndexDefinition = new MongoPersistentEntityIndexResolver.IndexDefinitionHolder(indexDefinitionHolder.getPath(), indexDefinitionHolder.getIndexDefinition(), collectionName);
            dynamicCollectionIndexDefinitionHolders.add(dynamicIndexDefinition);
        });
        return dynamicCollectionIndexDefinitionHolders;
    }

    private void createIndex(MongoPersistentEntityIndexResolver.IndexDefinitionHolder indexDefinitionHolder) {
        String collectionName = indexDefinitionHolder.getCollection();
        try {
            IndexDefinition indexDefinition = indexDefinitionHolder.getIndexDefinition();
            mongoTemplate.indexOps(collectionName).ensureIndex(indexDefinition);
        } catch (MongoException ex) {

            if (MongoDbErrorCodes.isDataIntegrityViolationCode(ex.getCode())) {

                DBObject existingIndex = fetchIndexInformation(indexDefinitionHolder);
                String message = "Cannot create index for '%s' in collection '%s' with keys '%s' and options '%s'.";

                if (existingIndex != null) {
                    message += " Index already defined as '%s'.";
                    logger.debug(message, indexDefinitionHolder.getPath());
                    return;
                }

                throw new DataIntegrityViolationException(
                        String.format(message, indexDefinitionHolder.getPath(), collectionName,
                                indexDefinitionHolder.getIndexKeys(), indexDefinitionHolder.getIndexOptions()),
                        ex);
            }

            RuntimeException exceptionToThrow = mongoDbFactory.getExceptionTranslator().translateExceptionIfPossible(ex);

            throw exceptionToThrow != null ? exceptionToThrow : ex;
        }
    }


    private DBObject fetchIndexInformation(MongoPersistentEntityIndexResolver.IndexDefinitionHolder indexDefinition) {

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
            logger.debug(
                    String.format("Failed to load index information for collection '%s'.", indexDefinition.getCollection()), e);
        }

        return null;
    }

}
