package presidio.ade.processes.shell;

import fortscale.aggregation.feature.bucket.BucketConfigurationService;
import fortscale.aggregation.feature.bucket.FeatureBucketConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.common.general.Schema;
import fortscale.common.shell.command.PresidioCommands;
import fortscale.ml.model.*;
import fortscale.ml.model.builder.IModelBuilder;
import fortscale.ml.model.builder.factories.GaussianPriorModelBuilderFactory;
import fortscale.ml.model.builder.gaussian.ContinuousMaxHistogramModelBuilderConf;
import fortscale.ml.model.builder.gaussian.prior.GaussianPriorModelBuilderConf;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.metrics.GaussianPriorModelBuilderMetricsContainer;
import fortscale.ml.model.metrics.GaussianPriorModelBuilderMetricsContainerConfig;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.utils.data.Pair;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.pagination.aggregated.AggregatedDataPaginationParam;
import presidio.ade.domain.pagination.aggregated.AggregatedDataReader;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.aggregated.AggregatedFeatureType;
import presidio.ade.domain.record.aggregated.ScoredFeatureAggregationRecord;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataStore;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.test.utils.generators.MultiFileEventGenerator;
import presidio.ade.test.utils.generators.factory.FileEventGeneratorTemplateFactory;
import presidio.ade.test.utils.tests.BaseAppTest;
import presidio.data.ade.AdeFileOperationGeneratorTemplateFactory;
import presidio.data.generators.common.GeneratorException;
import presidio.data.generators.common.StringRegexCyclicValuesGenerator;
import presidio.data.generators.common.time.ITimeGeneratorFactory;
import presidio.data.generators.common.time.SingleTimeGeneratorFactory;
import presidio.data.generators.fileop.IFileOperationGenerator;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Barak Schuster
 */
@Category(ModuleTestCategory.class)
@ContextConfiguration
public class FeatureAggregationsApplicationTest extends BaseAppTest {
    private static final Schema ADE_EVENT_TYPE = Schema.FILE;
    private static final String COMMAND = "run --schema %s --start_date %s --end_date %s --fixed_duration_strategy %s";

    @Autowired
    private GaussianPriorModelBuilderFactory gaussianPriorModelBuilderFactory;
    @Autowired
    private AggregatedDataReader scoredFeatureAggregatedReader;
    @Autowired
    private ModelsCacheService modelsCacheService;
    @Autowired
    private EnrichedDataStore enrichedDataStore;
    @Autowired
    private ModelConfService modelConfService;

    @Autowired
    @Qualifier("bucketConfigurationService")
    private BucketConfigurationService bucketConfigurationService;
    @Autowired
    @Qualifier("levelThreeBucketConfigurationService")
    private BucketConfigurationService levelThreeBucketConfigurationService;
    @Autowired
    @Qualifier("aggregatedFeatureEventsConfService")
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    @Qualifier("levelThreeAggregatedFeatureEventsConfService")
    private AggregatedFeatureEventsConfService levelThreeAggregatedFeatureEventsConfService;

    @Override
    protected String getContextTestExecutionCommand() {
        int DAYS_BACK_FROM = 2;
        Instant START_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(DAYS_BACK_FROM)), Duration.ofDays(1));
        Instant END_DATE = START_DATE.plus(Duration.ofHours(1));
        return String.format(COMMAND, ADE_EVENT_TYPE, START_DATE.toString(), END_DATE.toString(), 3600);
    }

    @Before
    public void setUp() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
        modelsCacheService.resetCache();
    }

    /**
     * Test feature scores and feature values of all features.
     * User get high feature values, while user has low anomalies model.
     */
    @Test
    public void lowAnomaliesUserTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 1;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 2;
        String contextId = "user";
        Set<String> contextIdSet = new HashSet<>();
        contextIdSet.add("userId#" + contextId);
        TimeRange timeRange = generateData(getAllFileOperationGenerator(), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, contextId);
        Instant start = TimeService.floorTime(timeRange.getStart(), Duration.ofDays(1));
        Instant end = TimeService.floorTime(timeRange.getEnd().plus(Duration.ofDays(1)), Duration.ofDays(1));
        createModels(contextIdSet, start, 50, 2.6, 0.19, 5);
        String command = String.format(COMMAND, ADE_EVENT_TYPE, start, end, 3600);
        executeAndAssertCommandSuccess(command);
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet = new HashSet<>();

        for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfs) {
            AggregatedDataPaginationParam aggregatedDataPaginationParam = new AggregatedDataPaginationParam(aggregatedFeatureEventConf.getName(), AggregatedFeatureType.FEATURE_AGGREGATION);
            aggregatedDataPaginationParamSet.add(aggregatedDataPaginationParam);
        }

        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = scoredFeatureAggregatedReader.readRecords(aggregatedDataPaginationParamSet, contextIdSet, timeRange);
        Map<String, Double> featureToScore = getExpectedFeatureToScoreOfLowAnomaliesUser();
        Map<String, Double> featureToValue = getExpectedFeatureToValue();

        for (ScoredFeatureAggregationRecord scoredFeatureAggregationRecord : scoredFeatureAggregationRecords) {
            Assert.assertEquals(
                    String.format("wrong score for feature %s", scoredFeatureAggregationRecord.getFeatureName()),
                    featureToScore.get(scoredFeatureAggregationRecord.getFeatureName()),
                    scoredFeatureAggregationRecord.getScore());
            Assert.assertEquals(
                    String.format("wrong value for feature %s", scoredFeatureAggregationRecord.getFeatureName()),
                    featureToValue.get(scoredFeatureAggregationRecord.getFeatureName()),
                    scoredFeatureAggregationRecord.getFeatureValue());
        }
    }

    /**
     * Test feature scores and feature values of all features.
     * User get high/low/avg feature values (depends on feature), while user has avg anomalies model.
     */
    @Test
    public void avgAnomaliesUserTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 1;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 2;
        String contextId = "user";
        Set<String> contextIdSet = new HashSet<>();
        contextIdSet.add("userId#" + contextId);
        TimeRange timeRange = generateData(getAllFileOperationGenerator(), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, contextId);
        Instant start = TimeService.floorTime(timeRange.getStart(), Duration.ofDays(1));
        Instant end = TimeService.floorTime(timeRange.getEnd().plus(Duration.ofDays(1)), Duration.ofDays(1));

        // numOfRecords to featureValue
        // 10 -> 1
        // 10 -> 2
        // 10 -> 5
        // 20 -> 12
        createModels(contextIdSet, start, 50, 6.4, 0.679, 12);
        String command = String.format(COMMAND, ADE_EVENT_TYPE, start, end, 3600);
        executeAndAssertCommandSuccess(command);
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet = new HashSet<>();

        for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfs) {
            AggregatedDataPaginationParam aggregatedDataPaginationParam = new AggregatedDataPaginationParam(aggregatedFeatureEventConf.getName(), AggregatedFeatureType.FEATURE_AGGREGATION);
            aggregatedDataPaginationParamSet.add(aggregatedDataPaginationParam);
        }

        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = scoredFeatureAggregatedReader.readRecords(aggregatedDataPaginationParamSet, contextIdSet, timeRange);
        Map<String, Double> featureToScore = getExpectedFeatureToScoreOfAvgAnomaliesUser();
        Map<String, Double> featureToValue = getExpectedFeatureToValue();

        for (ScoredFeatureAggregationRecord scoredFeatureAggregationRecord : scoredFeatureAggregationRecords) {
            Assert.assertEquals(featureToScore.get(scoredFeatureAggregationRecord.getFeatureName()), scoredFeatureAggregationRecord.getScore());
            Assert.assertEquals(featureToValue.get(scoredFeatureAggregationRecord.getFeatureName()), scoredFeatureAggregationRecord.getFeatureValue());
        }
    }

    /**
     * Test score of user with same behaviour with different modes.
     * Mean and maxValue of model grows gradually over the time.
     * Result: Gradually reduced score.
     */
    @Test
    public void graduallyRisingScoreTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 1;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 2;
        String contextId = "user";
        Set<String> contextIdSet = new HashSet<>();
        contextIdSet.add("userId#" + contextId);
        List<Pair<Double, Double>> meanToMaxValueList = new ArrayList<>();
        meanToMaxValueList.add(new Pair<>(0.001, 0.01));
        meanToMaxValueList.add(new Pair<>(1.0, 4.0));
        meanToMaxValueList.add(new Pair<>(4.0, 6.0));
        meanToMaxValueList.add(new Pair<>(10.0, 12.0));
        meanToMaxValueList.add(new Pair<>(20.0, 36.0));
        meanToMaxValueList.add(new Pair<>(22.0, 38.0));
        meanToMaxValueList.add(new Pair<>(30.0, 38.0));
        meanToMaxValueList.add(new Pair<>(36.0, 38.0));
        Instant startInstant = Instant.now();
        Instant endInstant = Instant.EPOCH;
        int numOfDays = 0;

        for (Pair<Double, Double> pair : meanToMaxValueList) {
            TimeRange timeRange = generateData(getAllFileOperationGenerator(), startHourOfDay, endHourOfDay, daysBackFrom - numOfDays, daysBackTo - numOfDays, contextId);
            Instant start = TimeService.floorTime(timeRange.getStart(), Duration.ofDays(1));
            Instant end = TimeService.floorTime(timeRange.getEnd().plus(Duration.ofDays(1)), Duration.ofDays(1));
            createModels(contextIdSet, start, 50, pair.getKey(), 0.19, pair.getValue());
            if (startInstant.isAfter(start)) startInstant = start;
            if (endInstant.isBefore(end)) endInstant = end;
            numOfDays += 2;
        }

        String command = String.format(COMMAND, ADE_EVENT_TYPE, startInstant, endInstant, 3600);
        executeAndAssertCommandSuccess(command);
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet = new HashSet<>();

        for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfs) {
            AggregatedDataPaginationParam aggregatedDataPaginationParam = new AggregatedDataPaginationParam(aggregatedFeatureEventConf.getName(), AggregatedFeatureType.FEATURE_AGGREGATION);
            aggregatedDataPaginationParamSet.add(aggregatedDataPaginationParam);
        }

        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = scoredFeatureAggregatedReader.readRecords(aggregatedDataPaginationParamSet, contextIdSet, new TimeRange(startInstant, endInstant));
        Instant start = startInstant;
        scoredFeatureAggregationRecords.stream()
                .filter(record -> record.getStartInstant().equals(start))
                .collect(Collectors.toList())
                .forEach(record -> {
                    Double score = record.getScore();
                    Assert.assertTrue(score > 0);
                    String featureName = record.getFeatureName();
                    List<ScoredFeatureAggregationRecord> filteredRecordsByFeature = scoredFeatureAggregationRecords.stream()
                            .filter(r -> r.getFeatureName().equals(featureName))
                            .collect(Collectors.toList());
                    List<Double> results = filteredRecordsByFeature.stream()
                            .sorted(Comparator.comparing(AdeRecord::getStartInstant))
                            .map(ScoredFeatureAggregationRecord::getScore)
                            .collect(Collectors.toList());

                    for (Double result : results) {
                        Assert.assertTrue(result <= score);
                        score = result;
                    }
                });
    }

    /**
     * Test that only feature that related to file open have score greater than 0, while other scores are 0.
     */
    @Test
    public void fileOpenedFeaturesTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 1;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 2;
        String contextId = "user";
        Set<String> contextIdSet = new HashSet<>();
        contextIdSet.add("userId#" + contextId);
        List<IFileOperationGenerator> fileOperationGenerators = new ArrayList<>();
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createOpenFileOperationsGenerator());
        TimeRange timeRange = generateData(fileOperationGenerators, startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, contextId);
        Instant start = TimeService.floorTime(timeRange.getStart(), Duration.ofDays(1));
        Instant end = TimeService.floorTime(timeRange.getEnd().plus(Duration.ofDays(1)), Duration.ofDays(1));

        // numOfRecords to featureValue
        // 10 -> 1
        // 10 -> 2
        // 10 -> 3
        // 20 -> 5
        createModels(contextIdSet, start, 50, 2.6, 0.19, 5);
        String command = String.format(COMMAND, ADE_EVENT_TYPE, start, end, 3600);
        executeAndAssertCommandSuccess(command);
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet = new HashSet<>();

        for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfs) {
            AggregatedDataPaginationParam aggregatedDataPaginationParam = new AggregatedDataPaginationParam(aggregatedFeatureEventConf.getName(), AggregatedFeatureType.FEATURE_AGGREGATION);
            aggregatedDataPaginationParamSet.add(aggregatedDataPaginationParam);
        }

        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = scoredFeatureAggregatedReader.readRecords(aggregatedDataPaginationParamSet, contextIdSet, timeRange);
        Set<String> features = new HashSet<>();
        features.add("numberOfSuccessfulFileActionsUserIdFileHourly");
        features.add("numberOfDistinctFileOpenedUserIdFileHourly");

        for (ScoredFeatureAggregationRecord scoredFeatureAggregationRecord : scoredFeatureAggregationRecords) {
            if(features.contains(scoredFeatureAggregationRecord.getFeatureName())){
                Assert.assertTrue(scoredFeatureAggregationRecord.getScore() > 0D);
            }
            else{
                Assert.assertEquals(scoredFeatureAggregationRecord.getScore(),(Double)0D);
            }
        }
    }

    /**
     * Test feature scores and feature values without continuous and gaussian prior models.
     */
    @Test
    public void noModelsTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 1;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 2;
        String contextId = "user";
        Set<String> contextIdSet = new HashSet<>();
        contextIdSet.add("userId#" + contextId);
        TimeRange timeRange = generateData(getAllFileOperationGenerator(), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, contextId);
        Instant start = TimeService.floorTime(timeRange.getStart(), Duration.ofDays(1));
        Instant end = TimeService.floorTime(timeRange.getEnd().plus(Duration.ofDays(1)), Duration.ofDays(1));
        String command = String.format(COMMAND, ADE_EVENT_TYPE, start, end, 3600);
        executeAndAssertCommandSuccess(command);
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet = new HashSet<>();

        for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfs) {
            AggregatedDataPaginationParam aggregatedDataPaginationParam = new AggregatedDataPaginationParam(aggregatedFeatureEventConf.getName(), AggregatedFeatureType.FEATURE_AGGREGATION);
            aggregatedDataPaginationParamSet.add(aggregatedDataPaginationParam);
        }

        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = scoredFeatureAggregatedReader.readRecords(aggregatedDataPaginationParamSet, contextIdSet, timeRange);
        Double expectedScore = 0.0;
        Map<String, Double> featureToValue = getExpectedFeatureToValue();

        for (ScoredFeatureAggregationRecord scoredFeatureAggregationRecord : scoredFeatureAggregationRecords) {
            Assert.assertEquals(expectedScore, scoredFeatureAggregationRecord.getScore());
            Assert.assertEquals(featureToValue.get(scoredFeatureAggregationRecord.getFeatureName()), scoredFeatureAggregationRecord.getFeatureValue());
        }
    }

    @Test
    public void expectedTimeTest() throws GeneratorException {
        int daysBackFrom = 30;
        int durationOfProcess = 4;
        int daysBackTo = daysBackFrom - durationOfProcess;
        int startHourOfDay = 1;
        int endHourOfDay = 2;
        String contextId = "user";
        Set<String> contextIdSet = new HashSet<>();
        contextIdSet.add("userId#" + contextId);
        TimeRange timeRange = generateData(getAllFileOperationGenerator(), startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, contextId);
        Instant start = TimeService.floorTime(timeRange.getStart(), Duration.ofDays(1));
        Instant end = TimeService.floorTime(timeRange.getEnd().plus(Duration.ofDays(1)), Duration.ofDays(1));
        createModels(contextIdSet, start, 50, 2.6, 0.19, 5);
        createModels(contextIdSet, start.plus(Duration.ofDays(2)), 50, 2.6, 0.19, 5);
        String command = String.format(COMMAND, ADE_EVENT_TYPE, start.plus(Duration.ofDays(1)), end.minus(Duration.ofDays(1)), 3600);
        executeAndAssertCommandSuccess(command);
        List<AggregatedFeatureEventConf> aggregatedFeatureEventConfs = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList();
        Set<AggregatedDataPaginationParam> aggregatedDataPaginationParamSet = new HashSet<>();

        for (AggregatedFeatureEventConf aggregatedFeatureEventConf : aggregatedFeatureEventConfs) {
            AggregatedDataPaginationParam aggregatedDataPaginationParam = new AggregatedDataPaginationParam(aggregatedFeatureEventConf.getName(), AggregatedFeatureType.FEATURE_AGGREGATION);
            aggregatedDataPaginationParamSet.add(aggregatedDataPaginationParam);
        }

        List<ScoredFeatureAggregationRecord> scoredFeatureAggregationRecords = scoredFeatureAggregatedReader.readRecords(aggregatedDataPaginationParamSet, contextIdSet, timeRange);

        // Assert that features created in expected time range.
        for (ScoredFeatureAggregationRecord scoredFeatureAggregationRecord : scoredFeatureAggregationRecords) {
            Assert.assertTrue(scoredFeatureAggregationRecord.getStartInstant().getEpochSecond() >= start.plus(Duration.ofDays(1)).getEpochSecond());
            Assert.assertTrue(scoredFeatureAggregationRecord.getStartInstant().getEpochSecond() < end.minus(Duration.ofDays(1)).getEpochSecond());
        }
    }

    @Test
    public void validateContextFieldNamesOfLevelThreeFeatureBucketConfs() {
        levelThreeAggregatedFeatureEventsConfService.getAggregatedFeatureEventConfList().forEach(levelThreeAggregatedFeatureEventConf -> {
            String levelThreeFeatureBucketConfName = levelThreeAggregatedFeatureEventConf.getBucketConfName();
            FeatureBucketConf levelThreeFeatureBucketConf = levelThreeBucketConfigurationService.getBucketConf(levelThreeFeatureBucketConfName);
            Assert.assertNotNull(levelThreeFeatureBucketConf);
            List<String> levelThreeContextFieldNames = levelThreeFeatureBucketConf.getContextFieldNames();
            List<String> adeEventTypes = levelThreeFeatureBucketConf.getAdeEventTypes();
            Assert.assertEquals(1, adeEventTypes.size());
            String adeEventType = adeEventTypes.get(0);
            adeEventType = adeEventType.substring("aggr_event.".length());
            AggregatedFeatureEventConf aggregatedFeatureEventConf = aggregatedFeatureEventsConfService.getAggregatedFeatureEventConf(adeEventType);
            Assert.assertNotNull(aggregatedFeatureEventConf);
            String featureBucketConfName = aggregatedFeatureEventConf.getBucketConfName();
            FeatureBucketConf featureBucketConf = bucketConfigurationService.getBucketConf(featureBucketConfName);
            Assert.assertNotNull(featureBucketConf);
            List<String> contextFieldNames = featureBucketConf.getContextFieldNames();
            Assert.assertTrue(contextFieldNames.containsAll(levelThreeContextFieldNames));
        });
    }

    /**
     * Generate records every 5 minutes.
     * Create context and time generators.
     *
     * @param fileOperationGeneratorList file operation generator list.
     * @param startHourOfDay             start hour of day.
     * @param endHourOfDay               end hour of day.
     * @param contextIdPattern           contextId pattern for contextIdGenerator.
     * @return TimeRange of records.
     */
    public TimeRange generateData(List<IFileOperationGenerator> fileOperationGeneratorList, int startHourOfDay, int endHourOfDay, int daysBackFrom, int daysBackTo, String contextIdPattern) throws GeneratorException {
        StringRegexCyclicValuesGenerator contextIdGenerator = new StringRegexCyclicValuesGenerator(contextIdPattern);
        ITimeGeneratorFactory timeGeneratorFactory = new SingleTimeGeneratorFactory(startHourOfDay, endHourOfDay, daysBackFrom, daysBackTo, 5);
        FileEventGeneratorTemplateFactory fileEventGeneratorTemplateFactory = new FileEventGeneratorTemplateFactory();
        MultiFileEventGenerator multiFileEventGenerator = fileEventGeneratorTemplateFactory.createMultiFileEventGenerator(timeGeneratorFactory, contextIdGenerator, fileOperationGeneratorList);
        List<EnrichedFileRecord> enrichedFileRecords = multiFileEventGenerator.generate();
        EnrichedRecordsMetadata recordsMetadata = new EnrichedRecordsMetadata("file", Instant.now(), Instant.now());
        enrichedDataStore.store(recordsMetadata, enrichedFileRecords, new StoreMetadataProperties());
        Instant start = enrichedFileRecords.stream().min(Comparator.comparing(EnrichedFileRecord::getStartInstant)).get().getStartInstant();
        Instant end = enrichedFileRecords.stream().max(Comparator.comparing(EnrichedFileRecord::getStartInstant)).get().getStartInstant();
        return new TimeRange(start, end);
    }

    /**
     * Build expected scores due to results of the test.
     */
    private Map<String, Double> getExpectedFeatureToScoreOfLowAnomaliesUser() {
        Map<String, Double> featureToScore = new HashMap<>();
        featureToScore.put("numberOfSuccessfulFileActionsUserIdFileHourly", 100.0);
        featureToScore.put("numberOfDistinctFileOpenedUserIdFileHourly", 17.85178066230927);
        featureToScore.put("numberOfFileMovedUserIdFileHourly", 100.0);
        featureToScore.put("numberOfDistinctFolderOpenedUserIdFileHourly", 17.85178066230927);
        featureToScore.put("numberOfFailedFilePermissionChangesUserIdFileHourly", 100.0);
        featureToScore.put("numberOfFileMovedToSharedDriveUserIdFileHourly", 17.85178066230927);
        featureToScore.put("numberOfFailedFileActionsUserIdFileHourly", 100.0);
        featureToScore.put("numberOfSuccessfulFilePermissionChangesUserIdFileHourly", 17.85178066230927);
        featureToScore.put("numberOfSuccessfulFileRenamedUserIdFileHourly", 17.85178066230927);
        featureToScore.put("numberOfFileDeletedUserIdFileHourly", 17.85178066230927);
        featureToScore.put("numberOfFileMovedFromSharedDriveUserIdFileHourly", 17.85178066230927);
        featureToScore.put("numberOfFileDownloadedUserIdFileHourly", 99.99961049927788);
        featureToScore.put("numberOfFileCopiedUserIdFileHourly", 50.53052381387548);
        return featureToScore;
    }

    /**
     * Build expected scores due to results of the test.
     */
    private Map<String, Double> getExpectedFeatureToScoreOfAvgAnomaliesUser() {
        Map<String, Double> featureToScore = new HashMap<>();
        featureToScore.put("numberOfSuccessfulFileActionsUserIdFileHourly", 100.0);
        featureToScore.put("numberOfDistinctFileOpenedUserIdFileHourly", 0.0);
        featureToScore.put("numberOfFileMovedUserIdFileHourly", 90.84707502883582);
        featureToScore.put("numberOfDistinctFolderOpenedUserIdFileHourly", 0.0);
        featureToScore.put("numberOfFailedFilePermissionChangesUserIdFileHourly", 40.36212319396937);
        featureToScore.put("numberOfFileMovedToSharedDriveUserIdFileHourly", 0.0);
        featureToScore.put("numberOfFailedFileActionsUserIdFileHourly", 100.0);
        featureToScore.put("numberOfSuccessfulFilePermissionChangesUserIdFileHourly", 0.0);
        featureToScore.put("numberOfSuccessfulFileRenamedUserIdFileHourly", 0.0);
        featureToScore.put("numberOfFileDeletedUserIdFileHourly", 0.0);
        featureToScore.put("numberOfFileMovedFromSharedDriveUserIdFileHourly", 0.0);
        featureToScore.put("numberOfFileDownloadedUserIdFileHourly", 93.26795434547577);
        featureToScore.put("numberOfFileCopiedUserIdFileHourly", 0.0);
        return featureToScore;
    }

    /**
     * Build expected values due to results of the test.
     */
    private Map<String, Double> getExpectedFeatureToValue() {
        Map<String, Double> featureToValue = new HashMap<>();
        featureToValue.put("numberOfSuccessfulFileActionsUserIdFileHourly", 96.0);
        featureToValue.put("numberOfDistinctFileOpenedUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFileMovedUserIdFileHourly", 24.0);
        featureToValue.put("numberOfDistinctFolderOpenedUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFailedFilePermissionChangesUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFileMovedToSharedDriveUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFailedFileActionsUserIdFileHourly", 24.0);
        featureToValue.put("numberOfSuccessfulFilePermissionChangesUserIdFileHourly", 12.0);
        featureToValue.put("numberOfSuccessfulFileRenamedUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFileDeletedUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFileMovedFromSharedDriveUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFileDownloadedUserIdFileHourly", 12.0);
        featureToValue.put("numberOfFileCopiedUserIdFileHourly", 12.0);
        return featureToValue;
    }

    /**
     * Create models:
     * Create continuous model by given params.
     * Create gaussian prior model by continuous model.
     *
     * @param N        population size.
     * @param mean     mean.
     * @param sd       standard deviation.
     * @param maxValue maximal value.
     */
    private void createModels(Set<String> contextIds, Instant endDate, long N, double mean, double sd, double maxValue) {
        // Add users in order to provide enough models for GaussianPriorModel.
        for (int i = 1; i < 30; i++) {
            contextIds.add("userId#user" + i);
        }

        List<ModelConf> modelConfs = modelConfService.getModelConfs();
        List<Model> models = new ArrayList<>();

        for (ModelConf modelConf : modelConfs) {
            if (modelConf.getModelBuilderConf() instanceof ContinuousMaxHistogramModelBuilderConf) {
                for (String contextId : contextIds) {
                    ContinuousDataModel continuousDataModel = new ContinuousDataModel().setParameters(N, round(mean), round(sd), round(maxValue));
                    ContinuousMaxDataModel model = new ContinuousMaxDataModel(continuousDataModel, continuousDataModel, N);
                    ModelDAO modelDao = new ModelDAO("test-session-id", contextId, model, endDate.minus(Duration.ofDays(90)), endDate, null);
                    mongoTemplate.insert(modelDao, "model_" + modelConf.getName());
                    models.add(continuousDataModel);

                }
            } else if (modelConf.getModelBuilderConf() instanceof GaussianPriorModelBuilderConf) {
                IModelBuilder modelBuilder = gaussianPriorModelBuilderFactory.getProduct(modelConf.getModelBuilderConf());
                Model model = modelBuilder.build(models);
                ModelDAO modelDao = new ModelDAO("test-session-id", null, model, endDate.minus(Duration.ofDays(90)), endDate, null);
                mongoTemplate.insert(modelDao, "model_" + modelConf.getName());
                models = new ArrayList<>();
            }
        }
    }

    /**
     * Get IFileOperationGenerator that cover all the features.
     *
     * @return list of fileOperationGenerators.
     */
    private List<IFileOperationGenerator> getAllFileOperationGenerator() throws GeneratorException {
        List<IFileOperationGenerator> fileOperationGenerators = new ArrayList<>();
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createLocalSharePermissionsChangeOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedLocalSharePermissionsChangeOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFolderOpenFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createDeleteFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createRenameFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createFailedRenameFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createMoveFromSharedFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createMoveToSharedFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createDownloadFileOperationsGenerator());
        fileOperationGenerators.add(new AdeFileOperationGeneratorTemplateFactory().createCopyFileOperationsGenerator());
        return fileOperationGenerators;
    }

    private static double round(double value) {
        return Math.round(value * 1000000) / 1000000d;
    }

    @Configuration
    @Import({
            FeatureAggregationsConfigurationTest.class,
            PresidioCommands.class,
            BaseAppTest.springConfig.class,
            ModelStoreConfig.class,
            GaussianPriorModelBuilderMetricsContainerConfig.class
    })
    protected static class featureAggregationsTestConfig {
        @MockBean
        private GaussianPriorModelBuilderMetricsContainer gaussianPriorModelBuilderMetricsContainer;
        @Autowired
        public GaussianPriorModelBuilderFactory gaussianPriorModelBuilderFactory;

        @Bean
        public GaussianPriorModelBuilderFactory modelBuilderFactoryService() {
            return new GaussianPriorModelBuilderFactory();
        }
    }
}
