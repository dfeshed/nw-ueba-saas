package fortscale.domain.ad;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.AbstractTest;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.UserMachineDAO;

@Ignore
public class UserMachineDAOTest extends AbstractTest{
	
	@Autowired
	AdUserRepository repository;

	@Autowired
	private UserMachineDAO userMachineDAO;
		
	@Test
	public void testFindByUsername(){
		Assert.assertTrue(findMachines().size() > 0);
	}
	
	@Test
	public void testFindByHostname(){
		List<UserMachine> userMachines = userMachineDAO.findByHostname(findMachines().get(0).getHostname().toLowerCase());
		Assert.assertTrue(userMachines.size() > 0);
	}
	
	@Test
	public void testFindByHostnameip(){
		List<UserMachine> userMachines = userMachineDAO.findByHostnameip(findMachines().get(0).getHostnameip());
		Assert.assertTrue(userMachines.size() > 0);
	}
	
	private List<UserMachine> findMachines(){
		Iterator<AdUser> iter = repository.findAll().iterator();
		List<UserMachine> ret = Collections.emptyList();
		while(iter.hasNext()){
			AdUser user = iter.next();
			String userName = getUserName(user);
			if(StringUtils.isEmpty(userName)){
				continue;
			}
			ret = userMachineDAO.findByUsername(userName);
			if(ret.size() > 0){
				break;
			}
		}
		return ret;
	}
	
	private String getUserName(AdUser adUser){
		String userName = adUser.getUserPrincipalName();
		if(!StringUtils.isEmpty(userName)){
			userName = userName.split("@")[0];
		}
		return userName;
	}
}
