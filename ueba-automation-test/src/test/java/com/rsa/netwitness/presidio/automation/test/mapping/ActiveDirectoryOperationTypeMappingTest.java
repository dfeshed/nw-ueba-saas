package com.rsa.netwitness.presidio.automation.test.mapping;


import com.rsa.netwitness.presidio.automation.domain.activedirectory.ActiveDirectoryEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.ActiveDirectoryEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.mapping.operation_type.ActiveDirectoryOperationTypeMapping;
import com.rsa.netwitness.presidio.automation.mapping.operation_type.OperationTypeToCategories;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.util.Lists;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Instant;
import java.util.*;

import static com.rsa.netwitness.presidio.automation.utils.common.LambdaUtils.not;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class ActiveDirectoryOperationTypeMappingTest extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ActiveDirectoryOperationTypeMappingTest.class.getName());

    @Autowired
    private ActiveDirectoryEnrichStoredDataRepository enrichRepo;
    private SoftAssertions softly = new SoftAssertions();

    private List<Integer> eventCodeExcludedFromTest = Lists.newArrayList(4755, 4737);

    // Legacy operation types. not relevant for RSA
    private List<String> operationTypesExcludedFromTest = Lists.newArrayList(
            "SECURITY_ENABLED_GLOBAL_GROUP_CHANGED",
            "SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED",
            "USER_PASSWORD_NEVER_EXPIRES_OPTION_CHANGED",
            "USER_DO_NOT_REQUIRE_KERBEROS_PREAUTHENTICATION_OPTION_CHANGED",
            "USER_ACCOUNT_IS_SENSITIVE_AND_CANNOT_BE_DELEGATED_OPTION_CHANGED",
            "USER_ACCOUNT_TYPE_CHANGED",
            "USER_ACCOUNT_RE_ENABLED",
            "USER_ACCOUNT_IS_TRUSTED_FOR_DELEGATION_OPTION_CHANGED");


    private Map<String, List<String>> expectedOperationTypeToCategoriesMap;
    private Map<Integer, String> expectedEventCodeToOperationTypeMap;
    private List<ActiveDirectoryEnrichStoredData> actualEvents;


    @Parameters({"historical_days_back"})
    @BeforeClass
    public void init(@Optional("30") int historicalDaysBack) {
        expectedOperationTypeToCategoriesMap = getFromConfigurationFilesAndRemoveExcluded(operationTypesExcludedFromTest);
        expectedEventCodeToOperationTypeMap = getFromFlumeConfigurationAndRemoveExcluded(eventCodeExcludedFromTest);
        actualEvents = getFromMongoEnrichedTable(historicalDaysBack);
    }

    @Test
    public void expected_operation_types_should_exactly_match_the_active_directory_enrich() {
        List<String> actualOperationTypes = actualEvents.parallelStream().map(e -> e.getOperationType()).distinct().collect(toList());
        List<String> expectedOperationTypes = Lists.newArrayList(expectedOperationTypeToCategoriesMap.keySet());
        assertThat(actualOperationTypes).containsExactlyInAnyOrderElementsOf(expectedOperationTypes);
    }

    @Test
    public void expected_event_codes_should_exactly_match_the_active_directory_enrich() {
        List<String> actualEventCodes = actualEvents.parallelStream().map(e -> e.getDataSource()).distinct().collect(toList());
        List<String> expectedEventCodes = expectedEventCodeToOperationTypeMap.keySet().stream().distinct().map(String::valueOf).collect(toList());
        assertThat(actualEventCodes).containsExactlyInAnyOrderElementsOf(expectedEventCodes);
    }


    @Test
    public void operation_type_should_match_the_event_code() {

        Map<String, Set<String>> actualEvCodeToOpTypesResult = actualEvents.parallelStream()
                .collect(groupingBy(ActiveDirectoryEnrichStoredData::getDataSource,
                        mapping(ActiveDirectoryEnrichStoredData::getOperationType, toSet())));

        for (Map.Entry<String, Set<String>> actualEvCodeToOpTypes : actualEvCodeToOpTypesResult.entrySet()) {

            String expectedOpType = expectedEventCodeToOperationTypeMap
                    .getOrDefault(Integer.valueOf(actualEvCodeToOpTypes.getKey()),
                            "Operation type mapping is missing for id = " + actualEvCodeToOpTypes.getKey());

            softly.assertThat(actualEvCodeToOpTypes.getValue())
                    .as("Wrong operation type mapping for eventCode=" + actualEvCodeToOpTypes.getKey())
                    .containsExactly(expectedOpType);
        }

        softly.assertAll();
    }

    @Test
    public void operation_type_categories_should_match_the_operation_type() {

        Map<String, Set<List>> actualOpTypesToOpTypeCategories = actualEvents.parallelStream()
                .collect(groupingBy(ActiveDirectoryEnrichStoredData::getOperationType,
                        mapping(ActiveDirectoryEnrichStoredData::getOperationTypeCategories, toSet())));


        for (Map.Entry<String, Set<List>> actual : actualOpTypesToOpTypeCategories.entrySet()) {

            List<Object> actualDistinctCategories = actual.getValue().stream()
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(toList());

            List<String> expectedCategories = new ArrayList<>(expectedOperationTypeToCategoriesMap.getOrDefault(actual.getKey(),
                    Lists.emptyList()));

            softly.assertThat(expectedCategories)
                    .withFailMessage(actual.getKey() + " operation type is missing from the configuration")
                    .isNotEmpty();

            softly.assertThat(actualDistinctCategories)
                    .as("Wrong operation type mapping for " + actual.getKey())
                    .containsAll(expectedCategories);
        }

        softly.assertAll();
    }


    private Map<Integer, String> getFromFlumeConfigurationAndRemoveExcluded(List<Integer> eventCodeExcludedFromTest) {
        return ActiveDirectoryOperationTypeMapping.getInstance().getEventCodeMapToOperationTypeMap()
                .entrySet().stream().filter(not(e -> eventCodeExcludedFromTest.contains(e.getKey())))
                .collect(toMap(e -> e.getKey(), e -> e.getValue()));
    }

    private Map<String, List<String>> getFromConfigurationFilesAndRemoveExcluded(List<String> operationTypesExcludedFromTest) {
        Map<String, List<String>> flumeJsonList = ActiveDirectoryOperationTypeMapping.getInstance()
                .getOperationTypeToCategoryMap();

        Map<String, List<String>> operationTypeCategoryMappingJsonList = OperationTypeToCategories.getInstance().getForActiveDirectory();

        flumeJsonList.putAll(operationTypeCategoryMappingJsonList);

        return flumeJsonList.entrySet()
                .parallelStream()
                .filter(not(e -> operationTypesExcludedFromTest.contains(e.getKey())))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<ActiveDirectoryEnrichStoredData> getFromMongoEnrichedTable(int historicalDaysBack) {
        Instant endTime = Instant.now().truncatedTo(DAYS);
        Instant startTime = endTime.minus(historicalDaysBack, DAYS);
        List<ActiveDirectoryEnrichStoredData> actualEvents = enrichRepo.findByTime(startTime, endTime);
        assertThat(actualEvents)
                .withFailMessage("Enriched table data is missing for the period: startTime=" + startTime + " endTime=" + endTime)
                .isNotNull().isNotEmpty();

        return actualEvents;
    }

}
