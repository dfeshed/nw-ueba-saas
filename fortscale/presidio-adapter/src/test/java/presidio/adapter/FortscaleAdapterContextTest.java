package presidio.adapter;

import fortscale.common.shell.PresidioExecutionService;
import fortscale.utils.shell.BootShim;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.adapter.spring.AdapterTestConfig;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdapterTestConfig.class)
public class FortscaleAdapterContextTest {

    public static final String EXECUTION_COMMAND = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

    @Autowired
    private BootShim bootShim;

    @Autowired
    private PresidioExecutionService presidioExecutionService;


    @Test
    public void contextLoads() throws Exception {
        Assert.assertNotNull(this.presidioExecutionService);
    }

    @Test
    public void adapterShellTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }

}
