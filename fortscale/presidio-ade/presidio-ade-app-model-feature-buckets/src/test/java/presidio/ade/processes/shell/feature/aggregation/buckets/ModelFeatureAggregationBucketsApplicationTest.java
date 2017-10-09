package presidio.ade.processes.shell.feature.aggregation.buckets;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.bucket.FeatureBucketStoreMongoImpl;
import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.generators.EnrichedFileGenerator;
import presidio.ade.test.utils.generators.EnrichedFileGeneratorConfig;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.user.UserRegexCyclicValuesGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;


@Category(ModuleTestCategory.class)
@ContextConfiguration
public class ModelFeatureAggregationBucketsApplicationTest extends EnrichedFileSourceBaseAppTest {

    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;
    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);

    public static final String COMMAND_FORMAT = "run  --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s ";
    public static final String EXECUTION_COMMAND = String.format(COMMAND_FORMAT, ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 3600);

    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    @Qualifier("modelBucketConfigService")
    private BucketConfigurationService bucketConfigurationService;

    @Before
    public void beforeTest() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }

    @Override
    protected String getSanityTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    /**
     * runs execution without data
     * @return
     */
    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    /**
     *
     * @param generatedData - single user data from 2 days
     */
    @Override
    protected void assertSanityTest(List generatedData) {
        int expectedFeatureBucketsAmount = DAYS_BACK_FROM - DAYS_BACK_TO;

        assertFeatureBuckets(expectedFeatureBucketsAmount);
    }

    private void assertFeatureBuckets(int expectedFeatureBucketsAmount) {
        List<FeatureBucketConf> fileBucketConfs = bucketConfigurationService.getFeatureBucketConfs("file");
        int amountOfFileBucketConfs = fileBucketConfs.size();
        Assert.assertTrue("test relies on having at least 1 file bucketConf", amountOfFileBucketConfs >0);
        Set<String> allMongoCollections = mongoTemplate.getCollectionNames();
        fileBucketConfs.forEach(bucketConf -> {
            String collectionName = FeatureBucketStoreMongoImpl.getCollectionName(bucketConf);
            Assert.assertTrue(String.format("%s collection should exist",collectionName),allMongoCollections.contains(collectionName));
            Assert.assertEquals(expectedFeatureBucketsAmount,mongoTemplate.getCollection(collectionName).count());
        });
    }

    @Test
    public void shouldCreateBucketsForMultipleUsers() throws GeneratorException {
        EnrichedFileGenerator fileGenerator = new EnrichedFileGenerator(enrichedDataStore);
        fileGenerator.setUserGenerator(new UserRegexCyclicValuesGenerator("s-[1-5]{1}"));
        fileGenerator.generateAndPersistSanityData(10);
        executeAndAssertCommandSuccess(EXECUTION_COMMAND);
        assertFeatureBuckets(5*(DAYS_BACK_FROM - DAYS_BACK_TO));
    }

    @Test
    public void shouldCreateBucketsForPartialDateAndMultipleUsers() throws GeneratorException {
        EnrichedFileGenerator fileGenerator = new EnrichedFileGenerator(enrichedDataStore);
        fileGenerator.setUserGenerator(new UserRegexCyclicValuesGenerator("s-[1-8]{1}"));
        fileGenerator.generateAndPersistSanityData(10);
        executeAndAssertCommandSuccess(String.format(COMMAND_FORMAT, ADE_EVENT_TYPE, START_DATE.minus(1, ChronoUnit.DAYS).toString(), END_DATE.minus(1, ChronoUnit.DAYS).toString(), 3600));
        assertFeatureBuckets(8);
    }

    @Configuration
    @Import({EnrichedSourceSpringConfig.class, ModelFeatureAggregationBucketsApplicationConfigTest.class, PresidioCommands.class, EnrichedFileGeneratorConfig.class})
    protected static class springConfigScoreAggregationsApplication {

    }
}
