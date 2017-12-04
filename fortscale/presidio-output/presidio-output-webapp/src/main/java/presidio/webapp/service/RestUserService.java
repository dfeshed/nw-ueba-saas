package presidio.webapp.service;


import fortscale.utils.rest.jsonpatch.JsonPatch;
import presidio.webapp.model.AlertsWrapper;
import presidio.webapp.model.User;
import presidio.webapp.model.UserQuery;
import presidio.webapp.model.UsersWrapper;

public interface RestUserService {
    User getUserById(String userId, boolean expand);

    UsersWrapper getUsers(UserQuery userQuery);

    AlertsWrapper getAlertsByUserId(String userId);

    User updateUser(String userId, JsonPatch updateRequest);

    UsersWrapper updateUsers(UserQuery userQuery, JsonPatch jsonPatch);
}
