package fortscale.domain.ad;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.domain.ad.dao.AdGroupRepository;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/fortscale-domain-context-test.xml" })
public class AdGroupRepositoryTest {

	@Autowired
	private AdGroupRepository repository;
	
	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}
	
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
