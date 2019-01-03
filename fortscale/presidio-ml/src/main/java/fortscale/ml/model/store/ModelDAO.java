package fortscale.ml.model.store;

import fortscale.ml.model.Model;
import fortscale.utils.mongodb.index.DynamicIndexing;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document
@DynamicIndexing(compoundIndexes = {
        @CompoundIndex(name = "end", def = "{'endTime': 1}"),
        @CompoundIndex(name = "sessionEnd", def = "{'sessionId': 1, 'endTime': 1}"),
        @CompoundIndex(name = "ctxEnd", def = "{'contextId': 1, 'endTime': 1}"),
        @CompoundIndex(name = "sessionCtxEnd", def = "{'sessionId': 1, 'contextId': 1, 'endTime': 1}", unique = true)
})
public class ModelDAO {
    public static final String SESSION_ID_FIELD = "sessionId";
    public static final String CONTEXT_ID_FIELD = "contextId";
    public static final String CREATION_TIME_FIELD = "creationTime";
    public static final String MODEL_FIELD = "model";
    public static final String START_TIME_FIELD = "startTime";
    public static final String END_TIME_FIELD = "endTime";
    public static final String CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD = "contextFieldNameToValueMap";

    @Id
    private String id;

    @Field(SESSION_ID_FIELD)
    private String sessionId;
    @Field(CONTEXT_ID_FIELD)
    private String contextId;
    @CreatedDate @Field(CREATION_TIME_FIELD)
    private Instant creationTime;
    @Field(MODEL_FIELD)
    private Model model;
    @Field(START_TIME_FIELD)
    private Instant startTime;
    @Field(END_TIME_FIELD)
    private Instant endTime;
    @Field(CONTEXT_FIELD_NAME_TO_VALUE_MAP_FIELD)
    private Map<String, String> contextFieldNameToValueMap;

    public ModelDAO(String sessionId, String contextId, Model model, Instant startTime, Instant endTime,
                    Map<String, String> contextFieldNameToValueMap) {
        this.sessionId = sessionId;
        this.contextId = contextId;
        this.model = model;
        this.startTime = startTime;
        this.endTime = endTime;
        this.contextFieldNameToValueMap = contextFieldNameToValueMap;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getContextId() {
        return contextId;
    }

    public Instant getCreationTime() {
        return creationTime;
    }

    public Model getModel() {
        return model;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public Map<String, String> getContextFieldNameToValueMap() {
        return contextFieldNameToValueMap;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
