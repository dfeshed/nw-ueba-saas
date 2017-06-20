package fortscale.utils.cleanup;

import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Amir Keren on 24/09/15.
 */
public abstract class CleanupDeletionUtil {

    private static Logger logger = Logger.getLogger(CleanupDeletionUtil.class);

    abstract public Collection<String> getAllEntities();
    abstract public boolean deleteEntities(Collection<String> entities, boolean doValidate);
    abstract public boolean deleteAllEntities(boolean doValidate);

    @Autowired
    private CleanupPredicate cleanupPredicate;

    /***
     *
     * This method filters out topics according to a specific filter
     *
     * @param value   the value the filter should match
     * @param filter  the filter according to which the predicate will work
     * @return
     */
    public Collection<String> getEntitiesMatchingPredicate(String value, String filter) {
        logger.debug("getting all entities matching value [} with predicate {}", value, filter);
        Collection<String> tempEntitiesNames = getAllEntities();
        Set<String> entitiesNames = new HashSet(tempEntitiesNames);
        logger.debug("found {} entities", entitiesNames.size());
        if (value.isEmpty()) {
            return entitiesNames;
        }
        Iterator<String> it = entitiesNames.iterator();
        logger.debug("filtering out entities not matching predicate {}", filter);
        while (it.hasNext()) {
            String entityName = it.next();
            if (!cleanupPredicate.apply(entityName, value, filter)) {
                it.remove();
            }
        }
        logger.info("found {} entities with matching predicate {}", entitiesNames.size(), filter);
        return entitiesNames;
    }

}