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

	public DemoGenericEvent() {}

	protected DemoGenericEvent(User user, int score, DemoUtils.EventFailReason reason) {
		this.user = user;
		this.score = score;
		this.reason = reason;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public DemoUtils.EventFailReason getReason() {
		return reason;
	}

	public void setReason(DemoUtils.EventFailReason reason) {
		this.reason = reason;
	}

}