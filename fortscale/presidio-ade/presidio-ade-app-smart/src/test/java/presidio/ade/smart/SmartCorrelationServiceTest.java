package presidio.ade.smart;

import com.google.common.collect.Lists;
import fortscale.aggregation.feature.event.AggregatedFeatureEventConf;
import fortscale.aggregation.feature.event.AggregatedFeatureEventsConfService;
import fortscale.domain.feature.score.FeatureScore;
import fortscale.ml.model.SMARTMaxValuesModel;
import fortscale.ml.model.SMARTValuesPriorModel;
import fortscale.ml.model.SmartWeightsModel;
import fortscale.ml.model.cache.ModelsCacheService;
import fortscale.ml.model.store.ModelDAO;
import fortscale.ml.model.store.ModelStoreConfig;
import fortscale.smart.SmartUtil;
import fortscale.smart.record.conf.ClusterConf;
import fortscale.smart.record.conf.SmartRecordConfService;
import fortscale.utils.fixedduration.FixedDurationStrategy;
import fortscale.utils.logging.Logger;
import fortscale.utils.store.record.StoreMetadataProperties;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.TimeService;
import javafx.util.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.aggregated.*;
import presidio.ade.domain.store.aggr.AggregatedDataStore;
import presidio.ade.test.utils.generators.AdeAggregationRecordHourlyGenerator;
import presidio.ade.test.utils.generators.ScoredFeatureAggregationRecordHourlyGenerator;
import presidio.ade.test.utils.tests.BaseAppTest;
import presidio.data.generators.common.*;
import presidio.data.generators.common.time.ITimeGenerator;
import presidio.data.generators.common.time.MinutesIncrementTimeGenerator;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by YaronDL on 9/4/2017.
 */

@Category(ModuleTestCategory.class)
@ContextConfiguration
public class SmartCorrelationServiceTest extends BaseAppTest {

    private static final Logger logger = Logger.getLogger(SmartCorrelationServiceTest.class);

    private IMapGenerator aggregatedFeatureToScoreGenerator;
    private IMapGenerator aggregatedFeatureToValueGenerator;
    private static final double avgFeatureValueForLowAnomaliesUser = 0.5;
    private static final int numOfFeaturesInGroups = 5;
    public static final String EXECUTION_COMMAND = "process --smart_record_conf_name %s --start_date %s --end_date %s";
    @Autowired
    private AggregatedDataStore aggregatedDataStore;
    @Autowired
    protected MongoTemplate mongoTemplate;
    @Autowired
    private AggregatedFeatureEventsConfService aggregatedFeatureEventsConfService;
    @Autowired
    private ModelsCacheService modelsCacheService;
    @Autowired
    private SmartRecordConfService smartRecordConfService;

    @Override
    protected String getContextTestExecutionCommand() {
        Instant startDate = TimeService.floorTime(Instant.now().minus(Duration.ofDays(30)), Duration.ofDays(1));
        Instant endDate = startDate.plus(Duration.ofDays(2));
        return String.format(EXECUTION_COMMAND, "userId_hourly", startDate.toString(), endDate.toString());
    }

    @Before
    public void setUp() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
        modelsCacheService.resetCache();
    }


    /**
     *
     *  features sorted by score: a b c f j g d e h n x z => a c j g d h n x z
     *  e - filtered as it does not exist in the tree
     *  b,f -filtered as ancestors
     *  j - filtered as it full correlated with c
     *  tree1:
     *        b
     *       / \
     *      a   f
     *     /   / \
     *    g   j  c
     *   / \
     *  d  h
     *
     *  tree2:
     *
     *       n
     *      / \
     *     x  z
     *
     *  result of correlation factor:
     *    a - 1
     *    b - 0
     *    c - 1
     *    d - 0.25
     *    f - 0
     *    g - 0.5
     *    h - 0.25
     *    j - 0
     *    n - 1
     *    x- 0.3
     *    z - 0
     *
     *
     */
    @Test
    public void smartRecordsCorrelationTest() {

        Instant start = Instant.EPOCH;
        Instant end = Instant.EPOCH.plus(Duration.ofHours(1));
        Map<String, Double> featureToScore = new HashMap<>();
        featureToScore.put("numberOfSuccessfulFilePermissionChangesUserIdFileHourly", 99D); //A
        featureToScore.put("numberOfSuccessfulFileActionsUserIdFileHourly", 98D); //B
        featureToScore.put("numberOfFailedFilePermissionChangesUserIdFileHourly", 97D); //C
        featureToScore.put("numberOfFailedFileActionsUserIdFileHourly", 93D); //D
        featureToScore.put("numberOfDistinctFileOpenedUserIdFileHourly", 92D); //E
        featureToScore.put("numberOfDistinctFolderOpenedUserIdFileHourly", 95D); // J
        featureToScore.put("numberOfFileMovedFromSharedDriveUserIdFileHourly", 85D); // n
        featureToScore.put("numberOfFileDownloadedUserIdFileHourly", 84D); // x
        featureToScore.put("numberOfFileMovedToSharedDriveUserIdFileHourly", 83D); // z

        Map<String, Double> featureToValue = new HashMap<>();
        featureToValue.put("highestStartInstantScoreUserIdFileHourly", 96D); //F
        featureToValue.put("sumOfHighestOperationTypeScoresUserIdFilePermissionChangeFileHourly", 94D); //G
        featureToValue.put("sumOfHighestOperationTypeScoresUserIdFileActionFileHourly", 91D); // H


        Map<String, String> context = new HashMap<>();
        context.put("userId", "test_user");

        // create Ps
        List<AdeAggregationRecord> PRecords = new ArrayList<>();
        featureToValue.forEach((feature, value) -> {
            AdeAggregationRecord adeAggregationRecord = new AdeAggregationRecord(start, end, feature, value,
                    "test_feature_bucket", context, AggregatedFeatureType.SCORE_AGGREGATION);
            PRecords.add(adeAggregationRecord);
        });
        aggregatedDataStore.store(PRecords, AggregatedFeatureType.SCORE_AGGREGATION, new StoreMetadataProperties());



        //create Fs
        List<AdeAggregationRecord> FRecords = new ArrayList<>();
        featureToScore.forEach((feature, score) -> {
            AdeAggregationRecord adeAggregationRecord = new ScoredFeatureAggregationRecord(score, Collections.EMPTY_LIST, start, end,
                    feature, 0.0, "test_bucket_name", context,  AggregatedFeatureType.FEATURE_AGGREGATION);
            FRecords.add(adeAggregationRecord);
        });
        aggregatedDataStore.store(FRecords, AggregatedFeatureType.FEATURE_AGGREGATION, new StoreMetadataProperties());


        String command = String.format(EXECUTION_COMMAND, "userId_hourly", start, start.plus(Duration.ofDays(1)));
        executeAndAssertCommandSuccess(command);

        List<SmartRecord> smartRecords = mongoTemplate.findAll(SmartRecord.class, "smart_userId_hourly");

        assertSmartRecords(smartRecords);
    }

    public void assertSmartRecords(List<SmartRecord> smartRecords){

        Map<String, Double> featureToCorrelation = new HashMap<>();
        featureToCorrelation.put("numberOfSuccessfulFilePermissionChangesUserIdFileHourly", 1.0); //A
        featureToCorrelation.put("numberOfSuccessfulFileActionsUserIdFileHourly", 0.0); //B
        featureToCorrelation.put("numberOfFailedFilePermissionChangesUserIdFileHourly", 1.0); //C
        featureToCorrelation.put("numberOfFailedFileActionsUserIdFileHourly", 0.25); //D
        featureToCorrelation.put("numberOfDistinctFolderOpenedUserIdFileHourly", 0.0); // J
        featureToCorrelation.put("numberOfFileMovedFromSharedDriveUserIdFileHourly", 1.0); // n
        featureToCorrelation.put("numberOfFileDownloadedUserIdFileHourly", 0.3); // x
        featureToCorrelation.put("numberOfFileMovedToSharedDriveUserIdFileHourly", 0.0); // z
        featureToCorrelation.put("highestStartInstantScoreUserIdFileHourly", 0.0); //F
        featureToCorrelation.put("sumOfHighestOperationTypeScoresUserIdFilePermissionChangeFileHourly", 0.5); //G
        featureToCorrelation.put("sumOfHighestOperationTypeScoresUserIdFileActionFileHourly", 0.25); // H


        Pair<String, Double> notInfluencedFeatureToScore = new Pair<>("numberOfDistinctFileOpenedUserIdFileHourly", 92D);

        smartRecords.forEach(smartRecord -> {
            smartRecord.getSmartAggregationRecords().forEach(record -> {
                if(!record.getAggregationRecord().getFeatureName().equals(notInfluencedFeatureToScore.getKey())){
                    Double score = SmartUtil.getAdeAggregationRecordScore(record.getAggregationRecord());
                    Double correlationFactor = record.getCorrelationFactor();
                    Double oldScore = record.getOldScore();

                    Double expectedCorrelationFactor = featureToCorrelation.get(record.getAggregationRecord().getFeatureName());
                    Assert.assertTrue(correlationFactor.equals(expectedCorrelationFactor));
                    Assert.assertTrue(score.equals(oldScore*correlationFactor));
                }
                else{
                    Double score = SmartUtil.getAdeAggregationRecordScore(record.getAggregationRecord());
                    Assert.assertTrue(score.equals(notInfluencedFeatureToScore.getValue()));
                }

            });
        });


    }

    @Configuration
    @Import({presidio.ade.smart.config.SmartApplicationCorrelationConfigurationTest.class, BaseAppTest.springConfig.class, ModelStoreConfig.class})
    protected static class springConfigModelingServiceApplication {

    }
}
