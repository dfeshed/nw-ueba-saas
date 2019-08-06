package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.mongo.SmartHourlyEntitiesHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.rest.helper.builders.params.ParametersUrlBuilder;
import com.rsa.netwitness.presidio.automation.utils.output.OutputTestManager;
import org.assertj.core.api.SoftAssertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.*;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;

import static java.lang.String.join;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {OutputTestManager.class, MongoConfig.class})
public class E2EMongoRestValidation extends AbstractTestNGSpringContextTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private OutputTestManager testManager;

    @Autowired
    private SmartUserIdHourlyRepository smartRepository;

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
    private SmartHourlyEntitiesHelper entitiesHelper;
    private ParametersUrlBuilder allEntitiesUrl = restHelper.entities().url().withMaxSizeParameters();
    private List<EntitiesStoredRecord> restEntities;

    @Parameters({"outputProcessingStartDaysBack", "outputProcessingEndDaysBack", "historical_days_back"})
    @BeforeClass
    public void setup(@Optional("30") int outputProcessingStartDaysBack,
                      @Optional("1") int outputProcessingEndDaysBack,
                      @Optional("14") int historicalDaysBack) {

        this.outputProcessingStartDaysBack = outputProcessingStartDaysBack;
        this.outputProcessingEndDaysBack = outputProcessingEndDaysBack;
        this.historicalDaysBack = historicalDaysBack;
        this.entitiesHelper = new SmartHourlyEntitiesHelper(mongoTemplate, historicalDaysBack, 1);
        this.restEntities = restHelper.entities().request().getEntities(allEntitiesUrl);
        assertThat(restEntities).withFailMessage(allEntitiesUrl + "\nIs null or empty").isNotNull().isNotEmpty();
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }

    private BiFunction<String, Set<String>, String> errorMessage = (label, gap) ->
            allEntitiesUrl
                    + "\nMongo query: " + entitiesHelper.getQuery()
                    + "\nMessage: " + gap.size() + " " + label + " Mongo entities are missing from REST result."
                    + "\nSubset of missing elements:\n "
                    + join("\n", gap.stream().limit(10).collect(toSet()));

    private Function<String, Set<String>> getRestEntitiesByType = type ->
            restEntities.parallelStream()
                    .filter(e -> e.getEntityType().equals(type))
                    .map(EntitiesStoredRecord::getEntityId)
                    .collect(toSet());

    @Test
    public void all_mongo_user_id_entities_existing_in_rest_response() {
        Set<String> mongoEntities = entitiesHelper.getEntitiesUserId();
        assertThat(mongoEntities).withFailMessage("No users in smart_userId_hourly table").isNotEmpty();
        Set<String> restUniqueEntities = getRestEntitiesByType.apply("userId");
        mongoEntities.removeAll(restUniqueEntities);
        assertThat(mongoEntities).overridingErrorMessage(errorMessage.apply("smart_userId_hourly", mongoEntities)).isEmpty();
    }

    @Test
    public void all_mongo_ja3_entities_existing_in_rest_response() {
        Set<String> mongoEntities = entitiesHelper.getEntitiesJa3();
        assertThat(mongoEntities).withFailMessage("No users in smart_ja3_hourly table").isNotEmpty();
        Set<String> restUniqueEntities = getRestEntitiesByType.apply("ja3");
        mongoEntities.removeAll(restUniqueEntities);
        assertThat(mongoEntities).overridingErrorMessage(errorMessage.apply("smart_ja3_hourly", mongoEntities)).isEmpty();
    }

    @Test
    public void all_mongo_ssl_subject_entities_existing_in_rest_response() {
        Set<String> mongoEntities = entitiesHelper.getEntitiesSslSubject();
        assertThat(mongoEntities).withFailMessage("No users in smart_sslSubject_hourly table").isNotEmpty();
        Set<String> restUniqueEntities = getRestEntitiesByType.apply("sslSubject");
        mongoEntities.removeAll(restUniqueEntities);
        assertThat(mongoEntities).overridingErrorMessage(errorMessage.apply("smart_sslSubject_hourly", mongoEntities)).isEmpty();
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
