package com.rsa.netwitness.presidio.automation.test.enrichment;


import com.google.common.collect.ImmutableMap;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.file.FileEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.FileEnrichStoredDataRepository;
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

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class FileOperationTypeAndEventCodeTest extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(FileOperationTypeAndEventCodeTest.class.getName());

    @Autowired
    private FileEnrichStoredDataRepository enrichRepo;

    private Map<Integer, List<String>> expectedEventCodeToOperationTypes =
        ImmutableMap.of(
                4663, Lists.newArrayList("FILE_CREATED", "FILE_OPENED", "FILE_MODIFIED",
                        "FILE_WRITE_DAC_PERMISSION_CHANGED", "FILE_WRITE_OWNER_PERMISSION_CHANGED"),
                4660, Lists.newArrayList("FILE_DELETED"),
                4670, Lists.newArrayList("FILE_PERMISSION_CHANGED"),
                5145, Lists.newArrayList("FILE_OPENED"));


    private List<String> expectedOperationTypes = expectedEventCodeToOperationTypes.values()
            .stream().flatMap(Collection::stream).distinct().collect(toList());
    private Map<String, List<String>> expectedOperationTypeToCategoriesMap;
    private List<FileEnrichStoredData> actualEvents;


    @Parameters({"historical_days_back"})
    @BeforeClass
    public void init(@Optional("30") int historicalDaysBack) {
        expectedOperationTypeToCategoriesMap = getFromConfigurationFileAndFilterIrrelevant(expectedOperationTypes);
        actualEvents = getFromMongoEnrichedTable(historicalDaysBack);
    }

    @Test
    public void file_enrich_should_contain_all_expected_operation_types() {
        List<String> actualOperationTypes = actualEvents.parallelStream()
                .map(FileEnrichStoredData::getOperationType).distinct().collect(toList());
        assertThat(actualOperationTypes).containsExactlyInAnyOrderElementsOf(expectedOperationTypes);
    }

    @Test
    public void expected_event_codes_should_exactly_match_the_file_enrich() {
        List<String> actualEventCodes = actualEvents.parallelStream().map(FileEnrichStoredData::getDataSource).distinct().collect(toList());
        List<String> expectedEventCodes = expectedEventCodeToOperationTypes.keySet().stream().distinct().map(String::valueOf).collect(toList());
        assertThat(actualEventCodes).containsExactlyInAnyOrderElementsOf(expectedEventCodes);
    }


    @Test
    public void operation_type_should_match_the_event_code() {
        SoftAssertions softly = new SoftAssertions();

        Map<String, Set<String>> actualEvCodeToOpTypesResult = actualEvents.parallelStream()
                .collect(groupingBy(FileEnrichStoredData::getDataSource,
                        mapping(FileEnrichStoredData::getOperationType, toSet())));

        for (Map.Entry<String, Set<String>> actualEvCodeToOpTypes : actualEvCodeToOpTypesResult.entrySet()) {

            List<String> expectedOpType = expectedEventCodeToOperationTypes
                    .getOrDefault(Integer.valueOf(actualEvCodeToOpTypes.getKey()),
                            Lists.newArrayList("Operation type mapping is missing for id = " + actualEvCodeToOpTypes.getKey()));

            softly.assertThat(actualEvCodeToOpTypes.getValue())
                    .as("Wrong operation type mapping for eventCode=" + actualEvCodeToOpTypes.getKey())
                    .containsExactlyInAnyOrderElementsOf(expectedOpType);
        }

        softly.assertAll();
    }

    @Test
    public void operation_type_categories_should_match_the_operation_type() {
        SoftAssertions softly = new SoftAssertions();

        Map<String, Set<String[]>> actualOpTypesToOpTypeCategories = actualEvents.parallelStream()
                .collect(groupingBy(FileEnrichStoredData::getOperationType,
                        mapping(FileEnrichStoredData::getOperationTypeCategories, toSet())));

        for (Map.Entry<String, Set<String[]>> actual : actualOpTypesToOpTypeCategories.entrySet()) {

            List<String> actualDistinctCategories = actual.getValue().stream()
                    .flatMap(Arrays::stream)
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

    private Map<String, List<String>> getFromConfigurationFileAndFilterIrrelevant(List<String> operationTypesExcludedFromTest) {
        Map<String, List<String>> flumeJsonList = OperationTypeToCategories.getInstance().getForFile();

        return flumeJsonList.entrySet()
                .parallelStream()
                .filter(e -> operationTypesExcludedFromTest.contains(e.getKey()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private List<FileEnrichStoredData> getFromMongoEnrichedTable(int historicalDaysBack) {
        Instant endTime = Instant.now().truncatedTo(DAYS);
        Instant startTime = endTime.minus(historicalDaysBack, DAYS);
        List<FileEnrichStoredData> actualEvents = enrichRepo.findByTime(startTime, endTime);
        assertThat(actualEvents)
                .withFailMessage("Enriched table data is missing for the period: startTime=" + startTime + " endTime=" + endTime)
                .isNotNull().isNotEmpty();

        return actualEvents;
    }

}
