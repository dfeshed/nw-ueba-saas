package fortscale.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.ad.UserMachine;



public interface UserMachineDAO {
	
	public List<UserMachine> findByUsername(String username);
	
	public List<UserMachine> findByHostname(String hostname);
	
	public List<UserMachine> findByHostnameip(String hostnameip);

	public List<UserMachine> findAll(Pageable pageable);
	
	public List<UserMachine> findAll();
}
