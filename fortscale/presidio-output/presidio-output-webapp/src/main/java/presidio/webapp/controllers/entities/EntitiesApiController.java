package presidio.webapp.controllers.entities;

import fortscale.utils.logging.Logger;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import fortscale.utils.rest.jsonpatch.JsonPatchOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import presidio.webapp.model.*;
import presidio.webapp.service.RestEntityService;

import java.util.ArrayList;
import java.util.List;

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
    public ResponseEntity<AlertsWrapper> getAlertsByUser(@PathVariable String userId, EntityAlertsQuery entityAlertsQuery) {
        return getAlertsByEntity(userId, entityAlertsQuery);
    }

    @Override
    public ResponseEntity<Entity> getEntity(@ApiParam(name = "entityId", value = "The UUID of the entity to return", required = true) @PathVariable String entityId,
                                          @ApiParam(value = "Expand response to get entity alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {
        return toResponseEntity(restEntityService.getEntityById(entityId, expand), entityId);
    }

    @Override
    public ResponseEntity<User> getUser(@ApiParam(name = "userId", value = "The UUID of the user to return", required = true) @PathVariable String userId,
                                        @ApiParam(value = "Expand response to get user alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {
        return toResponseEntity(entityToUser(restEntityService.getEntityById(userId, expand)), userId);
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
    public ResponseEntity<UsersWrapper> getUsers(presidio.webapp.model.UserQuery userQuery) {
        try {
            UsersWrapper usersWrapper = entitiesWrapperToUsersWrapper(
                    restEntityService.getEntities(userQueryToEntityQuery(userQuery)));
            return new ResponseEntity(usersWrapper, HttpStatus.OK);
        } catch (Exception ex) {
            logger.error("Trying to get users with userQuery:{}, but got exception {}", userQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<Entity> updateEntity(@ApiParam(name = "entityId", value = "The UUID of the entity to return", required = true) @PathVariable String entityId, @RequestBody JsonPatch jsonPatch) {
        if (updateRequestIsInvalid(jsonPatch)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(restEntityService.updateEntity(entityId, jsonPatch), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> updateUser(@ApiParam(name = "userId", value = "The UUID of the user to return", required = true) @PathVariable String userId, @RequestBody JsonPatch jsonPatch) {
        if (updateRequestIsInvalid(jsonPatch)) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(entityToUser(restEntityService.updateEntity(userId, jsonPatch)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<EntitiesWrapper> updateEntities(@RequestBody EntityPatchBody entityPatchBody) {

        if (updateRequestIsInvalid(entityPatchBody.getJsonPatch())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(restEntityService.updateEntities(entityPatchBody.getEntityQuery(), entityPatchBody.getJsonPatch()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UsersWrapper> updateUsers(@RequestBody UserPatchBody userPatchBody) {

        if (updateRequestIsInvalid(userPatchBody.getJsonPatch())) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(
                entitiesWrapperToUsersWrapper(
                        restEntityService.updateEntities(
                                userQueryToEntityQuery(
                                        userPatchBody.getUserQuery()), userPatchBody.getJsonPatch())), HttpStatus.OK);
    }



    private boolean updateRequestIsInvalid(JsonPatch jsonPatch) {
        for (JsonPatchOperation jsonPatchOperation : jsonPatch.getOperations()) {
            if (!jsonPatchOperation.getPath().toString().contains("tags")) {
                return true;
            }
        }
        return false;
    }

    private User entityToUser(Entity entity) {
        if (ObjectUtils.isEmpty(entity)) {
            return null;
        }
        User user = new User();
        user.setId(entity.getId());
        user.setUserId(entity.getEntityId());
        user.setUsername(entity.getEntityName());
        user.setUserDisplayName(entity.getEntityName().toLowerCase());
        user.setTags(entity.getTags());
        user.setScore(entity.getScore());
        user.setSeverity(UserQueryEnums.UserSeverity.fromValue(entity.getSeverity().name()));
        user.setAlertsCount(entity.getAlertsCount());
        user.setAlerts(entity.getAlerts());
        user.setAlertClassifications(entity.getAlertClassifications());
        return user;
    }

    private EntityQuery userQueryToEntityQuery(UserQuery userQuery) {
        if (ObjectUtils.isEmpty(userQuery)) {
            return null;
        }
        EntityQuery entityQuery = new EntityQuery();
        entityQuery.setEntityName(userQuery.getUserName());
        entityQuery.setFreeText(userQuery.getFreeText());
        entityQuery.setPageSize(userQuery.getPageSize());
        entityQuery.setPageNumber(userQuery.getPageNumber());
        entityQuery.setMinScore(userQuery.getMinScore());
        entityQuery.setMaxScore(userQuery.getMaxScore());
        entityQuery.setIsPrefix(userQuery.getIsPrefix());
        entityQuery.setTags(userQuery.getTags());
        entityQuery.setAlertClassifications(userQuery.getAlertClassifications());
        entityQuery.setIndicatorsName(userQuery.getIndicatorsName());
        entityQuery.setSortFieldNames(convertSortFields(userQuery.getSortFieldNames()));
        entityQuery.setSeverity(convertSeverities(userQuery.getSeverity()));
        entityQuery.setExpand(userQuery.getExpand());
        entityQuery.setSortDirection(userQuery.getSortDirection());
        entityQuery.setAggregateBy(convertAggregationFields(userQuery.getAggregateBy()));
        return entityQuery;
    }

    private UsersWrapper entitiesWrapperToUsersWrapper(EntitiesWrapper entitiesWrapper) {
        if (ObjectUtils.isEmpty(entitiesWrapper)) {
            return null;
        }
        UsersWrapper usersWrapper = new UsersWrapper();
        usersWrapper.setUsers(entitiesToUsers(entitiesWrapper.getEntities()));
        usersWrapper.setTotal(entitiesWrapper.getTotal());
        usersWrapper.setPage(entitiesWrapper.getPage());
        usersWrapper.setAggregationData(entitiesWrapper.getAggregationData());
        return usersWrapper;
    }

    private List<User> entitiesToUsers(List<Entity> entities) {
        if (ObjectUtils.isEmpty(entities)) {
            return null;
        }
        List<User> users = new ArrayList<>();
        entities.forEach(entity -> users.add(entityToUser(entity)));
        return users;
    }

    private List<EntityQueryEnums.EntityQuerySortFieldName> convertSortFields(List<UserQueryEnums.UserQuerySortFieldName> sortFields) {
        if (ObjectUtils.isEmpty(sortFields)) {
            return null;
        }
        List<EntityQueryEnums.EntityQuerySortFieldName> entityFields = new ArrayList<>();
        sortFields.forEach(fieldName -> entityFields.add(userSortFieldToEntitySortField(fieldName)));
        return entityFields;
    }

    private EntityQueryEnums.EntityQuerySortFieldName userSortFieldToEntitySortField(UserQueryEnums.UserQuerySortFieldName userQuerySortFieldName) {
        if (ObjectUtils.isEmpty(userQuerySortFieldName)) {
            return null;
        }
        EntityQueryEnums.EntityQuerySortFieldName fieldName = EntityQueryEnums.EntityQuerySortFieldName.SCORE;
        if (userQuerySortFieldName == UserQueryEnums.UserQuerySortFieldName.USER_NAME ||
                userQuerySortFieldName == UserQueryEnums.UserQuerySortFieldName.USER_DISPLAY_NAME) {
            fieldName = EntityQueryEnums.EntityQuerySortFieldName.ENTITY_NAME;
        }
        if (userQuerySortFieldName == UserQueryEnums.UserQuerySortFieldName.ALERT_NUM) {
            fieldName = EntityQueryEnums.EntityQuerySortFieldName.ALERT_NUM;
        }
        return fieldName;
    }

    private List<EntityQueryEnums.EntitySeverity> convertSeverities(List<UserQueryEnums.UserSeverity> userSeverities) {
        if (ObjectUtils.isEmpty(userSeverities)) {
            return null;
        }
        List<EntityQueryEnums.EntitySeverity> entitySeverities = new ArrayList<>();
        userSeverities.forEach(userSeverity -> entitySeverities.add(
                EntityQueryEnums.EntitySeverity.fromValue(userSeverity.toString())));
        return entitySeverities;
    }

    private List<EntityQueryEnums.EntityQueryAggregationFieldName> convertAggregationFields(List<UserQueryEnums.UserQueryAggregationFieldName> userQueryAggregationFieldNames) {
        if (ObjectUtils.isEmpty(userQueryAggregationFieldNames)) {
            return null;
        }
        List<EntityQueryEnums.EntityQueryAggregationFieldName> entityQueryAggregationFieldNames = new ArrayList<>();
        userQueryAggregationFieldNames.forEach(userQueryAggregationFieldName -> entityQueryAggregationFieldNames.add(
                EntityQueryEnums.EntityQueryAggregationFieldName.fromValue(userQueryAggregationFieldName.toString())));
        return entityQueryAggregationFieldNames;
    }

    private <T>ResponseEntity toResponseEntity(T response, String id) {
        try {
            HttpStatus httpStatus = response != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(response, httpStatus);
        } catch (Exception ex) {
            assert response != null;
            logger.error("Trying to get entity with id:{}, but got exception {}", id, ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
