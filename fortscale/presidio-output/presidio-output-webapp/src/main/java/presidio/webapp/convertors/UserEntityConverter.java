package presidio.webapp.convertors;

import org.springframework.util.ObjectUtils;
import presidio.webapp.model.*;

import java.util.ArrayList;
import java.util.List;

public class UserEntityConverter {

    public static User entityToUser(Entity entity) {
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

    public static EntityQuery userQueryToEntityQuery(UserQuery userQuery) {
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

    public static UsersWrapper entitiesWrapperToUsersWrapper(EntitiesWrapper entitiesWrapper) {
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

    private static List<User> entitiesToUsers(List<Entity> entities) {
        if (ObjectUtils.isEmpty(entities)) {
            return null;
        }
        List<User> users = new ArrayList<>();
        entities.forEach(entity -> users.add(entityToUser(entity)));
        return users;
    }

    private static List<EntityQueryEnums.EntityQuerySortFieldName> convertSortFields(List<UserQueryEnums.UserQuerySortFieldName> sortFields) {
        if (ObjectUtils.isEmpty(sortFields)) {
            return null;
        }
        List<EntityQueryEnums.EntityQuerySortFieldName> entityFields = new ArrayList<>();
        sortFields.forEach(fieldName -> entityFields.add(userSortFieldToEntitySortField(fieldName)));
        return entityFields;
    }

    private static EntityQueryEnums.EntityQuerySortFieldName userSortFieldToEntitySortField(UserQueryEnums.UserQuerySortFieldName userQuerySortFieldName) {
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

    private static List<EntityQueryEnums.EntitySeverity> convertSeverities(List<UserQueryEnums.UserSeverity> userSeverities) {
        if (ObjectUtils.isEmpty(userSeverities)) {
            return null;
        }
        List<EntityQueryEnums.EntitySeverity> entitySeverities = new ArrayList<>();
        userSeverities.forEach(userSeverity -> entitySeverities.add(
                EntityQueryEnums.EntitySeverity.fromValue(userSeverity.toString())));
        return entitySeverities;
    }

    private static List<EntityQueryEnums.EntityQueryAggregationFieldName> convertAggregationFields(List<UserQueryEnums.UserQueryAggregationFieldName> userQueryAggregationFieldNames) {
        if (ObjectUtils.isEmpty(userQueryAggregationFieldNames)) {
            return null;
        }
        List<EntityQueryEnums.EntityQueryAggregationFieldName> entityQueryAggregationFieldNames = new ArrayList<>();
        userQueryAggregationFieldNames.forEach(userQueryAggregationFieldName -> entityQueryAggregationFieldNames.add(
                EntityQueryEnums.EntityQueryAggregationFieldName.fromValue(userQueryAggregationFieldName.toString())));
        return entityQueryAggregationFieldNames;
    }
}
