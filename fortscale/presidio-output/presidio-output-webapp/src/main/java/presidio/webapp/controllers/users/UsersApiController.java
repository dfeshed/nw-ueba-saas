package presidio.webapp.controllers.users;

import fortscale.utils.logging.Logger;
import io.swagger.annotations.ApiParam;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.Patch;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;
import presidio.webapp.service.RestUserService;

import java.util.List;

@Controller
public class UsersApiController implements UsersApi {

    private final Logger logger = Logger.getLogger(UsersApiController.class);

    private final RestUserService restUserService;


    public UsersApiController(RestUserService restUserService) {
        this.restUserService = restUserService;
    }

    @Override
    public ResponseEntity<AlertsWrapper> getAlertsByUser(@PathVariable String userId, presidio.webapp.model.UserAlertsQuery userAlertsQuery) {
        try {
            AlertsWrapper alertsWrapper = restUserService.getAlertsByUserId(userId);
            HttpStatus httpStatus = alertsWrapper.getTotal() > 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(alertsWrapper, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying to get alerts by user id with userAlertsQuery:{}, but got exception {}", userAlertsQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<User> getUser(@ApiParam(name = "userId", value = "The UUID of the user to return", required = true) @PathVariable String userId,
                                        @ApiParam(value = "Expand response to get user alerts data", defaultValue = "false") @RequestParam(value = "expand", required = false, defaultValue = "false") Boolean expand) {
        try {
            User user = restUserService.getUserById(userId, expand);
            HttpStatus httpStatus = user != null ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(user, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying to get user with id:{}, but got exception {}", userId, ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<UsersWrapper> getUsers(presidio.webapp.model.UserQuery userQuery) {
        try {
            UsersWrapper usersWrapper = restUserService.getUsers(userQuery);
            HttpStatus httpStatus = usersWrapper.getTotal() >= 0 ? HttpStatus.OK : HttpStatus.BAD_REQUEST;
            return new ResponseEntity(usersWrapper, httpStatus);
        } catch (Exception ex) {
            logger.error("Trying to get users with userQuery:{}, but got exception {}", userQuery.toString(), ex);
            return new ResponseEntity(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<User> updateUser(List<Patch> patch) {
        throw new UnsupportedOperationException();
    }
}
