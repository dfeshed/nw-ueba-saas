package fortscale;

import fortscale.services.OutputProcessServiceImpl;
import fortscale.spring.OutputProcessorConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OutputProcessorConfiguration.class)

public class FortscaleOutputProcessorApplicationTest {

	@Autowired
	OutputProcessServiceImpl processService;

	@Test
	public void contextLoads() throws Exception {
		processService.run("SHAY");

	}

}
