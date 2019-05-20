package presidio.webapp.model;

import fortscale.utils.rest.jsonpatch.JsonPatch;

public class EntityPatchBody {

    private EntityQuery entityQuery;
    private JsonPatch jsonPatch;

    public EntityPatchBody() {
    }

    public EntityQuery getEntityQuery() {
        return entityQuery;
    }

    public void setEntityQuery(EntityQuery entityQuery) {
        this.entityQuery = entityQuery;
    }

    public JsonPatch getJsonPatch() {
        return jsonPatch;
    }

    public void setJsonPatch(JsonPatch jsonPatch) {
        this.jsonPatch = jsonPatch;
    }
}