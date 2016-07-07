package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Field;

import java.util.UUID;

/**
 * Created by alexp on 04/07/2016.
 */
public class Comment {
	public static final String entityIdField = "entityId";
	public static final String analystUserNameField = "analystUserName";
	public static final String updateDateField = "updateDate";
	public static final String commentTextField = "commentText";

	@Field(entityIdField) private String entityId;
	@Field(analystUserNameField) private String analystUserName;
	@Field(updateDateField) private long updateDate;
	@Field(commentTextField) private String commentText;

	public Comment() {
	}

	public Comment(String analyst, long updateDate, String commentText) {
		this.analystUserName = analyst;
		this.updateDate = updateDate;
		this.commentText = commentText;
		this.setEntityId(UUID.randomUUID().toString());
	}

	public Comment(String analyst, long updateDate, String commentText, String commentId){
		this.analystUserName = analyst;
		this.updateDate = updateDate;
		this.commentText = commentText;
		this.entityId = commentId;
	}

	public String getAnalystUserName() {
		return analystUserName;
	}

	public void setAnalystUserName(String analystUserName) {
		this.analystUserName = analystUserName;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public long getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(long updateDate) {
		this.updateDate = updateDate;
	}

	public String getCommentText() {
		return commentText;
	}

	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		Comment comment = (Comment) o;

		return entityId != null ? entityId.equals(comment.entityId) : comment.entityId == null;

	}

	@Override public int hashCode() {
		return entityId != null ? entityId.hashCode() : 0;
	}
}
