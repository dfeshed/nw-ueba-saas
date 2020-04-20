package presidio.webapp.service;


import fortscale.utils.rest.jsonpatch.JsonPatch;
import presidio.webapp.model.*;

public interface RestEntityService {
    Entity getEntityByDocumentId(String entityDocumentId, boolean expand);

    EntitiesWrapper getEntities(EntityQuery entityQuery);

    AlertsWrapper getAlertsByEntityDocumentId(String entityDocumentId);

    Entity updateEntity(String entityDocumentId, JsonPatch updateRequest);

    EntitiesWrapper updateEntities(EntityQuery entityQuery, JsonPatch jsonPatch);
}
