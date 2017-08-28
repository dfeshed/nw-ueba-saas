package presidio.ade.processes.shell;

import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.ml.model.ContinuousDataModel;
import fortscale.ml.model.GaussianPriorModel;
import fortscale.ml.model.Model;
import fortscale.ml.model.store.ModelDAO;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.BeforeTest;
import presidio.ade.domain.record.accumulator.AccumulatedAggregationFeatureRecord;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.file.AdeScoredFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.test.utils.generators.EnrichedFileGeneratorConfig;
import presidio.ade.test.utils.generators.EnrichedSuccessfulFileOpenedGeneratorConfig;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

/**
 * Created by barak_schuster on 7/30/17.
 */
@Category(ModuleTestCategory.class)
@ContextConfiguration
public class FeatureAggregationsApplicationTest extends EnrichedFileSourceBaseAppTest {

    private static final int DAYS_BACK_FROM = 2;

    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = START_DATE.plus(Duration.ofHours(1));

    public static final String EXECUTION_COMMAND = String.format("run --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s", ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 3600);

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Override
    protected String getSanityTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    protected int getInterval() {
        return 10;
    }

    @Before
    public void beforeTest() {
        ContinuousDataModel continuousDataModel = new ContinuousDataModel();
        continuousDataModel.setParameters(200, 1, 1, 1);
        GaussianPriorModel gaussianPriorModel = new GaussianPriorModel()
                .init(Collections.singletonList(new GaussianPriorModel.SegmentPrior().init(0, 0, 0)));

        String sessionId = "test_model";
        Instant startTime = START_DATE.minus(Duration.ofDays(30));
        Instant endTime = START_DATE.minus(Duration.ofDays(1));
        ModelDAO continousModelDao = new ModelDAO(sessionId, "userId#testUser", continuousDataModel, startTime, endTime);

        ModelDAO priorModelDao = new ModelDAO(sessionId, null, gaussianPriorModel, startTime, endTime);
        mongoTemplate.insert(continousModelDao, "model_numberOfSuccessfulFileAction.userId.file.hourly");
        mongoTemplate.insert(priorModelDao, "model_numberOfSuccessfulFileAction.userId.prior.global.file.hourly");
    }


    /**
     * Generate 6 events per hour.
     * Run feature aggregation app for 1 hour
     * Operation type of all the events is "open"
     * Create model for numberOfSuccessfulFileAction.userId.file.hourly with N=200 and mean=1
     *
     * <p>
     * Expected result:
     * scored_feature_aggr__numberOfSuccessfulFileActionUserIdFileHourly collection:
     * featureValue of each event is 6.
     * score greater then 0
     **/
    @Override
    protected void assertSanityTest() {
        String openFileCollectionName = "scored_feature_aggr__numberOfSuccessfulFileActionUserIdFileHourly";

        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = mongoTemplate.findAll(ScoredFeatureAggregationRecord.class, openFileCollectionName);

        for (ScoredFeatureAggregationRecord scoredFeatureAggregationRecord : scoredFeatureAggregationRecords) {
            Assert.assertTrue(scoredFeatureAggregationRecord.getScore() > 0);
            Assert.assertTrue(scoredFeatureAggregationRecord.getFeatureValue() == 6);
        }
    }


    @Configuration
    @Import({EnrichedSourceSpringConfig.class, FeatureAggregationsConfigurationTest.class, PresidioCommands.class, EnrichedSuccessfulFileOpenedGeneratorConfig.class})
    protected static class featureAggregationsTestConfig {

    }


}