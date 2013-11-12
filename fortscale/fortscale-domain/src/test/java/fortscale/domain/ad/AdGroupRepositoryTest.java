package fortscale.domain.ad;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.AbstractTest;
import fortscale.domain.ad.dao.AdGroupRepository;


@Ignore
public class AdGroupRepositoryTest extends AbstractTest{

	@Autowired
	private AdGroupRepository repository;
	
	
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
		AdGroup group = repository.findAll().iterator().next();
		repository.findOne(group.getId());
	}
	
	@Test
	public void testFindByDistinguishedName(){
		AdGroup group = repository.findAll().iterator().next();
		repository.findByDistinguishedName(group.getDistinguishedName());
	}
}
