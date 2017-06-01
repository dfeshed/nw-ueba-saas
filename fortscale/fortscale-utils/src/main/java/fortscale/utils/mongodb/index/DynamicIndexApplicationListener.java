package fortscale.utils.mongodb.index;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by barak_schuster on 6/1/17.
 */
public class DynamicIndexApplicationListener implements ApplicationListener<BeforeConvertEvent<Object>> {

    private final MongoTemplate mongoTemplate;
    private final Set<Pair<String, ? extends Class<?>>> collectionToClassIndexCache;

    @Autowired
    public DynamicIndexApplicationListener(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
        this.collectionToClassIndexCache = new HashSet<>();
    }

    /**
     * @return null safe set of fields decorated by given annotation
     */
    public static Set<Field> findAnnotatedFields(Class<?> classs, Class<? extends Annotation> ann) {
        Set<Field> set = new HashSet<>();
        Class<?> c = classs;
        while (c != null) {
            for (Field field : c.getDeclaredFields()) {
                if (field.isAnnotationPresent(ann)) {
                    set.add(field);
                }
            }
            c = c.getSuperclass();
        }
        return set;
    }

    @Override
    public void onApplicationEvent(BeforeConvertEvent<Object> event) {
        String collectionName = event.getCollectionName();

        Class<?> sourceClass = event.getSource().getClass();
        Pair<String, ? extends Class<?>> cacheKey = Pair.of(collectionName, sourceClass);
        if (!collectionToClassIndexCache.contains(cacheKey)) {
            collectionToClassIndexCache.add(cacheKey);
            createClassDynamicIndexes(sourceClass, collectionName);
        }
    }

    private void createClassDynamicIndexes(Class<?> sourceClass, String collectionName) {
        Set<Field> dynamicIndexedAnotatedFields = findAnnotatedFields(sourceClass, DynamicallyIndexed.class);
        Set<Field> fieldAnnotatedFields = findAnnotatedFields(sourceClass, org.springframework.data.mongodb.core.mapping.Field.class);

        fieldAnnotatedFields.retainAll(dynamicIndexedAnotatedFields);
        dynamicIndexedAnotatedFields.removeAll(fieldAnnotatedFields);

        for (Field fieldAnnotatedField : fieldAnnotatedFields) {
            org.springframework.data.mongodb.core.mapping.Field fieldValue =
                    fieldAnnotatedField.getAnnotation(org.springframework.data.mongodb.core.mapping.Field.class);
            String indexName = fieldValue.value();
            ensureIndex(collectionName, indexName);
        }
        for (Field dynamicIndexAnnotatedField : dynamicIndexedAnotatedFields) {
            ensureIndex(collectionName, dynamicIndexAnnotatedField.getName());
        }

    }

    private void ensureIndex(String collectionName, String indexName) {
        mongoTemplate.indexOps(collectionName).ensureIndex(new Index(indexName, Sort.Direction.DESC));
    }
}
