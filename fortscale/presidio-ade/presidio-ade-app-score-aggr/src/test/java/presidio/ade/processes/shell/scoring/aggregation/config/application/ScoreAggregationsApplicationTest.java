package presidio.ade.processes.shell.scoring.aggregation.config.application;

import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.FixMethodOrder;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.testng.annotations.BeforeTest;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;


/**
 * Created by barak_schuster on 7/25/17.
 */
@Category(ModuleTestCategory.class)
@ContextConfiguration
public class ScoreAggregationsApplicationTest extends EnrichedFileSourceBaseAppTest {
    public static final String EXECUTION_COMMAND2 = "run  --schema DLPFILE --start_date 2017-06-13T07:00:00.00Z --end_date 2017-06-13T09:00:00.00Z --fixed_duration_strategy 3600";
    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;

    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);

    public static final String EXECUTION_COMMAND = String.format("run  --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s ", ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 3600);
    @BeforeTest
    public void beforeTest()
    {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Override
    protected String getSanityTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Override
    protected void assertSanityTest() {
        Set<String> collectionNames = mongoTemplate.getCollectionNames();
        System.out.println("aa");
    }

    @Configuration
    @Import({EnrichedSourceSpringConfig.class,ScoreAggregationsApplicationConfigTest.class, PresidioCommands.class})
    protected static class springConfigScoreAggregationsApplication {

    }
}