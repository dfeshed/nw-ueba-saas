package fortscale.domain.core.alert.analystfeedback;

import com.fasterxml.jackson.annotation.JsonTypeName;
import org.joda.time.DateTime;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * Created by alexp on 04/07/2016.
 */
@JsonTypeName(AnalystCommentFeedback.ANALYST_COMMENT_FEEDBACK_TYPE)
public class AnalystCommentFeedback extends AnalystFeedback {
	public static final String ANALYST_COMMENT_FEEDBACK_TYPE = "AnalystCommentFeedback";

	public static final String commentTextField = "commentText";

	@Field(commentTextField)
	private String commentText;

	public AnalystCommentFeedback() {
	}

	public AnalystCommentFeedback(String analyst, String commentText, Long modifiedAt, String alertId) {
		super(analyst, modifiedAt,alertId);
		this.commentText = commentText;
	}

	public AnalystCommentFeedback(String analyst, String commentText, String commentId, Long modifiedAt,String alertId){
		super(commentId, analyst, modifiedAt,alertId);
		this.commentText = commentText;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

}