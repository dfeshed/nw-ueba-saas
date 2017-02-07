package fortscale.web.beans.request;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by alexp on 06/02/2017.
 */
public class CommentFeedbackRequest {
    @NotNull
    @NotEmpty
    private String commentText;

    @NotNull
    @NotEmpty
    private String analystUserName;

    public CommentFeedbackRequest() {
    }

    public CommentFeedbackRequest(String analystUserName, String commentText) {
        this.analystUserName = analystUserName;
        this.commentText = commentText;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(String commentText) {
        this.commentText = commentText;
    }

    public String getAnalystUserName() {
        return analystUserName;
    }

    public void setAnalystUserName(String analystUserName) {
        this.analystUserName = analystUserName;
    }
}
