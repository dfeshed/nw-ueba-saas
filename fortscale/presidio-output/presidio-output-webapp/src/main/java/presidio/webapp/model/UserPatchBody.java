package presidio.webapp.model;

import fortscale.utils.rest.jsonpatch.JsonPatch;

public class UserPatchBody {

    private UserQuery userQuery;
    private JsonPatch jsonPatch;

    public UserPatchBody() {
    }

    public UserQuery getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(UserQuery userQuery) {
        this.userQuery = userQuery;
    }

    public JsonPatch getJsonPatch() {
        return jsonPatch;
    }

    public void setJsonPatch(JsonPatch jsonPatch) {
        this.jsonPatch = jsonPatch;
    }
}
