package presidio.input.core;


import fortscale.common.shell.PresidioExecutionService;
import fortscale.common.shell.config.ShellableApplicationConfig;
import fortscale.utils.shell.BootShim;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.aspectj.EnableSpringConfigured;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.input.core.services.impl.InputExecutionServiceImpl;
import presidio.input.core.spring.InputCoreConfiguration;


@RunWith(SpringRunner.class)
public class FortscaleInputCoreApplicationTest {
    public static final String EXECUTION_COMMAND = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

    @Autowired
    private BootShim bootShim;

    @Autowired
    private PresidioExecutionService processService;

    @Test
    public void contextLoads() throws Exception {
        Assert.assertTrue(processService instanceof InputExecutionServiceImpl);
    }

    @Test
    public void inputCoreShellTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }

    @Configuration
    @Import({InputCoreConfiguration.class, MongodbTestConfig.class, ShellableApplicationConfig.class})
    @EnableSpringConfigured
    public static class springConfig {

    }
}
