package fortscale.collection.jobs.demo;

import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public abstract class DemoGenericEvent {

	private User user;
	private int score;
	private DemoUtils.EventFailReason reason;

	protected abstract String getAnomalyValue();

	protected DemoGenericEvent(User user, int score, DemoUtils.EventFailReason reason) {
		this.user = user;
		this.score = score;
		this.reason = reason;
	}

	public User getUser() {
		return user;
	}

	public int getScore() {
		return score;
	}

	public DemoUtils.EventFailReason getReason() {
		return reason;
	}

}