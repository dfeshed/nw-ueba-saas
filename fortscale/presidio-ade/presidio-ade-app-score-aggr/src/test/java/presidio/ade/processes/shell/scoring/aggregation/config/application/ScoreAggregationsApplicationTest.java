package presidio.ade.processes.shell.scoring.aggregation.config.application;

import com.mongodb.DBCollection;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.AdeEventTypeScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerContainerConf;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;
import presidio.ade.domain.store.scored.AdeScoredEnrichedRecordToCollectionNameTranslator;
import presidio.ade.test.utils.generators.EnrichedFileGeneratorConfig;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord.EVENT_TYPE_PREFIX;


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
    @Autowired
    private ScorerConfService scorerConfService;
    @Autowired
    private AdeScoredEnrichedRecordToCollectionNameTranslator adeScoredEnrichedRecordToCollectionNameTranslator;
    @Autowired
    private AggrDataToCollectionNameTranslator aggrDataToCollectionNameTranslator;
    @Autowired
    private FactoryService<Scorer> scorerFactoryService;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;

    @Before
    public void beforeTest() {
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
        AdeEventTypeScorerConfs adeEventTypeScorerConfs = scorerConfService.getAdeEventTypeScorerConfs(ADE_EVENT_TYPE.getName());
        List<IScorerConf> scorerConfs = adeEventTypeScorerConfs.getScorerConfs().stream().filter(x -> x instanceof ScorerContainerConf).collect(Collectors.toList());
        List<ScorerContainerConf> scorerContainerConfs = new ArrayList<>();
        scorerConfs.forEach(scorerConf -> scorerContainerConfs.add(((ScorerContainerConf) scorerConf)));
        scorerContainerConfs.forEach(
                scorerContainerConf -> {
                    List<IScorerConf> scorerConfList = scorerContainerConf.getScorerConfList();
                    Assert.assertTrue("scorers conf should contain at least one configuration value",scorerConfList.size() > 0);
                    scorerConfList.forEach(
                            fileScorer -> {
                                String scorerName = fileScorer.getName();
                                String adeEventType = String.format("%s_%s_%s", EVENT_TYPE_PREFIX.toLowerCase(), ADE_EVENT_TYPE.getName(), scorerName);
                                String collectionName = adeScoredEnrichedRecordToCollectionNameTranslator.toCollectionName(adeEventType);
                                DBCollection collection = mongoTemplate.getCollection(collectionName);
                                Assert.assertTrue(String.format("scored collection=%s must have at least one scored record",collectionName),collection.count() > 0);
                            });
                });
        Assert.assertTrue(scorerContainerConfs.size() > 0);
        List<AggregatedFeatureEventConf> hourlyScoreAggrConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().filter(conf -> {
            boolean isScoreAggregationConf = AggregatedFeatureType.fromCodeRepresentation(conf.getType()).equals(AggregatedFeatureType.SCORE_AGGREGATION);

            String strategyName = conf.getBucketConf().getStrategyName();
            boolean isHourly = FixedDurationStrategy.fromStrategyName(strategyName).equals(FixedDurationStrategy.HOURLY);
            return (isScoreAggregationConf && isHourly && conf.getName().contains("File"));
        }).collect(Collectors.toList());

        hourlyScoreAggrConfs.forEach(conf -> {
            String collectionName = aggrDataToCollectionNameTranslator.toCollectionName(new AggrRecordsMetadata(conf.getName(),AggregatedFeatureType.SCORE_AGGREGATION));
            DBCollection collection = mongoTemplate.getCollection(collectionName);
            Assert.assertTrue(String.format("scored aggr collection=%s must have at least one record",collectionName),collection.count() > 0);
        });
    }

    @Configuration
    @Import({EnrichedSourceSpringConfig.class, ScoreAggregationsApplicationConfigTest.class, PresidioCommands.class, EnrichedFileGeneratorConfig.class})
    protected static class springConfigScoreAggregationsApplication {

    }
}