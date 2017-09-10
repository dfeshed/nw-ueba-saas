package presidio.webapp.controllers.users;

import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import presidio.webapp.model.Alert;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.Patch;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;
import presidio.webapp.model.*;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.service.RestUserService;

import java.util.List;

@Controller
public class UsersApiController implements UsersApi {

    private final RestUserService restUserService;
    private final RestAlertService restAlertService;


    public UsersApiController(RestUserService restUserService, RestAlertService restAlertService) {
        this.restUserService = restUserService;
        this.restAlertService = restAlertService;
    }

    @Override
    public ResponseEntity<AlertsWrapper> getAlertsByUser(String userId) {
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
    public ResponseEntity<User> getUser(@ApiParam(name = "userId", value = "The UUID of the user to return", required = true) @PathVariable String userId) {
        User user = restUserService.getUserById(userId);
        return new ResponseEntity(user, HttpStatus.OK);
    }

    @Override
    public ResponseEntity<UsersWrapper> getUsers(presidio.webapp.model.UserQuery userQuery) {
        return new ResponseEntity(new UsersWrapper().users(restUserService.getUsers(userQuery)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<User> updateUser(List<Patch> patch) {
        return null;
    }
}
