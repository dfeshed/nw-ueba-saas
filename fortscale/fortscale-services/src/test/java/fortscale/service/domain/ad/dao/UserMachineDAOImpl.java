package fortscale.service.domain.ad.dao;

import java.util.List;

import org.springframework.stereotype.Component;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.UserMachineDAO;

@Component("UserMachineDAO")
public class UserMachineDAOImpl implements UserMachineDAO{

	@Override
	public List<UserMachine> findByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserMachine> findByHostname(String hostname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<UserMachine> findByHostnameip(String hostnameip) {
		// TODO Auto-generated method stub
		return null;
	}

}
