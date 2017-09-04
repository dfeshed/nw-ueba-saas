package presidio.ade.smart;

import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.experimental.categories.Category;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.smart.config.SmartApplicationConfigurationTest;
import presidio.ade.test.utils.tests.BaseAppTest;

import java.time.Duration;
import java.time.Instant;

/**
 * Created by YaronDL on 9/4/2017.
 */

@Category(ModuleTestCategory.class)
@ContextConfiguration
public class SmartApplicationTest extends BaseAppTest {
    private static final int DAYS_BACK_FROM = 2;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = START_DATE.plus(Duration.ofHours(1));

    public static final String EXECUTION_COMMAND = String.format("process --smart_record_conf_name %s --start_date %s --end_date %s", "userId_hourly", START_DATE.toString(), END_DATE.toString());

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }



    @Configuration
    @Import({SmartApplicationConfigurationTest.class, BaseAppTest.springConfig.class,})
    protected static class springConfigModelingServiceApplication {

    }
}
