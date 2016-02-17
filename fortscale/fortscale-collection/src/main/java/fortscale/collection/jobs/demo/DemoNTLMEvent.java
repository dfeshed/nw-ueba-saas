package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoNTLMEvent extends DemoGenericEvent {

	private Computer srcMachine;
	private String failureCode;

	public DemoNTLMEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
						 String failureCode) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.failureCode = failureCode;
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String getFailureCode() {
		return failureCode;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case SOURCE: return srcMachine.getName();
			case FAILURE: return failureCode;
			default: return null;
		}
	}

}