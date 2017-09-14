package presidio.webapp.service;


import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.BooleanUtils;
import org.elasticsearch.search.aggregations.Aggregation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.aggregation.impl.AggregatedPageImpl;
import org.springframework.util.ObjectUtils;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.model.Alert;
import presidio.webapp.model.User;
import presidio.webapp.model.UserQuery;
import presidio.webapp.model.UsersWrapper;

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

    public RestUserServiceImpl(RestAlertService restAlertService, UserPersistencyService userPersistencyService, int pageSize, int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.restAlertService = restAlertService;
        this.userPersistencyService = userPersistencyService;
    }

    @Override
    public User getUserById(String userId, boolean expand) {
        List<Alert> alerts = null;
        presidio.output.domain.records.users.User user;
        try {
            user = userPersistencyService.findUserById(userId);
        } catch (Exception ex) {
            return createResult(null, null);
        }
        if (expand)
            alerts = restAlertService.getAlertsByUserId(userId).getAlerts();
        return createResult(user, alerts);
    }

    @Override
    public UsersWrapper getUsers(UserQuery userQuery) {
        Page<presidio.output.domain.records.users.User> users;
        try {
            users = userPersistencyService.find(convertUserQuery(userQuery));
        } catch (Exception ex) {
            return createUsersWrapper(null, 0, 0, null);
        }
        List<User> restUsers = new ArrayList<>();

        if (users != null && users.getSize() > 0) {
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

            Map<String, Aggregation> userAggregationsMap = ((AggregatedPageImpl<presidio.output.domain.records.users.User>) users).getAggregations().asMap();
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
                Map<String, Map<String, Long>> aggregations = RestUtils.convertAggregationsToMap(userAggregationsMap);
                usersWrapper.setAggregationData(aggregations);
            }
        } else {
            usersWrapper.setUsers(new ArrayList());
            usersWrapper.setTotal(0);
            usersWrapper.setPage(0);
        }

        return usersWrapper;
    }

    @Override
    public User createResult(presidio.output.domain.records.users.User user, List<Alert> alerts) {
        User convertedUser = new User();
        if (ObjectUtils.isEmpty(user))
            return null;
        convertedUser.setId(user.getId());
        if (CollectionUtils.isNotEmpty(alerts))
            convertedUser.setAlerts(alerts);
        convertedUser.setUserDisplayName(user.getUserDisplayName());
        if (user.getUserSeverity() != null) {
            convertedUser.setUserSeverity(convertUserSeverity(user.getUserSeverity()));
        }
        convertedUser.setScore((int) user.getScore());
        convertedUser.setTags(user.getTags());
        convertedUser.setUsername(user.getUserName());
        convertedUser.setAlertClassifications(user.getAlertClassifications());
        convertedUser.setAlertsCount(user.getAlertsCount());
        return convertedUser;
    }

    @Override
    public List<Alert> getAlertsByUserId(String userId) {
        return restAlertService.getAlertsByUserId(userId).getAlerts();
    }

    private presidio.output.domain.records.users.UserQuery convertUserQuery(UserQuery userQuery) {
        presidio.output.domain.records.users.UserQuery.UserQueryBuilder builder = new presidio.output.domain.records.users.UserQuery.UserQueryBuilder();
        if (CollectionUtils.isNotEmpty(userQuery.getAlertClassifications())) {
            builder.filterByAlertClassifications(userQuery.getAlertClassifications());
        }
        if (userQuery.getUserName() != null) {
            builder.filterByUserName(userQuery.getUserName());
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
            builder.sortField(new Sort(orders));
        }
        if (BooleanUtils.isTrue(userQuery.getAggregateBySeverity())) {
            builder.aggregateBySeverity(userQuery.getAggregateBySeverity());
        }

        return builder.build();
    }

    private List<UserSeverity> convertSeverities(List<presidio.webapp.model.UserSeverity> severityEnumList) {
        List<UserSeverity> userSeverity = new ArrayList<>();
        severityEnumList.forEach(severity -> {
            userSeverity.add(UserSeverity.valueOf(severity.toString()));
        });
        return userSeverity;
    }

    private presidio.webapp.model.UserSeverity convertUserSeverity(UserSeverity userSeverity) {
        return presidio.webapp.model.UserSeverity.valueOf(userSeverity.name());
    }


}
