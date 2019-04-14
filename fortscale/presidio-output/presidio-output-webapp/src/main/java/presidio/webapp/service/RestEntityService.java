package presidio.webapp.service;


import fortscale.utils.rest.jsonpatch.JsonPatch;
import presidio.webapp.model.*;

public interface RestEntityService {
    Entity getEntityById(String entityId, boolean expand);

    EntitiesWrapper getEntities(EntityQuery entityQuery);

    AlertsWrapper getAlertsByEntityId(String entityId);

    Entity updateEntity(String entityId, JsonPatch updateRequest);

    EntitiesWrapper updateEntities(EntityQuery entityQuery, JsonPatch jsonPatch);
}
