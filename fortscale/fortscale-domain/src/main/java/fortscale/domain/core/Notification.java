package fortscale.domain.core;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Document(collection = Notification.COLLECTION_NAME)
@CompoundIndexes({
	@CompoundIndex(name="fsId_ts_desc", def = "{'fsId': 1, 'ts': -1}"),
	@CompoundIndex(name="ts_desc", def = "{'ts': -1}"),
	@CompoundIndex(name="dismissed_ts_desc", def = "{'dismissed': 1, 'ts': -1}", sparse=true)
})
public class Notification extends AbstractDocument implements Serializable {
	
	private static final long serialVersionUID = 4314533976059073710L;

	public static final String COLLECTION_NAME = "notifications";

	private long ts;
	@Indexed(unique=true)
	@JsonIgnore
	private String index;
	private String generator_name;
	private String name;
	private String cause;
	private String displayName;
	@JsonIgnore
	private String uuid;
	private String fsId;
	private String type;
	private Long eventsStart;
	private Long eventsEnd;
	private boolean dismissed;
	private int commentsCount;
	private Map<String, String> attributes;
	private List<NotificationComment> comments = new LinkedList<NotificationComment>();
	
	public Notification() {}
	
	public Notification(String id, long ts, String index, String generator_name, String name, String cause, String displayName, String uuid, String fsId, String type, boolean dismissed, int commentsCount) {
		this.ts = ts;
		this.index = index;
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
	
	
	
	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
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

	public void setTs(long ts) {
		this.ts = ts;
	}

	public void setGenerator_name(String generator_name) {
		this.generator_name = generator_name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public void setFsId(String fsId) {
		this.fsId = fsId;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Long getEventsStart() {
		return eventsStart;
	}

	public void setEventsStart(Long eventsStart) {
		this.eventsStart = eventsStart;
	}

	public Long getEventsEnd() {
		return eventsEnd;
	}

	public void setEventsEnd(Long eventsEnd) {
		this.eventsEnd = eventsEnd;
	}
}