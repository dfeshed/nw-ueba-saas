package fortscale.domain;


import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:META-INF/spring/fortscale-domain-context-test.xml" })
public abstract class AbstractTest {
	
//	@Autowired
//	MongoDbFactory mongoDbFactory;
	
	@Autowired
	TestDbInitializer testDbInitializer;

	@Before
	public void setUp() throws IOException, InterruptedException {
		testDbInitializer.init();
	}
	
	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}	
}
