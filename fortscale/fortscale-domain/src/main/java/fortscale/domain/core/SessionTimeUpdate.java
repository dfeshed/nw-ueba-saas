package fortscale.domain.core;

/**
 * Created by danal on 02/08/2015.
 */
public class SessionTimeUpdate {

	String sessionId;

	SessionUpdateType sessionUpdateType;

	Long startTimestamp;

	Long endTimestamp;

	EntityType entityType;

	String entityName;

	public SessionTimeUpdate(String sessionId, SessionUpdateType sessionUpdateType, Long startTimestamp,
			Long endTimestamp, EntityType entityType, String entityName) {
		this.sessionId = sessionId;
		this.sessionUpdateType = sessionUpdateType;
		this.startTimestamp = startTimestamp;
		this.endTimestamp = endTimestamp;
		this.entityType = entityType;
		this.entityName = entityName;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public SessionUpdateType getSessionUpdateType() {
		return sessionUpdateType;
	}

	public void setSessionUpdateType(SessionUpdateType sessionUpdateType) {
		this.sessionUpdateType = sessionUpdateType;
	}

	public Long getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Long startTimestamp) {
		this.startTimestamp = startTimestamp;
	}

	public Long getEndTimestamp() {
		return endTimestamp;
	}

	public void setEndTimestamp(Long endTimestamp) {
		this.endTimestamp = endTimestamp;
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	public String getEntityName() {
		return entityName;
	}

	public void setEntityName(String entityName) {
		this.entityName = entityName;
	}
}
