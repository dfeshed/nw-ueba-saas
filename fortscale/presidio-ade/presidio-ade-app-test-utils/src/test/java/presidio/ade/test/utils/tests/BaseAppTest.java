package presidio.ade.test.utils.tests;

import fortscale.utils.shell.BootShim;
import fortscale.utils.shell.BootShimConfig;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
@ContextConfiguration
public abstract class BaseAppTest {
    @Autowired
    protected BootShim bootShim;

    /**
     * test spring context loading and "empty" shell-execution
     */
    @Test
    public void contextTest() {
        executeAndAssertCommandSuccess(getContextTestExecutionCommand());
    }

    protected void executeAndAssertCommandSuccess(String command)
    {
        CommandResult commandResult = bootShim.getShell().executeCommand(command);
        Assert.assertTrue(commandResult.isSuccess());
    }

    protected abstract String getContextTestExecutionCommand();

    @Configuration
    @Import({MongodbTestConfig.class,  BootShimConfig.class})
    protected static class springConfig
    {

    }


}
