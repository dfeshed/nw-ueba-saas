package presidio.input.core;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.api.InputProcessService;
import presidio.input.core.spring.InputProcessConfiguration;


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
