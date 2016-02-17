package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoKerberosEvent extends DemoGenericEvent {

	private Computer srcMachine;
	private String[] dstMachines;
	private String clientAddress;
	private String failureCode;

	public DemoKerberosEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
							 String dstMachines[], String clientAddress, String failureCode) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.clientAddress = clientAddress;
		this.failureCode = failureCode;
	}

	public DemoKerberosEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
							 String dstMachine, String clientAddress, String failureCode) {
		this(user, score, reason, srcMachine, new String[] { dstMachine }, clientAddress, failureCode);
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String[] getDstMachines() {
		return dstMachines;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getFailureCode() {
		return failureCode;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case DEST: return dstMachines[0];
			case SOURCE: return srcMachine.getName();
			case FAILURE: return failureCode;
			default: return null;
		}
	}

}