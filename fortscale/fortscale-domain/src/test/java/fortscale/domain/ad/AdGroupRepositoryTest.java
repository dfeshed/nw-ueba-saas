package fortscale.domain.ad;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.AbstractTest;
import fortscale.domain.ad.dao.AdGroupRepository;



public class AdGroupRepositoryTest extends AbstractTest{

	@Autowired
	private AdGroupRepository repository;
	
	
	@Test
	public void testCount(){
		repository.count();
	}
	
	@Test
	@Ignore
	public void testFindAll(){
		repository.findAll();
	}
	
	@Test
	@Ignore
	public void testFindOne(){
		//TODO: remove the ignore after Omri remove the empty lines from the table
		AdGroup group = repository.findAll().iterator().next();
		repository.findOne(group.getId());
	}
	
	@Test
	@Ignore
	public void testFindByDistinguishedName(){
		//TODO: remove the ignore after Omri remove the empty lines from the table
		AdGroup group = repository.findAll().iterator().next();
		repository.findByDistinguishedName(group.getDistinguishedName());
	}
}
