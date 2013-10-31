package fortscale.domain.core;

import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.AbstractTest;
import fortscale.domain.core.dao.UserRepository;

public class UserRepositoryTest  extends AbstractTest{

	@Autowired
	UserRepository repository;

	
	@Test
	public void testCount(){
		repository.count();
	}
	
	@Test
	public void testFindAll(){
		repository.findAll();
	}
	
	@Test
	public void testFindOne(){
		User user = repository.findAll().iterator().next();
		User user2 = repository.findOne(user.getId());
		Assert.assertEquals(user.getId(), user2.getId());
	}
	
	@Test
	public void testFindByAdDn() {
		User user = repository.findAll().iterator().next();
		User user2 = repository.findByAdDn(user.getAdDn());
		Assert.assertEquals(user.getId(), user2.getId());
	}
	
	@Test
	public void testFindByUsername() {
		User user = repository.findAll().iterator().next();
		User user2 = repository.findByUsername(user.getUsername());
		Assert.assertEquals(user.getId(), user2.getId());
	}
	
	@Test
	public void testFindByEmailAddress() {
		Iterator<User> iterator = repository.findAll().iterator();
		User user = null;
		while(iterator.hasNext() ) {
			user = iterator.next();
			if(user.getEmailAddress() != null) {
				break;
			}
		}
		if(user.getEmailAddress() != null) {
			User user2 = repository.findByEmailAddress(user.getEmailAddress());
			Assert.assertEquals(user.getId(), user2.getId());
		}
	}
	
	@Test
	public void testFindByLastnameContaining() {
		User user = repository.findAll().iterator().next();
		User user2 = repository.findByLastnameContaining(user.getLastname()).get(0);
		Assert.assertEquals(user.getLastname(), user2.getLastname());
	}
	
	@Test
	public void testFindBySearchFieldContaining() {
		Iterator<User> iterator = repository.findAll().iterator();
		User user = iterator.next();
		//TODO: remove the following while statement after omri fixes 2 problems: 1. users with no username 2. users like systemmailbox{e0dc1c29-89c3-4034-b678-e6c29d823ed9}@fortscale.dom
		while(user.getUsername() == null || user.getUsername().contains("{")) {
			user = iterator.next();
		}
		User user2 = repository.findBySearchFieldContaining(user.getUsername()).get(0);
		Assert.assertEquals(user.getId(), user2.getId());
		List<User> users = repository.findBySearchFieldContaining(user.getFirstname().toLowerCase());
		Assert.assertTrue(users.size() > 0);
	}	
}
