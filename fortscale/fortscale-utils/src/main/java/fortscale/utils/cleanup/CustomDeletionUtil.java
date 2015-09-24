package fortscale.utils.cleanup;

import java.util.Collection;

/**
 * Created by Amir Keren on 24/09/15.
 */
public interface CustomDeletionUtil {

    Collection<String> getEntitiesWithPrefix(String prefix);
    boolean deleteEntities(Collection<String> entities, boolean doValidate);

}