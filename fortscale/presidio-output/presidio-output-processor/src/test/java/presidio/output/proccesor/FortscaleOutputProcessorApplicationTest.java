package presidio.output.proccesor;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.processor.services.OutputProcessServiceImpl;
import presidio.output.processor.spring.OutputProcessorConfiguration;

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
