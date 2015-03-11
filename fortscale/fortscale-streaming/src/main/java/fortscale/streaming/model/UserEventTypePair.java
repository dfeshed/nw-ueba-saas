package fortscale.streaming.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;

/**
 * Pair of user and event type used as store key
 */
public class UserEventTypePair {

	private String username;
	private String eventType;

	@JsonCreator
	public UserEventTypePair(@JsonProperty("username") String username, @JsonProperty("eventType") String eventType) {
		this.username = username;
		this.eventType = eventType;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		final UserEventTypePair other = (UserEventTypePair) obj;
		return Objects.equal(this.username, other.username) &&
				Objects.equal(this.eventType, other.eventType);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(username, eventType);
	}
}
