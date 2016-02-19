package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

import java.util.Random;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoKerberosEvent extends DemoGenericEvent {

	private static final String SUCCESS_CODE = "0x0";

	private Computer srcMachine;
	private String[] dstMachines;
	private String clientAddress;
	private String failureCode;

	public DemoKerberosEvent() {}

	private DemoKerberosEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
							 String dstMachines[], String clientAddress, String failureCode) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.clientAddress = clientAddress;
		this.failureCode = failureCode;
	}

	public static DemoKerberosEvent createBaseLineConfiguration(User user, Computer srcMachine, String dstMachines[]) {
		return new DemoKerberosEvent(user, DemoUtils.DEFAULT_SCORE, DemoUtils.EventFailReason.NONE, srcMachine,
				dstMachines, DemoUtils.generateRandomIPAddress(), SUCCESS_CODE);
	}

	public static DemoKerberosEvent createAnomalyConfiguration(User user, Computer srcMachine, String dstMachines[],
															   int score, DemoUtils.EventFailReason reason,
															   String failureCode) {
		return new DemoKerberosEvent(user, score, reason, srcMachine, dstMachines, DemoUtils.generateRandomIPAddress(),
				failureCode);
	}

	@Override
	protected String getAnomalyValue() {
		switch (getReason()) {
			case DEST: return dstMachines[0];
			case SOURCE: return srcMachine.getName();
			case FAILURE: return failureCode;
			default: return null;
		}
	}

	@Override
	public DemoKerberosEvent generateEvent() {
		Random random = new Random();
		String dstMachine = dstMachines[random.nextInt(dstMachines.length)];
		return new DemoKerberosEvent(user, score, reason, srcMachine, new String[] { dstMachine }, clientAddress,
				failureCode);
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public void setSrcMachine(Computer srcMachine) {
		this.srcMachine = srcMachine;
	}

	public String[] getDstMachines() {
		return dstMachines;
	}

	public String getDstMachine() {
		return dstMachines[0];
	}

	public void setDstMachines(String[] dstMachines) {
		this.dstMachines = dstMachines;
	}

	public String getFailureCode() {
		return failureCode;
	}

	public void setFailureCode(String failureCode) {
		this.failureCode = failureCode;
	}

}