package presidio.output.proccesor;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.shell.BootShim;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;
import presidio.output.processor.spring.OutputProcessorConfiguration;
@Ignore
@RunWith(SpringRunner.class)
@SpringBootTest(classes = OutputProcessorTestConfiguration.class)
public class FortscaleOutputProcessorApplicationTest {

	public static final String EXECUTION_COMMAND = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

	@Autowired
	private BootShim bootShim;

	@Autowired
	OutputExecutionService executionService;

	@Test
	public void contextLoads() throws Exception {
		Assert.assertTrue(executionService instanceof OutputExecutionServiceImpl);
	}

	@Test
	@Ignore
	//TODO add this test when we have solution to Junits with elasticsearch
	public void outputShellTest() {
		CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
		Assert.assertTrue(commandResult.isSuccess());
	}

}
