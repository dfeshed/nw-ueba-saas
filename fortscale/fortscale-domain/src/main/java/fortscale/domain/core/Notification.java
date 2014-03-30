package fortscale.domain.core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = Notification.COLLECTION_NAME)
public class Notification extends AbstractDocument implements Serializable {
	
	private static final long serialVersionUID = 4314533976059073710L;

	public static final String COLLECTION_NAME = "notifications";

	private long ts;
	private String generator_name;
	private String name;
	private String cause;
	private String displayName;
	private String uuid;
	private String fsId;
	private String type;
	private boolean dismissed;
	private int commentsCount;
	private Map<String, String> attributes;
	private List<NotificationComment> comments = new LinkedList<NotificationComment>();
	
	public Notification() {}
	
	public Notification(String id, long ts, String generator_name, String name, String cause, String displayName, String uuid, String fsId, String type, boolean dismissed, int commentsCount) {
		this.ts = ts;
		this.generator_name = generator_name;
		this.name = name;
		this.cause = cause;
		this.displayName = displayName;
		this.uuid = uuid;
		this.fsId = fsId;
		this.type = type;
		this.dismissed = dismissed;
		this.setCommentsCount(commentsCount);
	}
	
	
	public Map<String, String> getAttributes(){
		return attributes;
	}
	
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	public long getTs() {
		return ts;
	}

	public String getGenerator_name() {
		return generator_name;
	}

	public String getName() {
		return name;
	}

	public String getCause() {
		return cause;
	}

	public String getUuid() {
		return uuid;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getFsId() {
		return fsId;
	}

	public String getType() {
		return type;
	}
	
	public boolean isDismissed() {
		return dismissed;
	}
	
	public void setDismissed(boolean dismissed) {
		this.dismissed = dismissed;
	}

	public int getCommentsCount() {
		return commentsCount;
	}

	public void setCommentsCount(int commentsCount) {
		this.commentsCount = commentsCount;
	}

	public List<NotificationComment> getComments() {
		return comments;
	}

	public void setComments(List<NotificationComment> comments) {
		this.comments = (comments!=null)? comments : new LinkedList<NotificationComment>();
		this.commentsCount = (comments!=null)? comments.size() : 0;
	}
	
	public void addComment(NotificationComment comment) {
		this.commentsCount++;
		this.comments.add(comment);
	}

}