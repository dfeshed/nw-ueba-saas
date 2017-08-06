package presidio.ade.modeling;

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
import presidio.ade.modeling.config.ModelingServiceConfigurationTest;

/**
 * @author Lior Govrin
 */
@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
@ContextConfiguration(classes = ModelingServiceConfigurationTest.class)
public class ModelingServiceApplicationTest {
	private static final String ENRICHED_RECORDS_LINE = "process --group_name enriched-record-models --session_id test-run --end_date 2017-01-01T00:00:00Z";
	private static final String FEATURE_AGGREGATION_RECORDS_LINE = "process --group_name feature-aggregation-record-models --session_id test-run --end_date 2017-01-01T00:00:00Z";
	private static final String SMART_RECORDS_LINE = "process --group_name smart-record-models --session_id test-run --end_date 2017-01-01T00:00:00Z";

	@Autowired
	private BootShim bootShim;

	@Test
	public void modeling_service_application_test() {
		CommandResult commandResult = bootShim.getShell().executeCommand(ENRICHED_RECORDS_LINE);
		Assert.assertTrue(commandResult.isSuccess());
		commandResult = bootShim.getShell().executeCommand(FEATURE_AGGREGATION_RECORDS_LINE);
		Assert.assertTrue(commandResult.isSuccess());
//		commandResult = bootShim.getShell().executeCommand(SMART_RECORDS_LINE);
//		Assert.assertTrue(commandResult.isSuccess());
	}
}
