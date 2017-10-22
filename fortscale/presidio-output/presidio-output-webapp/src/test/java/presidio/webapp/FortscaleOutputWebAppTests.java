package presidio.webapp;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import presidio.webapp.service.RestAlertService;
import presidio.webapp.spring.OutputWebappConfigurationTest;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {OutputWebappConfigurationTest.class})
public class FortscaleOutputWebAppTests {

	@Autowired
	private RestAlertService restAlertService;

	@Test
	public void contextLoads() {
		Assert.notNull(restAlertService, "restAlertService cannot be null on spring context");
	}

	//TODO- add tests here for restAlertService (if nothing to test let's remove this class)
}
