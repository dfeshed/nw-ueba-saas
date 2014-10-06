package fortscale.dataqueries;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Factory for creating DataQueryEntities, which read their configuration from properties files.
 */
@Component
public class DataQueryEntityFactory {
    private HashMap<String, DataQueryEntity> entities = new HashMap<String, DataQueryEntity>();

    public DataQueryEntity getDataQueryEntity(String entityId) throws Exception{
        if (!entities.containsKey(entityId)){
            entities.put(entityId, new DataQueryEntity(entityId));
        }

        return entities.get(entityId);
    }
}
