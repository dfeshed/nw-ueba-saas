package fortscale.services.domain.ad.dao;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.UserMachineDAO;

@Component("UserMachineDAO")
public class UserMachineDAOImpl implements UserMachineDAO{

	@Override
	public List<UserMachine> findByUsername(String username) {
		
		return null;
	}

	@Override
	public List<UserMachine> findByHostname(String hostname) {
		
		return null;
	}

	@Override
	public List<UserMachine> findByHostnameip(String hostnameip) {
		
		return null;
	}

	@Override
	public List<UserMachine> findAll() {
		
		return null;
	}

	@Override
	public List<UserMachine> findAll(Pageable pageable) {
		
		return null;
	}

}
