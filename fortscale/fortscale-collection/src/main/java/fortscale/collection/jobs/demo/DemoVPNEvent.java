package fortscale.collection.jobs.demo;

import fortscale.domain.core.Computer;
import fortscale.domain.core.User;

/**
 * Created by Amir Keren on 2/17/16.
 */
public class DemoVPNEvent extends DemoEvent {

	private Computer srcMachine;
	private String clientAddress;
	private String country;
	private String status;

	public DemoVPNEvent(User user, int score, DemoUtils.EventFailReason reason, Computer srcMachine,
			String clientAddress, String country, String status) {
		super(user, score, reason);
		this.srcMachine = srcMachine;
		this.clientAddress = clientAddress;
		this.country = country;
		this.status = status;
	}

	public Computer getSrcMachine() {
		return srcMachine;
	}

	public String getClientAddress() {
		return clientAddress;
	}

	public String getCountry() {
		return country;
	}

	public String getStatus() {
		return status;
	}

	@Override protected String getAnomalyValue() {
		switch (getReason()) {
			case COUNTRY: return country;
			case SOURCE: return srcMachine.getName();
			default: return null;
		}
	}

}