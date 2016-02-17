package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoSSHEvent extends DemoGenericEvent {

	private Computer srcMachine;
	private String[] dstMachines;
	private String clientAddress;
	private String status;
	private String authMethod;

	public DemoSSHEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
						String[] dstMachines, String clientAddress, String status, String authMethod) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.dstMachines = dstMachines;
		this.clientAddress = clientAddress;
		this.status = status;
		this.authMethod = authMethod;
	}

	public DemoSSHEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
						String dstMachine, String clientAddress, String status, String authMethod) {
		this(user, score, reason, srcMachine, new String[] { dstMachine }, clientAddress, status, authMethod);
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

	public String getStatus() {
		return status;
	}

	public String getAuthMethod() {
		return authMethod;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case AUTH: return authMethod;
			case SOURCE: return srcMachine.getName();
			case DEST: return dstMachines[0];
			default: return null;
		}
	}

}