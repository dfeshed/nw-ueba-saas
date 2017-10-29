package fortscale.utils.mongodb.util;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.data.support.IsNewStrategyFactory;
import org.springframework.data.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by barak_schuster on 10/29/17.
 */
public class CachedIsNewAwareAuditingHandler extends IsNewAwareAuditingHandler {

    private final IsNewStrategyFactory isNewStrategyFactory;
    private Map<Class,Boolean> isAuditableCache;
    private Map<Class,Field> classCreatedDateField;
    private Map<Class,Field>  classLastModifiedDateField;

    public CachedIsNewAwareAuditingHandler(MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext, IsNewStrategyFactory isNewStrategyFactory) {
        super(mappingContext);
        this.isNewStrategyFactory = isNewStrategyFactory;
        this.isAuditableCache = new HashMap<>();
        this.classCreatedDateField = new HashMap<>();
        this.classLastModifiedDateField = new HashMap<>();
    }

    @Override
    public void markAudited(Object object) {
        Class<?> objectClass = object.getClass();
        Boolean isAuditable = this.isAuditableCache.get(objectClass);

        if(isAuditable == null)
        {
            isAuditable = isAuditable(object);
            isAuditableCache.put(objectClass,isAuditable);
        }

        if (!isAuditable) {
            return;
        }

        IsNewStrategy strategy = isNewStrategyFactory.getIsNewStrategy(objectClass);

        if (strategy.isNew(object)) {
            Field field = classCreatedDateField.computeIfAbsent(objectClass, x -> computeClassField(objectClass,CreatedDate.class));
            ReflectionUtils.setField(field,object, Instant.now());
        } else {
            Field field = classLastModifiedDateField.computeIfAbsent(objectClass, x -> computeClassField(objectClass,LastModifiedDate.class));
            ReflectionUtils.setField(field,object, Instant.now());
        }
    }

    private static Field computeClassField(Class<?> objectClass, Class<? extends Annotation> annotation) {
        // creation date marking works faster if skipping super.touch() - since it contains redundant converters allocations
        ReflectionUtils.AnnotationFieldFilter annotationFieldFilter = new ReflectionUtils.AnnotationFieldFilter(annotation);

        return ReflectionUtils.findField(objectClass, annotationFieldFilter, true);
    }
}
