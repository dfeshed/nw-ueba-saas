package fortscale.domain.core.alert.analystfeedback;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

/**
 * Created by alexp on 02/02/17.
 */
public abstract class AnalystFeedback {
    public static final String commentIdField = "commentId";
    public static final String analystUserNameField = "analystUserName";
    public static final String updateDateField = "updateDate";

    @Field(commentIdField)
    private String commentId;
    @Field(analystUserNameField)
    private String analystUserName;
    @Field(updateDateField)
    private long updateDate;

    public AnalystFeedback() {
    }

    public AnalystFeedback(String analystUserName, long updateDate) {
        this.commentId = UUID.randomUUID().toString();
        this.analystUserName = analystUserName;
        this.updateDate = updateDate;
    }

    public AnalystFeedback(String commentId, String analystUserName, long updateDate) {
        this.commentId = commentId;
        this.analystUserName = analystUserName;
        this.updateDate = updateDate;
    }

    public String getAnalystUserName() {
        return analystUserName;
    }

    public void setAnalystUserName(String analystUserName) {
        this.analystUserName = analystUserName;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public String getCommentId() {
        return commentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Comment comment = (Comment) o;

        return commentId != null ? commentId.equals(comment.getCommentId()) : comment.getCommentId() == null;

    }

    @Override
    public int hashCode() {
        return commentId != null ? commentId.hashCode() : 0;
    }
}