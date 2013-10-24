package fortscale.web.beans;

import java.util.List;

public class UserMachinesBean {
	
	private final String userId;
	private final List<UserMachineBean> machines;
	
	public UserMachinesBean(String userId, List<UserMachineBean> userMachineBeans) {
		this.userId = userId;
		this.machines = userMachineBeans;
	}

	public String getUserId() {
		return userId;
	}

	public List<UserMachineBean> getMachines() {
		return machines;
	}
}
