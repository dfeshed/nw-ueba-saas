package presidio.webapp.controllers.users;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import presidio.output.domain.records.users.UserQuery;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.Patch;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.service.RestUserService;

import java.util.ArrayList;
import java.util.List;


public class UsersApiController implements UsersApi {

    private final RestUserService restUserService;
    private final RestAlertService restAlertService;


    public UsersApiController(RestUserService restUserService, RestAlertService restAlertService) {
        this.restUserService = restUserService;
        this.restAlertService = restAlertService;
    }

    @Override
    public ResponseEntity<UsersWrapper> usersGet(Integer pageSize, Integer pageNumber, String name, Integer minScore, Integer maxScore, List<String> tags, List<String> sort, List<String> classification, List<String> indicatorsType, String severity, Boolean isPrefix) {
        List<Sort.Order> orders = new ArrayList<>();
        if (sort != null) {
            sort.forEach(s -> {
                String[] params = s.split(":");
                Sort.Direction direction = Sort.Direction.fromString(params[0]);
                orders.add(new Sort.Order(direction, params[1]));

            });
        }
        UserQuery userQurey = new UserQuery.UserQueryBuilder().filterByUserName(name).sort(new Sort(orders)).filterByUserNameWithPrefix(isPrefix).build();
        List<User> users = restUserService.getUsers(userQurey);
        UsersWrapper usersWrapper = new UsersWrapper();
        if (users != null) {
            usersWrapper.users(users);
        }
        return new ResponseEntity(usersWrapper, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<AlertsWrapper> usersUserIdAlertsGet(String userId) {
        List<Alert> alerts = restAlertService.getAlertsByUserId(userId);
        AlertsWrapper alertsWrapper = new AlertsWrapper();
        if (!CollectionUtils.isEmpty(alerts)) {
            alertsWrapper.setAlerts(alerts);
            alertsWrapper.setTotal(alerts.size());
            alertsWrapper.setPage(0);
        }
        return new ResponseEntity(alertsWrapper, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> usersUserIdGet(String userId) {
        User user = restUserService.getUserById(userId);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> usersUserIdPatch(List<Patch> patch) {
        return null;
    }
}
