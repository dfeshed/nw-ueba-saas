package fortscale.domain.ad.dao;

import java.util.List;

import fortscale.domain.ad.UserMachine;



public interface UserMachineDAO {

	/**
	 * Gets logins by the given user name
	 */
	List<UserMachine> findByUsername(String username);
	
	/**
	 * Get logins to the given machine
	 */
	List<UserMachine> findByHostname(String hostname, int daysToConsider);
}
