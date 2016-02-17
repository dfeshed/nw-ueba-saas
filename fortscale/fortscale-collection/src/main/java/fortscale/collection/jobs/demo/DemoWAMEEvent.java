package fortscale.collection.jobs.demo;

import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoWAMEEvent extends DemoEvent {

	private String actionType;
	private String domain;
	private String status;
	private String targetUsername;

	public DemoWAMEEvent(User user, int score, DemoUtils.EventFailReason reason, String actionType, String domain,
			String status, String targetUsername) {
		super(user, score, reason);
		this.actionType = actionType;
		this.domain = domain;
		this.status = status;
		this.targetUsername = targetUsername;
	}

	public String getActionType() {
		return actionType;
	}

	public String getDomain() {
		return domain;
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

}