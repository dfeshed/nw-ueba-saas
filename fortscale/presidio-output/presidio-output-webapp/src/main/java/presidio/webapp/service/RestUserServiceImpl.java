package presidio.webapp.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.json.ObjectMapperProvider;
import fortscale.utils.logging.Logger;
import fortscale.utils.rest.jsonpatch.JsonPatch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.elasticsearch.search.aggregations.Aggregation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.util.ObjectUtils;
import presidio.output.commons.services.alert.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.model.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RestUserServiceImpl implements RestUserService {

    private static final Logger logger = Logger.getLogger(RestUserServiceImpl.class);


    private final RestAlertService restAlertService;
    private final UserPersistencyService userPersistencyService;
    private final int pageNumber;
    private final int pageSize;
    private ObjectMapper objectMapper;

    public RestUserServiceImpl(RestAlertService restAlertService, UserPersistencyService userPersistencyService, int pageSize, int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.restAlertService = restAlertService;
        this.userPersistencyService = userPersistencyService;
        objectMapper = ObjectMapperProvider.defaultJsonObjectMapper();
    }

    @Override
    public User getUserById(String userId, boolean expand) {
        List<Alert> alerts = null;
        presidio.output.domain.records.users.User user = userPersistencyService.findUserById(userId);
        if (expand)
            alerts = restAlertService.getAlertsByUserId(userId, false).getAlerts();
        return createResult(user, alerts);
    }

    @Override
    public UsersWrapper getUsers(UserQuery userQuery) {
        Page<presidio.output.domain.records.users.User> users = userPersistencyService.find(convertUserQuery(userQuery));
        List<User> restUsers = new ArrayList<>();

        if (users != null && users.getNumberOfElements() > 0) {
            Map<String, List<Alert>> usersIdsToAlertsMap = new HashMap<>();

            // Get the alert data for the received users
            if (userQuery.getExpand()) {
                List<String> usersIds = new ArrayList<>();
                for (presidio.output.domain.records.users.User user : users) {
                    usersIds.add(user.getId());
                }

                usersIdsToAlertsMap = restAlertService.getAlertsByUsersIds(usersIds);
            }

            // Create the rest response
            for (presidio.output.domain.records.users.User user : users) {
                List<Alert> alertList = null;
                if (MapUtils.isNotEmpty(usersIdsToAlertsMap)) {
                    alertList = usersIdsToAlertsMap.get(user.getId());
                }
                restUsers.add(createResult(user, alertList));
            }

            Map<String, Aggregation> userAggregationsMap = null;
            if (CollectionUtils.isNotEmpty(userQuery.getAggregateBy())) {
                userAggregationsMap = ((AggregatedPageImpl<presidio.output.domain.records.users.User>) users).getAggregations().asMap();
            }
            return createUsersWrapper(restUsers, ((Long) users.getTotalElements()).intValue(), userQuery.getPageNumber(), userAggregationsMap);
        } else {
            return createUsersWrapper(null, 0, 0, null);
        }
    }

    private UsersWrapper createUsersWrapper(List Users, int totalNumberOfElements, Integer pageNumber, Map<String, Aggregation> userAggregationsMap) {
        UsersWrapper usersWrapper = new UsersWrapper();
        if (CollectionUtils.isNotEmpty(Users)) {
            usersWrapper.setUsers(Users);
            usersWrapper.setTotal(totalNumberOfElements);
            if (pageNumber != null) {
                usersWrapper.setPage(pageNumber);
            }
            if (MapUtils.isNotEmpty(userAggregationsMap)) {
                Map<String, String> aggregationNamesEnumMapping = new HashMap<>();
                userAggregationsMap.keySet().forEach(aggregationName -> {
                    aggregationNamesEnumMapping.put(aggregationName, UserQueryEnums.UserQueryAggregationFieldName.fromValue(aggregationName).name());
                });
                Map<String, Map<String, Long>> aggregations = RestUtils.convertAggregationsToMap(userAggregationsMap, aggregationNamesEnumMapping);
                usersWrapper.setAggregationData(aggregations);
            }
        } else {
            usersWrapper.setUsers(new ArrayList());
            usersWrapper.setTotal(0);
            usersWrapper.setPage(0);
        }

        return usersWrapper;
    }


    private User createResult(presidio.output.domain.records.users.User user, List<Alert> alerts) {
        User convertedUser = new User();
        if (ObjectUtils.isEmpty(user))
            return null;
        convertedUser.setId(user.getId());
        convertedUser.setUserId(user.getUserId());
        if (CollectionUtils.isNotEmpty(alerts))
            convertedUser.setAlerts(alerts);
        convertedUser.setUserDisplayName(user.getUserDisplayName());
        if (user.getSeverity() != null) {
            convertedUser.setSeverity(convertUserSeverity(user.getSeverity()));
        }
        convertedUser.setScore((int) user.getScore());
        convertedUser.setTags(user.getTags());
        convertedUser.setUsername(user.getUserName());
        convertedUser.setAlertClassifications(user.getAlertClassifications());
        convertedUser.setAlertsCount(user.getAlertsCount());
        return convertedUser;
    }

    @Override
    public AlertsWrapper getAlertsByUserId(String userId) {
        return restAlertService.getAlertsByUserId(userId, false);
    }

    @Override
    public User updateUser(String userId, JsonPatch updateRequest) {
        presidio.output.domain.records.users.User userById = userPersistencyService.findUserById(userId);
        userById = patchUser(updateRequest, userById);
        userPersistencyService.save(userById);
        return createResult(userById, null);
    }

    private presidio.output.domain.records.users.User patchUser(JsonPatch updateRequest, presidio.output.domain.records.users.User userById) {
        JsonNode patchedJson;
        try {
            JsonNode userJsonNode = objectMapper.valueToTree(userById);
            patchedJson = updateRequest.apply(userJsonNode);
            userById = objectMapper.treeToValue(patchedJson, presidio.output.domain.records.users.User.class);
        } catch (Exception e) {
            logger.error("Error parsing or processing  the user object to or from json", e);
        }
        return userById;
    }

    @Override
    public UsersWrapper updateUsers(UserQuery userQuery, JsonPatch jsonPatch) {
        Page<presidio.output.domain.records.users.User> users = userPersistencyService.find(convertUserQuery(userQuery));

        List<presidio.output.domain.records.users.User> updatedUsers = new ArrayList<>();
        users.getContent().forEach(user -> {
            updatedUsers.add(patchUser(jsonPatch, user));
        });

        userPersistencyService.save(updatedUsers);
        return createUsersWrapper(updatedUsers, users.getNumberOfElements(), users.getNumber(), null);
    }

    private presidio.output.domain.records.users.UserQuery convertUserQuery(UserQuery userQuery) {
        presidio.output.domain.records.users.UserQuery.UserQueryBuilder builder = new presidio.output.domain.records.users.UserQuery.UserQueryBuilder();
        if (CollectionUtils.isNotEmpty(userQuery.getAlertClassifications())) {
            builder.filterByAlertClassifications(userQuery.getAlertClassifications());
        }
        if (userQuery.getUserName() != null) {
            builder.filterByUserName(userQuery.getUserName());
        }
        if (userQuery.getIndicatorsName() != null) {
            builder.filterByIndicators(userQuery.getIndicatorsName());
        }
        if (userQuery.getFreeText() != null) {
            builder.filterByFreeText(userQuery.getFreeText());
        }
        if (userQuery.getMaxScore() != null) {
            builder.maxScore(userQuery.getMaxScore());
        }
        if (userQuery.getMinScore() != null) {
            builder.minScore(userQuery.getMinScore());
        }
        if (CollectionUtils.isNotEmpty(userQuery.getSeverity())) {
            builder.filterBySeverities(convertSeverities(userQuery.getSeverity()));
        }
        if (userQuery.getPageSize() != null) {
            builder.pageSize(userQuery.getPageSize());
        }
        if (userQuery.getPageNumber() != null) {
            builder.pageNumber(userQuery.getPageNumber());
        }
        if (userQuery.getIsPrefix() != null) {
            builder.filterByUserNameWithPrefix(userQuery.getIsPrefix());
        }
        if (CollectionUtils.isNotEmpty(userQuery.getTags())) {
            builder.filterByUserTags(userQuery.getTags());
        }
        if (CollectionUtils.isNotEmpty(userQuery.getSortFieldNames()) && userQuery.getSortDirection() != null) {
            List<Sort.Order> orders = new ArrayList<>();
            userQuery.getSortFieldNames().forEach(s -> {
                orders.add(new Sort.Order(userQuery.getSortDirection(), s.toString()));
            });
            builder.sort(new Sort(orders));
        }
        if (CollectionUtils.isNotEmpty(userQuery.getAggregateBy())) {
            List<String> aggregateByFields = new ArrayList<>();
            userQuery.getAggregateBy().forEach(alertQueryAggregationFieldName -> {
                aggregateByFields.add(alertQueryAggregationFieldName.toString());
            });
            builder.aggregateByFields(aggregateByFields);
        }

        return builder.build();
    }

    private List<UserSeverity> convertSeverities(List<presidio.webapp.model.UserQueryEnums.UserSeverity> severityEnumList) {
        List<UserSeverity> userSeverity = new ArrayList<>();
        severityEnumList.forEach(severity -> {
            userSeverity.add(UserSeverity.valueOf(severity.toString()));
        });
        return userSeverity;
    }

    private presidio.webapp.model.UserQueryEnums.UserSeverity convertUserSeverity(UserSeverity userSeverity) {
        return presidio.webapp.model.UserQueryEnums.UserSeverity.valueOf(userSeverity.name());
    }
}
