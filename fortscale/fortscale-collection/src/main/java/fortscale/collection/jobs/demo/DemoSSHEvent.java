package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoSSHEvent extends DemoGenericEvent {

	private static final String SUCCESS_CODE = "Accepted";
	private static final String DEFAULT_AUTH = "password";

	private Computer srcMachine;
	private String[] dstMachines;
	private String clientAddress;
	private String status;
	private String authMethod;

	public DemoSSHEvent() {}

	private DemoSSHEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
						 String[] dstMachines, String clientAddress, String status, String authMethod) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.clientAddress = clientAddress;
		this.status = status;
		this.authMethod = authMethod;
	}

	public static DemoSSHEvent createBaseLineConfiguration(User user, Computer srcMachine, String dstMachines[]) {
		return new DemoSSHEvent(user, DemoUtils.DEFAULT_SCORE, DemoUtils.EventFailReason.NONE, srcMachine,
				dstMachines, DemoUtils.generateRandomIPAddress(), SUCCESS_CODE, DEFAULT_AUTH);
	}

	public static DemoSSHEvent createAnomalyConfiguration(User user, Computer srcMachine, String dstMachines[],
														  int score, DemoUtils.EventFailReason reason,
														  String status, String authMethod) {
		return new DemoSSHEvent(user, score, reason, srcMachine, dstMachines, DemoUtils.generateRandomIPAddress(),
				status, authMethod);
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String[] getDstMachines() {
		return dstMachines;
	}

	public String getDstMachine() {
		return dstMachines[0];
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getStatus() {
		return status;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	public void setSrcMachine(Computer srcMachine) {
		this.srcMachine = srcMachine;
	}

	public void setDstMachines(String[] dstMachines) {
		this.dstMachines = dstMachines;
	}

	public void setClientAddress(String clientAddress) {
		this.clientAddress = clientAddress;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public void setAuthMethod(String authMethod) {
		this.authMethod = authMethod;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case AUTH: return authMethod;
			case SOURCE: return srcMachine.getName();
			case DEST: return dstMachines[0];
			default: return null;
		}
	}

	@Override
	protected DemoGenericEvent generateEvent() {
		return new DemoSSHEvent(user, score, reason, srcMachine, dstMachines, clientAddress, status, authMethod);
	}

}