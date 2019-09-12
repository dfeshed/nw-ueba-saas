package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.SmartJa3Hourly;
import com.rsa.netwitness.presidio.automation.domain.output.SmartSslSubjectHourly;
import com.rsa.netwitness.presidio.automation.domain.output.SmartUserIdStoredRecored;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.mongo.SmartHourlyEntitiesHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.PresidioUrl;
import com.rsa.netwitness.presidio.automation.test_managers.OutputTestManager;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.lang.String.join;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.list;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {OutputTestManager.class, MongoConfig.class})
public class E2EMongoRestValidation extends AbstractTestNGSpringContextTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private OutputTestManager testManager;

    @Autowired
    private SmartUserIdHourlyRepository smartUserIdHourlyRepository;

    @Autowired
    private SmartJa3HourlyRepository smartJa3HourlyRepository;

    @Autowired
    private SmartSslSubjectHourlyRepository smartSslSubjectHourlyRepository;

    @Autowired
    private OutputActiveDirectoryStoredDataRepository adRepository;

    @Autowired
    private OutputAuthenticationStoredDataRepository authRepository;

    @Autowired
    private OutputFileStoredDataRepository fileRepository;
    @Autowired
    private OutputProcessStoredDataRepository processRepository;
    @Autowired
    private OutputRegistryStoredDataRepository registryRepository;

    private String testName;
    private SoftAssertions softly = new SoftAssertions();

    private int outputProcessingEndDaysBack;
    private int outputProcessingStartDaysBack;
    private int historicalDaysBack;

    private RestHelper restHelper = new RestHelper();
    private PresidioUrl allEntitiesUrl = restHelper.entities().url().withMaxSizeParameters();
    private List<EntitiesStoredRecord> allRestEntities;

    @Parameters({"outputProcessingStartDaysBack", "outputProcessingEndDaysBack", "historical_days_back"})
    @BeforeClass
    public void setup(@Optional("30") int outputProcessingStartDaysBack,
                      @Optional("1") int outputProcessingEndDaysBack,
                      @Optional("14") int historicalDaysBack) {

        this.outputProcessingStartDaysBack = outputProcessingStartDaysBack;
        this.outputProcessingEndDaysBack = outputProcessingEndDaysBack;
        this.historicalDaysBack = historicalDaysBack;
        this.allRestEntities = restHelper.entities().request().getEntities(allEntitiesUrl);
        assertThat(allRestEntities).withFailMessage(allEntitiesUrl + "\nIs null or empty").isNotNull().isNotEmpty();
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }


    private String errorMessageGen(String label, Set<String> gap, Instant anomalyDateEnd) {
        return allEntitiesUrl
                + "\nUrl result compared to Mongo query limited by startInstant < " + anomalyDateEnd
                + "\nError message: " + gap.size() + " " + label + " Mongo entities are missing from REST result."
                + "\nSubset of missing elements:\n "
                + join("\n", gap.stream().limit(10).collect(toSet()));
    }

    private Function<String, Set<String>> getRestEntitiesByType = type ->
            allRestEntities.parallelStream()
                    .filter(e -> e.getEntityType().equals(type))
                    .map(EntitiesStoredRecord::getEntityId)
                    .collect(toSet());

    @Test
    public void all_rest_response_entities_must_be_unique_by_entity_type(){
        List<String> entityTypes = list("userId", "ja3", "sslSubject");
        Map<String, List<EntitiesStoredRecord>> actual =
                allRestEntities.parallelStream().collect(groupingBy(EntitiesStoredRecord::getEntityType));

        entityTypes.forEach(entityType ->
                softly.assertThat(actual.get(entityType)).as(entityType + " entity type")
                    .extracting("entityId").doesNotHaveDuplicates());
        softly.assertAll();
    }

    @Test
    public void all_mongo_user_id_entities_existing_in_rest_response() {
        SmartUserIdStoredRecored entity = smartUserIdHourlyRepository.findFirstByOrderByCreatedDateAsc();
        assertThat(entity).overridingErrorMessage("smart_userId_hourly table is empty").isNotNull();
        Instant anomalyDayEndTime = entity.getCreatedDate().truncatedTo(DAYS);

        SmartHourlyEntitiesHelper mongoEntitiesHelper = new SmartHourlyEntitiesHelper(mongoTemplate);
        Set<String> actualMongoEntities = mongoEntitiesHelper.getEntitiesUserId(anomalyDayEndTime);
        assertThat(actualMongoEntities).withFailMessage("No users in smart_userId_hourly table").isNotEmpty();

        Set<String> restUniqueEntities = getRestEntitiesByType.apply("userId");
        actualMongoEntities.removeAll(restUniqueEntities);

        assertThat(actualMongoEntities)
                .overridingErrorMessage(errorMessageGen("smart_userId_hourly", actualMongoEntities, anomalyDayEndTime))
                .isEmpty();
    }

    @Test
    public void all_mongo_ja3_entities_existing_in_rest_response() {
        SmartJa3Hourly entity = smartJa3HourlyRepository.findFirstByOrderByCreatedDateAsc();
        assertThat(entity).overridingErrorMessage("smart_ja3_hourly table is empty").isNotNull();
        Instant anomalyDayEndTime = entity.getCreatedDate().truncatedTo(DAYS);

        SmartHourlyEntitiesHelper mongoEntitiesHelper = new SmartHourlyEntitiesHelper(mongoTemplate);
        Set<String> actualMongoEntities = mongoEntitiesHelper.getEntitiesJa3(anomalyDayEndTime);
        assertThat(actualMongoEntities).withFailMessage("No users in smart_ja3_hourly table").isNotEmpty();

        Set<String> restUniqueEntities = getRestEntitiesByType.apply("ja3");
        actualMongoEntities.removeAll(restUniqueEntities);

        assertThat(actualMongoEntities)
                .overridingErrorMessage(errorMessageGen("smart_ja3_hourly", actualMongoEntities, anomalyDayEndTime))
                .isEmpty();
    }

    @Test
    public void all_mongo_ssl_subject_entities_existing_in_rest_response() {
        SmartSslSubjectHourly entity = smartSslSubjectHourlyRepository.findFirstByOrderByCreatedDateAsc();
        assertThat(entity).overridingErrorMessage("smart_sslSubject_hourly table is empty").isNotNull();
        Instant anomalyDayEndTime = entity.getCreatedDate().truncatedTo(DAYS);

        SmartHourlyEntitiesHelper mongoEntitiesHelper = new SmartHourlyEntitiesHelper(mongoTemplate);
        Set<String> actualMongoEntities = mongoEntitiesHelper.getEntitiesSslSubject(anomalyDayEndTime);
        assertThat(actualMongoEntities).withFailMessage("No users in smart_sslSubject_hourly table").isNotEmpty();

        Set<String> restUniqueEntities = getRestEntitiesByType.apply("sslSubject");
        actualMongoEntities.removeAll(restUniqueEntities);

        assertThat(actualMongoEntities)
                .overridingErrorMessage(errorMessageGen("smart_sslSubject_hourly", actualMongoEntities, anomalyDayEndTime))
                .isEmpty();
    }

    @Test
    public void all_rest_entities_found_in_mongo_smart_hourly() {
        SmartHourlyEntitiesHelper entitiesHelper = new SmartHourlyEntitiesHelper(mongoTemplate);
        Set<String> mongoEntities = entitiesHelper.getAllEntities();
        Set<String> allRestEntities = getRestEntitiesByType.apply("");

        allRestEntities.removeAll(mongoEntities);
        Function<Set<String>, String> errorMessage1 = gap ->
                allEntitiesUrl + "\n # " + gap.size() + " REST  entities are missing from smart hourly tables."
                        + "\nSubset of missing elements: " + join("\n", gap.stream().limit(10).collect(toSet()));

        assertThat(allRestEntities).overridingErrorMessage(errorMessage1.apply(allRestEntities)).isEmpty();
    }
}
