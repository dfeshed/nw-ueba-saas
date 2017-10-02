package fortscale.utils.mongodb.index;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

/**
 * Once a new collection is created for a {@link Document}, indexes defined by the following annotations are
 * also created dynamically, if they are present in the {@link Document}: {@link Indexed}, {@link CompoundIndex},
 * {@link CompoundIndexes} and {@link DynamicIndexing}. The call to {@link #onApplicationEvent(BeforeConvertEvent)}
 * is triggered right before elements are converted to Mongo objects and inserted.
 *
 * @author Barak Schuster
 * @author Lior Govrin
 */
public class DynamicIndexingApplicationListener implements ApplicationListener<BeforeConvertEvent<Object>> {
    private final DynamicIndexingService dynamicIndexingService;

    /**
     * C'tor.
     *
     * @param mappingContext {@link MongoMappingContext}
     * @param mongoTemplate  {@link MongoTemplate}
     */
    public DynamicIndexingApplicationListener(MongoMappingContext mappingContext, MongoTemplate mongoTemplate) {
        this.dynamicIndexingService = new DynamicIndexingService(mappingContext, mongoTemplate);
    }

    @Override
    public void onApplicationEvent(BeforeConvertEvent<Object> event) {
        ensureDynamicIndexesExist(event.getSource().getClass(), event.getCollectionName());
    }

    /**
     * A source class that is not annotated with {@link Document} is ignored. A source class that
     * is annotated with {@link Document}, but represents a static collection, is also ignored.
     *
     * @param sourceClass    may contain annotations that define the collection's indexes
     * @param collectionName the name of the collection that needs to be checked
     * @see DynamicIndexingService#ensureDynamicIndexesExist(Class, String)
     */
    public void ensureDynamicIndexesExist(Class<?> sourceClass, String collectionName) {
        Document document = sourceClass.getAnnotation(Document.class);
        // Ignore if the class is not annotated with Document, or if a collection name is defined
        // in the annotation (i.e. the option is not blank), because it is not a dynamic collection.
        if (document == null || StringUtils.isNotBlank(document.collection())) return;
        dynamicIndexingService.ensureDynamicIndexesExist(sourceClass, collectionName);
    }
}
