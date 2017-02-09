package fortscale.domain.core.alert.analystfeedback;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by alexp on 04/07/2016.
 */
public class AnalystCommentFeedback extends AnalystFeedback {
	public static final String ANALYST_COMMENT_FEEDBACK_TYPE = "AnalystCommentFeedback";

	public static final String commentTextField = "commentText";

	@Field(commentTextField)
	private String commentText;

	public AnalystCommentFeedback() {
	}

	public AnalystCommentFeedback(String analyst, String commentText, Long modifiedAt) {
		super(analyst, modifiedAt);
		this.commentText = commentText;
	}

	public AnalystCommentFeedback(String analyst, String commentText, String commentId, Long modifiedAt){
		super(commentId, analyst, modifiedAt);
		this.commentText = commentText;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
}
