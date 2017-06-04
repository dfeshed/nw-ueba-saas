package presidio.ade.sdk.executions.online;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import fortscale.utils.mongodb.index.DynamicIndexApplicationListenerConfig;
import fortscale.utils.spring.TestPropertiesPlaceholderConfigurer;
import fortscale.utils.test.mongodb.MongodbTestConfig;
import fortscale.utils.time.SystemDateService;
import fortscale.utils.time.impl.config.SystemDateServiceImplForcedConfig;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import presidio.ade.domain.store.input.ADEInputRecordsMetaData;
import presidio.ade.domain.store.translators.ADEInputDataToCollectionNameTranslator;
import presidio.ade.sdk.executions.data.generator.ADEMockedInputRecordGenerator;
import presidio.ade.sdk.executions.data.generator.ADEMockedInputRecordGeneratorConfig;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

/**
 * Created by barak_schuster on 5/21/17.
 */
@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ADEOnlineSDKTest {
    @Autowired
    private ADEOnlineSDK adeOnlineSDK;
    @Autowired
    private SystemDateService systemDateService;
    @Autowired
    private ADEMockedInputRecordGenerator dataGenerator;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ADEInputDataToCollectionNameTranslator translator;

    @Test
    public void shouldInsertDataAndCreateIndexes() {
        adeOnlineSDK.getRunId();
        Instant startInstant = systemDateService.getInstant();
        Instant endInstant = systemDateService.getInstant().plus(1, ChronoUnit.HOURS);
        ADEInputRecordsMetaData metaData = new ADEInputRecordsMetaData("testDataSource", Duration.ofHours(1), startInstant, endInstant);
        List<ADEMockedInputRecord> generate = dataGenerator.generate(metaData);
        adeOnlineSDK.store(metaData, generate);
        String collectionName = translator.toCollectionName(metaData);
        List<ADEMockedInputRecord> insertedRecords = mongoTemplate.findAll(ADEMockedInputRecord.class, collectionName);

        Assert.assertTrue("ade input records exists", insertedRecords.size() > 0);
        DBCollection collection = mongoTemplate.getCollection(collectionName);
        List<DBObject> indexInfo = collection.getIndexInfo();
        // 1 index is always created for _id_ field. because of that reason we need to check that are at least 2
        Assert.assertTrue("more than one index created", indexInfo.size() >= 2);
    }

    @Configuration
    @Import({
            MongodbTestConfig.class,
            ADEOnlineSDKConfig.class,
            SystemDateServiceImplForcedConfig.class,
            ADEMockedInputRecordGeneratorConfig.class
    })
    public static class springConfig {
        @Bean
        public static TestPropertiesPlaceholderConfigurer ADEOnlineSDKTestPropertiesConfigurer() {
            Properties properties = new Properties();
            return new TestPropertiesPlaceholderConfigurer(properties);
        }
    }
}