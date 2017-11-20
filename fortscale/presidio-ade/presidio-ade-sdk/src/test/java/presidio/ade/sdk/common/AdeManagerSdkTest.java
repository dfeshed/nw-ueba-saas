package presidio.ade.sdk.common;

import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.SystemDateService;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.impl.config.SystemDateServiceImplForcedConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.IndexInfo;
import org.springframework.data.util.Pair;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import presidio.ade.domain.record.AdeRecord;
import presidio.ade.domain.record.enriched.AdeScoredEnrichedRecord;
import presidio.ade.domain.record.enriched.file.AdeScoredFileRecord;
import presidio.ade.domain.store.enriched.EnrichedDataAdeToCollectionNameTranslator;
import presidio.ade.domain.store.enriched.EnrichedRecordsMetadata;
import presidio.ade.sdk.data_generator.MockedEnrichedRecord;
import presidio.ade.sdk.data_generator.MockedEnrichedRecordGenerator;
import presidio.ade.sdk.data_generator.MockedEnrichedRecordGeneratorConfig;
import presidio.ade.test.utils.generators.ScoredEnrichedFileGenerator;
import presidio.ade.test.utils.generators.ScoredEnrichedFileGeneratorConfig;
import presidio.data.generators.common.GeneratorException;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static presidio.ade.test.utils.generators.ScoredEnrichedFileGenerator.GENERATED_SCORE;
import static presidio.ade.test.utils.generators.ScoredEnrichedFileGenerator.GENERATED_USER;

/**
 * @author Barak Schuster
 */
@ContextConfiguration
@RunWith(SpringRunner.class)
@Category(ModuleTestCategory.class)
public class AdeManagerSdkTest {
    @Autowired
    private AdeManagerSdk adeManagerSdk;
    @Autowired
    private SystemDateService systemDateService;
    @Autowired
    private MockedEnrichedRecordGenerator dataGenerator;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private EnrichedDataAdeToCollectionNameTranslator translator;
    @Autowired
    private ScoredEnrichedFileGenerator scoredEnrichedFileGenerator;

    @Test
    public void testScoredEnrichedQueries() throws GeneratorException {
        List<AdeScoredFileRecord> generatedScoredRecords = scoredEnrichedFileGenerator.generateAndPersistSanityData(30);
        String adeEventType = generatedScoredRecords.get(0).getAdeEventType();
        List<String> eventIds = generatedScoredRecords.stream().map(x -> x.getContext().getEventId()).collect(Collectors.toList());
        List<AdeScoredEnrichedRecord> retrievedScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventIds, adeEventType, GENERATED_SCORE-0.01);
        Assert.assertEquals(generatedScoredRecords.size(), retrievedScoredEnrichedRecords.size());
        Instant startInstant = retrievedScoredEnrichedRecords.stream().min(Comparator.comparing(AdeRecord::getStartInstant)).get().getStartInstant();
        Instant endInstant = retrievedScoredEnrichedRecords.stream().max(Comparator.comparing(AdeRecord::getStartInstant)).get().getStartInstant();
        TimeRange timeRange = new TimeRange(startInstant, endInstant);
        Pair<String, String> contextFieldAndValue = Pair.of("userId", GENERATED_USER);
        List<String> distinctOperationTypes = adeManagerSdk.findScoredEnrichedRecordsDistinctFeatureValues(adeEventType, contextFieldAndValue, timeRange, "operationType", GENERATED_SCORE-0.1);
        Assert.assertTrue(distinctOperationTypes.size()>=1);
    }

    @Test
    public void shouldGetScoreAggregationNameToAdeEventTypeMap() {
        Map<String, List<String>> scoreAggregationNameToAdeEventTypeMap = adeManagerSdk.getAggregationNameToAdeEventTypeMap();
        Assert.assertNotNull(scoreAggregationNameToAdeEventTypeMap);
        Assert.assertTrue(scoreAggregationNameToAdeEventTypeMap.size()>0);
        scoreAggregationNameToAdeEventTypeMap.values().forEach(value -> Assert.assertTrue(value.size()>=1));
    }

    @Test
    public void shouldInsertDataAndCreateIndexes() {
        Instant startInstant = systemDateService.getInstant();
        Instant endInstant = systemDateService.getInstant().plus(1, ChronoUnit.HOURS);
        EnrichedRecordsMetadata metaData = new EnrichedRecordsMetadata("testDataSource", startInstant, endInstant);
        List<MockedEnrichedRecord> generate = dataGenerator.generate(metaData);
        adeManagerSdk.storeEnrichedRecords(metaData, generate);
        String collectionName = translator.toCollectionName(metaData);
        List<MockedEnrichedRecord> insertedRecords = mongoTemplate.findAll(MockedEnrichedRecord.class, collectionName);
        Assert.assertTrue("ADE input records are missing.", insertedRecords.size() > 0);
        List<IndexInfo> indexInfo = mongoTemplate.indexOps(collectionName).getIndexInfo();
        Assert.assertEquals("Unexpected number of indexes.", 2, indexInfo.size());
        Assert.assertEquals("Unexpected index name.",
                new HashSet<>(Arrays.asList("_id_", "start")),
                indexInfo.stream().map(IndexInfo::getName).collect(Collectors.toSet()));
    }

    /**
     * Test cleanup of enriched data
     */
    @Test
    public void cleanupEnrichedData() {
        Instant startInstant = systemDateService.getInstant();
        Instant endInstant = systemDateService.getInstant().plus(4, ChronoUnit.HOURS);
        EnrichedRecordsMetadata metaData = new EnrichedRecordsMetadata("testDataSource", startInstant, endInstant);
        List<MockedEnrichedRecord> generate = dataGenerator.generate(metaData);
        adeManagerSdk.storeEnrichedRecords(metaData, generate);

        Instant removeFrom = startInstant.plus(Duration.ofHours(1));
        Instant removeTo = removeFrom.plus(Duration.ofHours(1));
        adeManagerSdk.cleanupEnrichedData(new TimeRange(removeFrom, removeTo));


        String collectionName = translator.toCollectionName(metaData);
        List<MockedEnrichedRecord> insertedRecords = mongoTemplate.findAll(MockedEnrichedRecord.class, collectionName);

        insertedRecords.forEach(insertedRecord -> {

            Assert.assertTrue(insertedRecord.getStartInstant().isAfter(removeTo) ||
                    insertedRecord.getStartInstant().equals(removeTo) ||
                    insertedRecord.getStartInstant().isBefore(removeFrom));

        });
    }

    @Configuration
    @Import({
            MongodbTestConfig.class,
            AdeManagerSdkConfig.class,
            SystemDateServiceImplForcedConfig.class,
            MockedEnrichedRecordGeneratorConfig.class,
            ScoredEnrichedFileGeneratorConfig.class,
    })
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer AdeManagerSdkTestPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("spring.application.name", "test-app-name");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
