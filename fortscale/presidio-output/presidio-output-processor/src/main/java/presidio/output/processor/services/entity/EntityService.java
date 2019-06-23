package presidio.output.processor.services.entity;

import presidio.output.domain.records.entity.Entity;

import java.time.Instant;
import java.util.List;

/**
 * Created by efratn on 22/08/2017.
 */
public interface EntityService {
    Entity createEntity(String entityId, String entityType);

    List<Entity> save(List<Entity> Entities);

    Entity findEntityById(String entityId);

    void addEntityAlertData(Entity entity, EntitiesAlertData entitiesAlertData);

    void setEntityAlertData(Entity entity, EntitiesAlertData entitiesAlertData);

    void setEntityAlertDataToDefault(Entity entity);

    void recalculateEntityAlertData(Entity entity);

    List<Entity> findEntityByVendorEntityIdAndType(String vendorEntityId, String entityType);

    /**
     * Recalculate all alerts related data on the entity for the last X days (configurable).
     * Recalculating- entity score, alerts count and classification
     *
     * @return
     */
    boolean updateAllEntitiesAlertData(Instant endDate, String entityType);


    void updateEntityData(Instant endDate, String entityType);

}
