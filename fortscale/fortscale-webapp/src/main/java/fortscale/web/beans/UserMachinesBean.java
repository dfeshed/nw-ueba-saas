package fortscale.web.beans;

import java.util.List;

import fortscale.domain.ad.UserMachine;

public class UserMachinesBean {
	
	private final String userId;
	private final List<UserMachine> machines;
	
	public UserMachinesBean(String userId, List<UserMachine> userMachineBeans) {
		this.userId = userId;
		this.machines = userMachineBeans;
	}

	public String getUserId() {
		return userId;
	}

	public List<UserMachine> getMachines() {
		return machines;
	}
}
