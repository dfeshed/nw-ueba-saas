package fortscale.domain.core.alert.analystfeedback;

import org.springframework.data.mongodb.core.mapping.Field;

import java.time.Instant;
import java.util.UUID;

/**
 * Created by alexp on 02/02/17.
 */
public abstract class AnalystFeedback {
    public static final String ANALYST_FEEDBACK_ID_FIELD = "analystFeedbackId";
    public static final String ANALYST_USER_NAME_FIELD = "analystUserName";
    private static final String MODIFIED_AT_FIELD = "modifiedAt";

    @Field(ANALYST_FEEDBACK_ID_FIELD)
    private String analystFeedbackId;
    @Field(ANALYST_USER_NAME_FIELD)
    private String analystUserName;

    @Field(MODIFIED_AT_FIELD)
    private Instant modifiedAt;

    public AnalystFeedback() {
    }

    public AnalystFeedback(String analystUserName, Instant modifiedAt) {
        this.analystFeedbackId = UUID.randomUUID().toString();
        this.analystUserName = analystUserName;
        this.modifiedAt = modifiedAt;
    }

    public AnalystFeedback(String commentId, String analystUserName, Instant modifiedAt) {
        this.analystFeedbackId = commentId;
        this.analystUserName = analystUserName;
        this.modifiedAt = modifiedAt;
    }

    public String getAnalystUserName() {
        return analystUserName;
    }

    public void setAnalystUserName(String analystUserName) {
        this.analystUserName = analystUserName;
    }

    public String getAnalystFeedbackId() {
        return analystFeedbackId;
    }

    public Instant getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(Instant modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        AnalystCommentFeedback comment = (AnalystCommentFeedback) o;

        return analystFeedbackId != null ? analystFeedbackId.equals(comment.getAnalystFeedbackId()) : comment.getAnalystFeedbackId() == null;

    }

    @Override
    public int hashCode() {
        return analystFeedbackId != null ? analystFeedbackId.hashCode() : 0;
    }
}