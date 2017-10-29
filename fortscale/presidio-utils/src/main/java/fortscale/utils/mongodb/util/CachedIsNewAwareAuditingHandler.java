package fortscale.utils.mongodb.util;

import org.springframework.data.auditing.IsNewAwareAuditingHandler;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.support.IsNewStrategy;
import org.springframework.data.support.IsNewStrategyFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by barak_schuster on 10/29/17.
 */
public class CachedIsNewAwareAuditingHandler extends IsNewAwareAuditingHandler {

    private final IsNewStrategyFactory isNewStrategyFactory;
    private Map<Class,Boolean> isAuditableCache;

    public CachedIsNewAwareAuditingHandler(MappingContext<? extends PersistentEntity<?, ?>, ? extends PersistentProperty<?>> mappingContext, IsNewStrategyFactory isNewStrategyFactory) {
        super(mappingContext);
        this.isNewStrategyFactory = isNewStrategyFactory;
        this.isAuditableCache = new HashMap<>();
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
            markCreated(object);
        } else {
            markModified(object);
        }
    }
}
