package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoNTLMEvent extends DemoGenericEvent {

	private static final String SUCCESS_CODE = "0x0";

	private Computer srcMachine;
	private String failureCode;

	public DemoNTLMEvent() {}

	private DemoNTLMEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
						 String failureCode) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.failureCode = failureCode;
	}

	public static DemoNTLMEvent createBaseLineConfiguration(User user, Computer srcMachine) {
		return new DemoNTLMEvent(user, DemoUtils.DEFAULT_SCORE, DemoUtils.EventFailReason.NONE, srcMachine,
				SUCCESS_CODE);
	}

	public static DemoNTLMEvent createAnomalyConfiguration(User user, Computer srcMachine, int score,
														   DemoUtils.EventFailReason reason, String failureCode) {
		return new DemoNTLMEvent(user, score, reason, srcMachine, failureCode);
	}

	public void setSrcMachine(Computer srcMachine) {
		this.srcMachine = srcMachine;
	}

	public void setFailureCode(String failureCode) {
		this.failureCode = failureCode;
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String getFailureCode() {
		return failureCode;
	}

	@Override
	protected String getAnomalyValue() {
		switch (getReason()) {
			case SOURCE: return srcMachine.getName();
			case FAILURE: return failureCode;
			default: return null;
		}
	}

	@Override
	protected DemoGenericEvent generateEvent() {
		return new DemoNTLMEvent(user, score, reason, srcMachine, failureCode);
	}

}