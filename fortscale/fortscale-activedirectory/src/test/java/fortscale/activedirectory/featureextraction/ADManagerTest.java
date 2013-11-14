package fortscale.activedirectory.featureextraction;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import fortscale.activedirectory.main.ADManager;
import fortscale.services.fe.FeService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/active-directory-application-context-test.xml" })
public class ADManagerTest {

	@Autowired
	private FeService feService;
	
	@Test
	 public void thisAlwaysPasses() {
	 }
	
	@Ignore
	public void runTest(){
		ADManager adManager = new ADManager();
		adManager.run(feService, null);
	}
}
