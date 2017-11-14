package presidio.output.proccesor;

import fortscale.utils.shell.BootShim;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.proccesor.spring.OutputProcessorTestConfiguration;
import presidio.output.proccesor.spring.TestConfig;
import presidio.output.processor.services.OutputExecutionService;
import presidio.output.processor.services.OutputExecutionServiceImpl;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputProcessorTestConfiguration.class, TestConfig.class, MongodbTestConfig.class})
@ActiveProfiles("useEmbeddedElastic")
public class OutputProcessorApplicationShellTest {

    public static final String EXECUTION_COMMAND = "run  --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

    @Autowired
    private BootShim bootShim;

    @Autowired
    OutputExecutionService executionService;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(executionService instanceof OutputExecutionServiceImpl);
    }

    @Test
    public void outputShellTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }

}
