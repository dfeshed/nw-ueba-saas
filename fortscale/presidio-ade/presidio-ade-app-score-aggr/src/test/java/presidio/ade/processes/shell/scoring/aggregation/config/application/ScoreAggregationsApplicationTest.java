package presidio.ade.processes.shell.scoring.aggregation.config.application;

import com.mongodb.DBCollection;
import fortscale.aggregation.configuration.AslConfigurationPaths;
import fortscale.aggregation.configuration.AslResourceFactory;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.ml.model.ModelConf;
import fortscale.ml.model.ModelConfService;
import fortscale.ml.model.ModelConfServiceBuilder;
import fortscale.ml.model.builder.CategoryRarityModelBuilderConf;
import fortscale.ml.model.builder.TimeModelBuilderConf;
import fortscale.ml.model.metrics.CategoryRarityModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.CategoryRarityModeBuilderMetricsContainerConfig;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStore;
import fortscale.ml.scorer.Scorer;
import fortscale.ml.scorer.config.AdeEventTypeScorerConfs;
import fortscale.ml.scorer.config.IScorerConf;
import fortscale.ml.scorer.config.ScorerConfService;
import fortscale.ml.scorer.config.ScorerContainerConf;
import fortscale.utils.factory.FactoryService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.store.record.StoreManagerMetadataProperties;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.aggregated.AdeAggregationRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.aggr.AggrDataToCollectionNameTranslator;
import presidio.ade.domain.store.aggr.AggrRecordsMetadata;
import presidio.ade.domain.store.scored.AdeScoredEnrichedRecordToCollectionNameTranslator;
import presidio.ade.test.utils.generators.EnrichedFileGeneratorConfig;
import presidio.ade.test.utils.generators.models.CategoryRarityModelGenerator;
import presidio.ade.test.utils.generators.models.ModelDaoGenerator;
import presidio.ade.test.utils.generators.models.TimeModelGenerator;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;
import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord.EVENT_TYPE_PREFIX;


/**
 * Created by barak_schuster on 7/25/17.
 */
@Category(ModuleTestCategory.class)
@ContextConfiguration
public class ScoreAggregationsApplicationTest extends EnrichedFileSourceBaseAppTest {
    private static final int DAYS_BACK_FROM = 3;
    private static final int DAYS_BACK_TO = 1;

    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), DURATION);
    private static final Instant END_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_TO)), DURATION);

    public static final String EXECUTION_COMMAND = String.format("run  --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s ", ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 3600);
    public static final String ENRICHED_RECORD_MODELS_GROUP_NAME = "enriched-record-models";
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
    @Autowired
    private ModelStore modelStore;
    @Autowired
    private AslResourceFactory aslResourceFactory;
    @Autowired
    private CategoryRarityModelBuilderMetricsContainer categoryRarityModelBuilderMetricsContainer;

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
    protected void assertSanityTest(List generatedData) throws GeneratorException {
        // process is executed without models. scores are expected to be zero
        assertScoredEnrichedEventsCreated(-1D,(List< EnrichedFileRecord>)generatedData);

        assertScoredAggregationCollectionsCreated(-1D);
    }

    private void assertScoredAggregationCollectionsCreated(Double featureValueGt) {
        List<AggregatedFeatureEventConf> hourlyScoreAggrConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().stream().filter(conf -> {
            boolean isScoreAggregationConf = AggregatedFeatureType.fromCodeRepresentation(conf.getType()).equals(AggregatedFeatureType.SCORE_AGGREGATION);

            String strategyName = conf.getBucketConf().getStrategyName();
            boolean isHourly = FixedDurationStrategy.fromStrategyName(strategyName).equals(FixedDurationStrategy.HOURLY);
            return (isScoreAggregationConf && isHourly && conf.getName().contains("File"));
        }).collect(Collectors.toList());

        hourlyScoreAggrConfs.forEach(conf -> {
            AggrRecordsMetadata metadata = new AggrRecordsMetadata(conf.getName(), AggregatedFeatureType.SCORE_AGGREGATION);

            String collectionName = aggrDataToCollectionNameTranslator.toCollectionName(metadata);
            DBCollection collection = mongoTemplate.getCollection(collectionName);
            Query query = new Query();
            query.addCriteria(Criteria.where(AdeAggregationRecord.FEATURE_VALUE_FIELD_NAME).gt(featureValueGt));
            Assert.assertTrue(String.format("scored aggr collection=%s must have at least one record with feature value greater than=%s", collectionName,featureValueGt.toString()), collection.count(query.getQueryObject()) > 0);
            Assert.assertEquals(48,collection.count());
        });
    }

    private void assertScoredEnrichedEventsCreated(Double scoreGt, List<EnrichedFileRecord> generatedData) {
        AdeEventTypeScorerConfs adeEventTypeScorerConfs = scorerConfService.getAdeEventTypeScorerConfs(ADE_EVENT_TYPE.getName());
        List<IScorerConf> scorerConfs = adeEventTypeScorerConfs.getScorerConfs().stream().filter(x -> x instanceof ScorerContainerConf).collect(Collectors.toList());
        List<ScorerContainerConf> scorerContainerConfs = new ArrayList<>();
        scorerConfs.forEach(scorerConf -> scorerContainerConfs.add(((ScorerContainerConf) scorerConf)));
        int amountOfGeneratedData = generatedData.size();
        scorerContainerConfs.forEach(
                scorerContainerConf -> {
                    List<IScorerConf> scorerConfList = scorerContainerConf.getScorerConfList();
                    Assert.assertTrue("scorers conf should contain at least one configuration value", scorerConfList.size() > 0);
                    scorerConfList.forEach(
                            fileScorer -> {
                                String scorerName = fileScorer.getName();
                                String adeEventType = String.format("%s_%s_%s", EVENT_TYPE_PREFIX.toLowerCase(), ADE_EVENT_TYPE.getName(), scorerName);
                                String collectionName = adeScoredEnrichedRecordToCollectionNameTranslator.toCollectionName(adeEventType);
                                DBCollection collection = mongoTemplate.getCollection(collectionName);
                                Query query = new Query(Criteria.where(AdeScoredEnrichedRecord.SCORE_FIELD_NAME).gt(scoreGt));
                                long amountOfDocumentsWithScoreGt = collection.count(query.getQueryObject());
                                Assert.assertTrue(String.format("scored collection=%s must have at least one scored record with score greater than=%s", collectionName,scoreGt.toString()), amountOfDocumentsWithScoreGt > 0);
                                // conditional scorer filter the records
                                long amountOfDocuments = collection.count();
                                String message = String.format("collection=%s with conditional does not have expected amount of docs", collectionName);
                                if(scorerName.contains("FilePermissionChange"))
                                {
                                    Assert.assertEquals(message,96, amountOfDocuments);
                                }
                                else if (scorerName.contains("FileAction"))
                                {
                                    Assert.assertEquals(message,96, amountOfDocuments);
                                }
                                else {
                                    Assert.assertEquals(message,amountOfGeneratedData,amountOfDocuments );
                                }
                            });
                });
        Assert.assertTrue(scorerContainerConfs.size() > 0);
    }

    @Test
    public void scoreEventsByModels() throws GeneratorException {
        // generate all file category rarity models
        generateEnrichedRawModels();
        // sanity data should contain data that is related to feature operationType
        List<EnrichedFileRecord> generatedData = (List<EnrichedFileRecord>)eventsGenerator.generateAndPersistSanityData(getInterval());
        // score enriched and build score aggregation
        executeAndAssertCommandSuccess(getSanityTestExecutionCommand());
        // each scored enrich should contain at least one document with score>0
        assertScoredEnrichedEventsCreated(0D, (List<EnrichedFileRecord>) generatedData);
        // scored aggregations should contain records with values>0 sinces their are scored enriched events
        assertScoredAggregationCollectionsCreated(0D);
    }

    private void generateEnrichedRawModels() throws GeneratorException {
        Collection<AslConfigurationPaths> modelConfigurationPathsCollection = Arrays.asList(
                new AslConfigurationPaths(ENRICHED_RECORD_MODELS_GROUP_NAME, "classpath*:config/asl/models/enriched-records/"));
        ModelConfServiceBuilder modelConfServiceBuilder = new ModelConfServiceBuilder(modelConfigurationPathsCollection, aslResourceFactory);
        ModelConfService modelConfService = modelConfServiceBuilder.buildModelConfService(ENRICHED_RECORD_MODELS_GROUP_NAME + "." + ADE_EVENT_TYPE.toString());
        List<ModelConf> modelConfs = modelConfService.getModelConfs();

        for (ModelConf conf :
                modelConfs) {
            ModelDaoGenerator modelDaoGenerator = null;
            if (conf.getModelBuilderConf() instanceof CategoryRarityModelBuilderConf) {
                CategoryRarityModelGenerator categoryRarityModelGenerator = new CategoryRarityModelGenerator((CategoryRarityModelBuilderConf) conf.getModelBuilderConf(), categoryRarityModelBuilderMetricsContainer);
                modelDaoGenerator = new ModelDaoGenerator(categoryRarityModelGenerator);
            }
            if (conf.getModelBuilderConf() instanceof TimeModelBuilderConf) {
                TimeModelGenerator categoryRarityModelGenerator = new TimeModelGenerator((TimeModelBuilderConf) conf.getModelBuilderConf());
                modelDaoGenerator = new ModelDaoGenerator(categoryRarityModelGenerator);
            }
            Assert.assertTrue("model builder conf is expected to have a matching model generator. are you trying to add new model builder without adding tests?! good luck. :)", modelDaoGenerator != null);

            List<ModelDAO> modelDAOS = modelDaoGenerator.generate();
            Assert.assertFalse("generator is expected to generate at least one model", modelDAOS.isEmpty());
            modelDAOS.forEach(modelDAO -> modelStore.save(conf, modelDAO, new StoreManagerMetadataProperties()));
        }
    }

    @Configuration
    @Import({EnrichedSourceSpringConfig.class, ScoreAggregationsApplicationConfigTest.class, PresidioCommands.class, EnrichedFileGeneratorConfig.class, CategoryRarityModeBuilderMetricsContainerConfig.class})
    protected static class springConfigScoreAggregationsApplication {

    }
}