package presidio.webapp.controllers.users;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import presidio.output.domain.records.users.UserQuery;
import presidio.webapp.model.Alert;
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
    public ResponseEntity<UsersWrapper> usersGet(String name, Integer minScore, Integer maxScore, List<String> tags, Integer limit, Integer offset, String sort) {
        UserQuery userQurey = new UserQuery.UserQueryBuilder().filterByUserName(name).sortField(sort, false).build();
        List<User> users = restUserService.getUsers(userQurey);
        UsersWrapper usersWrapper = new UsersWrapper().users(users);
        return new ResponseEntity<UsersWrapper>(usersWrapper, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> usersUserIdAlertsGet(String userId) {
        Page<Alert> alerts = restAlertService.getAlertsByUserId(userId);
        List<Alert> restAlerts = new ArrayList<>();
        alerts.forEach(alert -> restAlerts.add(alert));


        return null;
    }

    @Override
    public ResponseEntity<User> usersUserIdGet(String userId) {
        User user = restUserService.getUserById(userId);
        return new ResponseEntity<User>(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> usersUserIdPatch(List<Patch> patch) {
        return null;
    }
}
