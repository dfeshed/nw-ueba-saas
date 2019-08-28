package com.rsa.netwitness.presidio.automation.test.enrichment;


import com.rsa.netwitness.presidio.automation.domain.authentication.EnrichedAuthenticationStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.AuthenticationEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.mapping.operation_type.OperationTypeToCategories;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.assertj.core.api.SoftAssertions;
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
import java.util.stream.Stream;

import static java.time.temporal.ChronoUnit.DAYS;
import static java.util.stream.Collectors.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Lists.newArrayList;
import static org.assertj.core.util.Maps.newHashMap;


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class AuthenticationOperationTypeAndEventCodeTest extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(AuthenticationOperationTypeAndEventCodeTest.class.getName());

    @Autowired
    private AuthenticationEnrichStoredDataRepository enrichRepo;
    private SoftAssertions softly = new SoftAssertions();

    private Map<String, ArrayList<String>> expectedEventCodeToOperationTypes = Stream.of(
            // todo: ask about:
            // newHashMap("4776", newArrayList("CREDENTIAL_VALIDATION")),
            newHashMap("rsaacesrv", newArrayList("MFA")),
            newHashMap("4648", newArrayList("EXPLICIT_CREDENTIALS_LOGON")),
            newHashMap("4769", newArrayList("CREDENTIAL_VALIDATION")),
            newHashMap("4624", newArrayList("INTERACTIVE", "REMOTE_INTERACTIVE")),
            newHashMap("4625", newArrayList("INTERACTIVE", "NETWORK", "REMOTE_INTERACTIVE"))
    ).flatMap(e -> e.entrySet().stream()).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));


    private List<String> expectedOperationTypes = expectedEventCodeToOperationTypes.values()
            .stream().flatMap(Collection::stream).distinct().collect(toList());
    private List<EnrichedAuthenticationStoredData> actualEvents;


    @Parameters({"historical_days_back"})
    @BeforeClass
    public void init(@Optional("30") int historicalDaysBack) {
        actualEvents = getFromMongoEnrichedTable(historicalDaysBack);
    }

    @Test
    public void authentication_enrich_should_contain_all_expected_operation_types() {
        List<String> actualOperationTypes = actualEvents.parallelStream()
                .map(EnrichedAuthenticationStoredData::getOperationType).distinct().collect(toList());
        assertThat(actualOperationTypes).containsExactlyInAnyOrderElementsOf(expectedOperationTypes);
    }

    @Test
    public void authentication_event_codes_should_exactly_match_the_file_enrich() {
        List<String> actualEventCodes = actualEvents.parallelStream().map(EnrichedAuthenticationStoredData::getDataSource).distinct().collect(toList());
        List<String> expectedEventCodes = expectedEventCodeToOperationTypes.keySet().stream().distinct().map(String::valueOf).collect(toList());
        assertThat(actualEventCodes).containsExactlyInAnyOrderElementsOf(expectedEventCodes);
    }


    @Test
    public void operation_type_should_match_the_event_code() {

        Map<String, Set<String>> actualEvCodeToOpTypesResult = actualEvents.parallelStream()
                .collect(groupingBy(EnrichedAuthenticationStoredData::getDataSource,
                        mapping(EnrichedAuthenticationStoredData::getOperationType, toSet())));

        for (Map.Entry<String, Set<String>> actualEvCodeToOpTypes : actualEvCodeToOpTypesResult.entrySet()) {

            List<String> expectedOpType = expectedEventCodeToOperationTypes
                    .getOrDefault(actualEvCodeToOpTypes.getKey(),
                            newArrayList("Operation type mapping is missing for id = " + actualEvCodeToOpTypes.getKey()));

            softly.assertThat(actualEvCodeToOpTypes.getValue())
                    .as("Wrong operation type mapping for eventCode=" + actualEvCodeToOpTypes.getKey())
                    .containsExactlyInAnyOrderElementsOf(expectedOpType);
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

    private List<EnrichedAuthenticationStoredData> getFromMongoEnrichedTable(int historicalDaysBack) {
        Instant endTime = Instant.now().truncatedTo(DAYS);
        Instant startTime = endTime.minus(historicalDaysBack, DAYS);
        List<EnrichedAuthenticationStoredData> actualEvents = enrichRepo.findByTime(startTime, endTime);
        assertThat(actualEvents)
                .withFailMessage("Enriched table data is missing for the period: startTime=" + startTime + " endTime=" + endTime)
                .isNotNull().isNotEmpty();

        return actualEvents;
    }

}
