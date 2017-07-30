package presidio.output.proccesor;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.spring.OutputProcessorConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OutputProcessorTestConfiguration.class)
public class FortscaleOutputProcessorApplicationTest {

	@Autowired
	PresidioExecutionService executionService;

	@Test
	public void contextLoads() throws Exception {
		Assert.assertTrue(executionService instanceof OutputExecutionServiceImpl);
	}

}
