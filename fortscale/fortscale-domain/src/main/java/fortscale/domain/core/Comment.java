package fortscale.domain.core;

import fortscale.domain.analyst.Analyst;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

/**
 * Created by alexp on 04/07/2016.
 */
public class Comment {
	public static final String entityIdField = "entityId";
	public static final String analystField = "analyst";
	public static final String timeStampField = "timeStamp";
	public static final String commentTextField = "commentText";

	@Field(entityIdField) private String entityId;
	@Field(analystField) @DBRef private Analyst analyst;
	@Field(timeStampField) private long timeStamp;
	@Field(commentTextField) private String commentText;

	public Comment(Analyst analyst, long timeStamp, String commentText) {
		this.analyst = analyst;
		this.timeStamp = timeStamp;
		this.commentText = commentText;
		this.setEntityId(UUID.randomUUID().toString());
	}

	public Comment(Comment comment) {
		this.entityId = comment.getEntityId();
		this.analyst = comment.getAnalyst();
		this.timeStamp = comment.getTimeStamp();
		this.commentText = comment.getCommentText();
	}

	public Analyst getAnalyst() {
		return analyst;
	}

	public void setAnalyst(Analyst analyst) {
		this.analyst = analyst;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public long getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
}
