package fortscale.domain.ad.dao;

import java.util.List;

import fortscale.domain.ad.UserMachine;



public interface UserMachineDAO {
	
	public List<UserMachine> findByUsername(String username);
	
	public List<UserMachine> findByHostname(String hostname);
	
	public List<UserMachine> findByHostnameip(String hostnameip);
}
