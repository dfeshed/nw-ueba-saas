package fortscale;

import fortscale.services.InputProcessService;
import fortscale.spring.InputProcessConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = InputProcessConfiguration.class)

public class FortscaleInputCoreApplicationTest {

	@Autowired
	InputProcessService processService;

	@Test
	public void contextLoads() throws Exception {
		processService.run(1,"SHAY");

	}

}
