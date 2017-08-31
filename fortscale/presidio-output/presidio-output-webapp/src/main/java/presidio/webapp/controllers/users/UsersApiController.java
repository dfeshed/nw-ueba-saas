package presidio.webapp.controllers.users;

import org.springframework.http.ResponseEntity;
import presidio.webapp.model.Patch;
import presidio.webapp.model.User;
import presidio.webapp.model.UsersWrapper;

import java.util.List;


public class UsersApiController implements UsersApi {

    private final RestUserService restUserService;

    @Override
    public ResponseEntity<UsersWrapper> usersGet(String name, Integer minScore, Integer maxScore, List<String> tags, Integer limit, Integer offset, String sort) {
        return null;
    }

    @Override
    public ResponseEntity<User> usersUserIdAlertsGet(String userId) {
        return null;
    }

    @Override
    public ResponseEntity<User> usersUserIdGet(String userId) {
        return null;
    }

    @Override
    public ResponseEntity<User> usersUserIdPatch(List<Patch> patch) {
        return null;
    }
}
