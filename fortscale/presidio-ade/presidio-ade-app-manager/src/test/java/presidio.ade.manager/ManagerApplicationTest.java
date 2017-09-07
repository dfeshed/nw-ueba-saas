package presidio.ade.manager;

import fortscale.utils.test.category.ModuleTestCategory;
import fortscale.utils.time.TimeService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import presidio.ade.domain.record.enriched.file.EnrichedFileRecord;
import presidio.ade.test.utils.generators.EnrichedFileGeneratorConfig;
import presidio.ade.test.utils.tests.EnrichedFileSourceBaseAppTest;

import java.time.Duration;
import java.time.Instant;
import java.util.List;


@Category(ModuleTestCategory.class)
@ContextConfiguration
public class ManagerApplicationTest extends EnrichedFileSourceBaseAppTest {

    private static final Duration DURATION = Duration.ofDays(1);
    private static final Instant UNTIL_DATE = TimeService.floorTime(Instant.now().minus(Duration.ofDays(1)), DURATION);
    private static final String COLLECTION_NAME = "enriched_file";

    public static final String EXECUTION_COMMAND = String.format("cleanup --until_date %s", UNTIL_DATE.toString());

    @Autowired
    private MongoTemplate mongoTemplate;

    @Value("#{T(java.time.Duration).parse('${presidio.enriched.ttl.duration}')}")
    private Duration ttl;
    @Value("#{T(java.time.Duration).parse('${presidio.enriched.cleanup.interval}')}")
    private Duration cleanup;

    @Override
    protected String getContextTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Override
    protected String getSanityTestExecutionCommand() {
        return EXECUTION_COMMAND;
    }

    @Before
    public void beforeTest() {
        mongoTemplate.getCollectionNames().forEach(collection -> mongoTemplate.dropCollection(collection));
    }

    /**
     * 1. Generate enriched file records.
     * 2. Remove enriched file records until UNTIL_DATE according to ttl and cleanup interval
     */
    @Override
    protected void assertSanityTest() {
        List<EnrichedFileRecord> enrichedFileRecordList = mongoTemplate.findAll(EnrichedFileRecord.class, COLLECTION_NAME);

        enrichedFileRecordList.forEach(
                enrichedFile -> {
                    Assert.assertTrue(enrichedFile.getStartInstant().compareTo(UNTIL_DATE.minus(ttl)) >= 0);
                }
        );
    }


    @Configuration
    @Import({EnrichedSourceSpringConfig.class, ManagerApplicationConfigurationTest.class, ManagerApplicationCommands.class, EnrichedFileGeneratorConfig.class})
    protected static class ManagerApplicationTestConfig {

    }


}