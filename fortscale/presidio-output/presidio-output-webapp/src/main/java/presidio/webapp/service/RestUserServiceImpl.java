package presidio.webapp.service;


import fortscale.utils.logging.Logger;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import presidio.output.domain.records.users.UserSeverity;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.model.Alert;
import presidio.webapp.model.User;
import presidio.webapp.model.UserQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
        List<Alert> alert = null;
        presidio.output.domain.records.users.User user = userPersistencyService.findUserById(userId);
        if (expand)
            alert = restAlertService.getAlertsByUserId(userId);
        return createResult(user, alert);
    }

    @Override
    public List<User> getUsers(UserQuery userQuery) {
        Page<presidio.output.domain.records.users.User> users = userPersistencyService.find(convertUserQuery(userQuery));
        List<User> restUsers = new ArrayList<>();
        for (presidio.output.domain.records.users.User user : users) {
            List<Alert> alert = null;
            if (userQuery.getExpand())
                alert = restAlertService.getAlertsByUserId(user.getId());
            restUsers.add(createResult(user, alert));
        }
        return restUsers;
    }

    @Override
    public User createResult(presidio.output.domain.records.users.User user, List<Alert> alerts) {
        User convertedUser = new User();
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
        convertedUser.setAlertclassifications(user.getAlertClassifications());
        return convertedUser;
    }

    @Override
    public List<Alert> getAlertsByUserId(String userId) {
        return restAlertService.getAlertsByUserId(userId);
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
        if (userQuery.getSeverity() != null) {
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
        if (CollectionUtils.isNotEmpty(userQuery.getSort())) {
            try {
                List<Sort.Order> orders = new ArrayList<>();
                userQuery.getSort().forEach(s -> {
                    String[] params = s.split(":");
                    Sort.Direction direction = Sort.Direction.fromString(params[0]);
                    orders.add(new Sort.Order(direction, params[1]));

                });
                builder.sortField(new Sort(orders));
            } catch (Exception e) {
                logger.error("Unable to pars sort list. the list to sort was {}, got exception {}", userQuery.getSort().toArray(), e);
            }
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
