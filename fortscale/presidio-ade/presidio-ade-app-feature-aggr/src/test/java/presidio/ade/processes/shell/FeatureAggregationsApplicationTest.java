package presidio.ade.processes.shell;

import fortscale.utils.shell.BootShim;
import fortscale.utils.test.category.ModuleTestCategory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.core.CommandResult;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by barak_schuster on 7/30/17.
 */
@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
@ContextConfiguration(classes = FeatureAggregationsConfigTest.class)
public class FeatureAggregationsApplicationTest {

    public static final String EXECUTION_COMMAND = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";

    @Autowired
    private BootShim bootShim;

    @Test
    public void contextTest() {
        CommandResult commandResult = bootShim.getShell().executeCommand(EXECUTION_COMMAND);
        Assert.assertTrue(commandResult.isSuccess());
    }
}