package fortscale.domain.core.alert.analystfeedback;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

/**
 * Created by alexp on 04/07/2016.
 */
public class Comment extends AnalystFeedback {

	public static final String commentTextField = "commentText";

	@Field(commentTextField) private String commentText;

	public Comment() {
	}

	public Comment(String analyst, long updateDate, String commentText) {
		super(analyst, updateDate);
		this.commentText = commentText;
	}

	public Comment(String analyst, long updateDate, String commentText, String commentId){
		super(commentId, analyst, updateDate);
		this.commentText = commentText;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
}
