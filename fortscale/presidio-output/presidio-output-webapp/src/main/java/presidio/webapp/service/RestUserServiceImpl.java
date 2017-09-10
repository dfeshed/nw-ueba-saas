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
    private final String TAG_ADMIN = "admin";

    public RestUserServiceImpl(RestAlertService restAlertService, UserPersistencyService userPersistencyService, int pageSize, int pageNumber) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.restAlertService = restAlertService;
        this.userPersistencyService = userPersistencyService;
    }

    @Override
    public User getUserById(String userId) {
        return createResult(userPersistencyService.findUserById(userId));
    }

    @Override
    public List<User> getUsers(UserQuery userQuery) {
        Page<presidio.output.domain.records.users.User> users = userPersistencyService.find(convertUserQuery(userQuery));
        List<User> restUsers = new ArrayList<>();
        for (presidio.output.domain.records.users.User user : users) {
            restUsers.add(createResult(user));
        }
        return restUsers;
    }

    @Override
    public User createResult(presidio.output.domain.records.users.User user) {
        User convertedUser = new User();
        convertedUser.setId(user.getId());
        convertedUser.setUserDisplayName(user.getUserDisplayName());
        convertedUser.setUserSeverity(convertUserSeverity(user.getUserSeverity()));
        convertedUser.setScore((int) user.getUserScore());
        if (user.getAdmin())
            convertedUser.setTags(new ArrayList<>(Arrays.asList(TAG_ADMIN)));
        convertedUser.setUsername(user.getUserName());
        convertedUser.setAlerts(restAlertService.getAlertsByUserId(user.getUserId()));
        return convertedUser;
    }

    @Override
    public List<Alert> getAlertsByUserId(String userId) {
        return restAlertService.getAlertsByUserId(userId);
    }

    private presidio.output.domain.records.users.UserQuery convertUserQuery(UserQuery userQuery) {
        presidio.output.domain.records.users.UserQuery.UserQueryBuilder builder = new presidio.output.domain.records.users.UserQuery.UserQueryBuilder();
        builder.filterByAlertClassifications(userQuery.getClassification());
        builder.filterByUserName(userQuery.getUserName());
        builder.maxScore(userQuery.getMaxScore());
        builder.minScore(userQuery.getMinScore());
        builder.filterByIndicators(userQuery.getIndicatorsType());
        builder.filterBySeverities(convertSeverities(userQuery.getSeverity()));
        builder.pageSize(userQuery.getPageSize());
        builder.pageNumber(userQuery.getPageNumber());
        builder.filterByUserNameWithPrefix(userQuery.getIsPrefix());
        if (!CollectionUtils.isEmpty(userQuery.getTags())) {
            userQuery.getTags().forEach(tag -> {
                if (tag.equals(TAG_ADMIN)) {
                    builder.filterByUserAdmin(true);
                }
            });
        }
        if (!CollectionUtils.isEmpty(userQuery.getSort())) {
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
