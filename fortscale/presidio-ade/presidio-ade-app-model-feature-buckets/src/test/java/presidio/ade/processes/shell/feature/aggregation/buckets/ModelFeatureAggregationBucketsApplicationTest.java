package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.test.utils.generators.EnrichedFileGeneratorConfig;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;

import java.time.Duration;
import java.time.Instant;


@Category(ModuleTestCategory.class)
@ContextConfiguration
public class ModelFeatureAggregationBucketsApplicationTest extends EnrichedFileSourceBaseAppTest {

    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;
    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);

    public static final String EXECUTION_COMMAND = String.format("run  --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s ", ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 3600);

    @Before
    public void beforeTest() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }

    @Override
    protected String getSanityTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Override
    protected void assertSanityTest() {

    }

    @Configuration
    @Import({EnrichedSourceSpringConfig.class, ModelFeatureAggregationBucketsApplicationConfigTest.class, PresidioCommands.class, EnrichedFileGeneratorConfig.class})
    protected static class springConfigScoreAggregationsApplication {

    }
}
