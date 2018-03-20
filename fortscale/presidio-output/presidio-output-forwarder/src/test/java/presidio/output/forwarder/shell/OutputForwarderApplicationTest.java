package presidio.output.forwarder.shell;

import fortscale.utils.shell.BootShim;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.output.forwarder.spring.OutputForwarderTestConfig;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OutputForwarderTestConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OutputForwarderApplicationTest {

    public static final String EXECUTION_COMMAND = "run --start_date 2018-03-16T00:00:00Z --end_date 2018-03-16T01:00:00Z";

    @Autowired
    private BootShim bootShim;

    @Autowired
    OutputForwarderExecutionService outputForwarderExecutionService;

    @Test
    public void contextLoads() throws Exception {
        try {
            Assert.assertNotNull(outputForwarderExecutionService);
        } catch (Exception e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    public void outputForwarderShellTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }

}
