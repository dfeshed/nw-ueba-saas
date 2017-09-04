package presidio.ade.sdk.common;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.SystemDateService;
import fortscale.utils.time.TimeRange;
import fortscale.utils.time.impl.config.SystemDateServiceImplForcedConfig;
import fortscale.utils.ttl.TtlServiceConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
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

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
        List<AdeScoredEnrichedRecord> retrievedScoredEnrichedRecords = adeManagerSdk.findScoredEnrichedRecords(eventIds, adeEventType, GENERATED_SCORE);
        Assert.assertEquals(generatedScoredRecords.size(),retrievedScoredEnrichedRecords.size());
        Instant startInstant = retrievedScoredEnrichedRecords.stream().min(Comparator.comparing(AdeRecord::getStartInstant)).get().getStartInstant();
        Instant endInstant = retrievedScoredEnrichedRecords.stream().max(Comparator.comparing(AdeRecord::getStartInstant)).get().getStartInstant();
        TimeRange timeRange = new TimeRange(startInstant, endInstant);
        Pair<String, String> contextFieldAndValue = Pair.of("userId", GENERATED_USER);
        List<String> distinctOperationTypes = adeManagerSdk.findScoredEnrichedRecordsDistinctFeatureValues(adeEventType, contextFieldAndValue, timeRange, "operationType", GENERATED_SCORE);
        Assert.assertTrue(distinctOperationTypes.size()>=1);
    }

    @Test
    public void shouldGetScoreAggregationNameToAdeEventTypeMap()
    {
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
        Assert.assertTrue("ade input records exists", insertedRecords.size() > 0);
        DBCollection collection = mongoTemplate.getCollection(collectionName);
        List<DBObject> indexInfo = collection.getIndexInfo();
        // 1 index is always created for _id_ field. because of that reason we need to check that are at least 2
        Assert.assertTrue("more than one index created", indexInfo.size() >= 2);
    }

    @Configuration
    @Import({
            MongodbTestConfig.class,
            AdeManagerSdkConfig.class,
            SystemDateServiceImplForcedConfig.class,
            MockedEnrichedRecordGeneratorConfig.class,
            ScoredEnrichedFileGeneratorConfig.class,
            TtlServiceConfig.class
    })
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer AdeManagerSdkTestPropertiesConfigurer() {
            Properties properties = new Properties();
            properties.put("streaming.event.field.type.aggr_event", "aggr_event");
            properties.put("streaming.aggr_event.field.context", "context");
            properties.put("presidio.application.name", "test-app-name");
            properties.put("presidio.default.ttl.duration", "PT48H");
            properties.put("presidio.default.cleanup.interval", "PT24H");
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}
