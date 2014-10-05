package fortscale.dataqueries;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * Factory for creating DataQueryEntities, which read their configuration from properties files.
 */
public class DataQueryEntityFactory {
    @Autowired
    private Map<String, DataQueryEntity> entities;

    public DataQueryEntity getDataQueryEntity(String entityId) throws Exception{
        if (!entities.containsKey(entityId)){
            entities.put(entityId, new DataQueryEntity(entityId));
        }

        return entities.get(entityId);
    }
}
