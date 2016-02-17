package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoKerberosEvent extends DemoEvent {

	private Computer srcMachine;
	private String[] dstMachines;
	private String domain;
	private String dc;
	private String clientAddress;
	private String failureCode;

	public DemoKerberosEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
			String dstMachines[], String domain, String dc, String clientAddress, String failureCode) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.domain = domain;
		this.dc = dc;
		this.clientAddress = clientAddress;
		this.failureCode = failureCode;
	}

	public DemoKerberosEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
			String dstMachine, String domain, String dc, String clientAddress, String failureCode) {
		this(user, score, reason, srcMachine, new String[] { dstMachine }, domain, dc, clientAddress, failureCode);
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String[] getDstMachines() {
		return dstMachines;
	}

	public String getDomain() {
		return domain;
	}

	public String getDc() {
		return dc;
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