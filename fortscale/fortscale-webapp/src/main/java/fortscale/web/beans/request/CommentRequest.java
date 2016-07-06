package fortscale.web.beans.request;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * Created by alexp on 04/07/2016.
 */
public class CommentRequest {

	@NotNull
	@NotEmpty
	private String analystUserName;

	@NotNull
	@NotEmpty
	private String commentText;

	public CommentRequest() {
	}

	public CommentRequest(String analystUserName, String commentText) {
		this.analystUserName = analystUserName;
		this.commentText = commentText;
	}

	public String getAnalystUserName() {
		return analystUserName;
	}

	public void setAnalystUserName(String analystUserName) {
		this.analystUserName = analystUserName;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
}
