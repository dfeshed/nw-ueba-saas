package presidio.output.processor.services.entity;

import java.util.List;

/**
 * Created by efratn on 22/08/2017.
 */
public class EntityDetails {
    private String entityName;
    private String entityId;
    private String entityType;
    private List<String> tags;

    public EntityDetails(String entityName, String entityId, List<String> tags, String entityType) {
        this.entityName = entityName;
        this.entityId = entityId;
        this.tags = tags;
        this.entityType = entityType;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public List<String> getTags() {
        return tags;
    }

    public String getEntityType() {
        return entityType;
    }

}