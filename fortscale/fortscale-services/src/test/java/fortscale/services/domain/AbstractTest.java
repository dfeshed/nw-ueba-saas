package fortscale.services.domain;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/fortscale-services-context-test.xml" })
public abstract class AbstractTest {
	//fortscale-services-context-with-domain-context-test.xml
    //fortscale-services-context-test.xml
	
	@BeforeClass
	public static void setUp() {
		
	}
	
	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}	
}
