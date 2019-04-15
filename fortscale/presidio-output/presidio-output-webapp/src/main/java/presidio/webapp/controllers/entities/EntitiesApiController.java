package presidio.webapp.controllers.entities;

import fortscale.utils.logging.Logger;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import fortscale.utils.rest.jsonpatch.JsonPatchOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import presidio.webapp.model.*;
import presidio.webapp.service.RestEntityService;

@Controller
public class EntitiesApiController implements EntitiesApi {

    private final Logger logger = Logger.getLogger(EntitiesApiController.class);

    private final RestEntityService restEntityService;


    public EntitiesApiController(RestEntityService restEntityService) {
        this.restEntityService = restEntityService;
    }

    @Override
    public ResponseEntity<AlertsWrapper> getAlertsByEntity(@PathVariable String entityId, EntityAlertsQuery entityAlertsQuery) {
        try {
            AlertsWrapper alertsWrapper = restEntityService.getAlertsByEntityId(entityId);
            HttpStatus httpStatus = alertsWrapper.getTotal() > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(alertsWrapper, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying to get alerts by entity id with entityAlertsQuery:{}, but got exception {}", entityAlertsQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Entity> getEntity(@ApiParam(name = "entityId", value = "The UUID of the entity to return", required = true) @PathVariable String entityId,
                                          @ApiParam(value = "Expand response to get entity alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {
        try {
            Entity entity = restEntityService.getEntityById(entityId, expand);
            HttpStatus httpStatus = entity != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(entity, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying to get entity with id:{}, but got exception {}", entityId, ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<EntitiesWrapper> getEntities(EntityQuery entityQuery) {
        try {
            EntitiesWrapper entitiesWrapper = restEntityService.getEntities(entityQuery);
            return new ResponseEntity(entitiesWrapper, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Trying to get entities with entityQuery:{}, but got exception {}", entityQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Entity> updateEntity(@ApiParam(name = "entityId", value = "The UUID of the entity to return", required = true) @PathVariable String entityId, @RequestBody JsonPatch jsonPatch) {
        if (updateRequestIsInvalid(jsonPatch)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(restEntityService.updateEntity(entityId, jsonPatch), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EntitiesWrapper> updateEntities(@RequestBody EntityPatchBody entityPatchBody) {

        if (updateRequestIsInvalid(entityPatchBody.getJsonPatch())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(restEntityService.updateEntities(entityPatchBody.getEntityQuery(), entityPatchBody.getJsonPatch()), HttpStatus.OK);
    }

    private boolean updateRequestIsInvalid(JsonPatch jsonPatch) {
        for (JsonPatchOperation jsonPatchOperation : jsonPatch.getOperations()) {
            if (!jsonPatchOperation.getPath().toString().contains("tags")) {
                return true;
            }
        }
        return false;
    }
}
