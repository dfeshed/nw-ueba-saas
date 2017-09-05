package presidio.webapp.service;


import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import presidio.output.domain.services.users.UserPersistencyService;
import presidio.webapp.model.Alert;
import presidio.webapp.model.User;
import presidio.webapp.model.UserQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by maors on 9/3/2017.
 */
public class RestUserServiceImpl implements RestUserService {

    private final RestAlertService restAlertService;
    private final UserPersistencyService userPersistencyService;
    private final int PAGE = 0;
    private final int SIZE = 100;

    public RestUserServiceImpl(RestAlertService restAlertService, UserPersistencyService userPersistencyService) {
        this.restAlertService = restAlertService;
        this.userPersistencyService = userPersistencyService;
    }

    @Override
    public User getUserById(String userId) {
        return createResult(userPersistencyService.findByUserId(userId, new PageRequest(PAGE, SIZE)).iterator().next());
    }

    @Override
    public List<User> getUsers(UserQuery userQurey) {
        Page<presidio.output.domain.records.users.User> users = userPersistencyService.find(convert(userQurey));
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
        convertedUser.setScore((int) user.getUserScore());
        if (user.getAdmin())
            convertedUser.setTags(new ArrayList<>(Arrays.asList("admin")));
        convertedUser.setUsername(user.getUserName());
        convertedUser.setAlerts(restAlertService.getAlertsByUserId(user.getUserId()));
        return convertedUser;
    }

    @Override
    public List<Alert> getAlertsByUserId(String userId) {
        return restAlertService.getAlertsByUserId(userId);
    }

    private presidio.output.domain.records.users.UserQuery convert(UserQuery userQurey) {
        presidio.output.domain.records.users.UserQuery.UserQueryBuilder builder = new presidio.output.domain.records.users.UserQuery.UserQueryBuilder();
        builder.filterByAlertClassifications(userQurey.getClassification());
        builder.filterByUserName(userQurey.getUserName());
        builder.maxScore(userQurey.getMaxScore());
        builder.minScore(userQurey.getMinScore());
        builder.filterByIndicators(userQurey.getIndicatorsType());
        builder.pageSize(userQurey.getPageSize());
        builder.pageNumber(userQurey.getPageNumber());
        if (!CollectionUtils.isEmpty(userQurey.getSort())) {
            List<Sort.Order> orders = new ArrayList<>();
            userQurey.getSort().forEach(s -> {
                String[] params = s.split(":");
                Sort.Direction direction = Sort.Direction.fromString(params[0]);
                orders.add(new Sort.Order(direction, params[1]));

            });
            builder.sortField(new Sort(orders));
        }

        return builder.build();
    }


}
