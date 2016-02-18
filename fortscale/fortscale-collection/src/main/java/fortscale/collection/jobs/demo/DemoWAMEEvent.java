package fortscale.collection.jobs.demo;

import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoWAMEEvent extends DemoGenericEvent {

	private String actionType;
	private String status;
	private String targetUsername;

	public DemoWAMEEvent() {}

	public DemoWAMEEvent(User user, int score, DemoUtils.EventFailReason reason, String actionType,
						 String status, String targetUsername) {
		super(user, score, reason);
		this.actionType = actionType;
		this.status = status;
		this.targetUsername = targetUsername;
	}

	public String getActionType() {
		return actionType;
	}

	public String getStatus() {
		return status;
	}

	public String getTargetUsername() {
		return targetUsername;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case ACTION_TYPE: return actionType;
			default: return null;
		}
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setTargetUsername(String targetUsername) {
		this.targetUsername = targetUsername;
	}

}