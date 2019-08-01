package com.rsa.netwitness.presidio.automation.test.mapping;

import com.rsa.netwitness.presidio.automation.domain.activedirectory.ActiveDirectoryEnrichStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.ActiveDirectoryEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.domain.repository.FileEnrichStoredDataRepository;
import com.rsa.netwitness.presidio.automation.mapping.operation_type.OperationTypeToCategories;
import org.assertj.core.api.SoftAssertions;
import org.assertj.core.util.Lists;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.rsa.netwitness.presidio.automation.mapping.operation_type.ActiveDirectoryOperationTypeMapping.getInstance;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * This is test of processing from "input_<schema>_raw" to "enriched_<schema>" collections.
 * Verifying that operation type category assigned correctly for relevant operations.
 * <p>
 * This test is relevant (categories exist) only for Active Directory and File schema.
 **/

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class})
public class OperationTypeMappingTest extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(OperationTypeMappingTest.class.getName());

    @Autowired
    private ActiveDirectoryEnrichStoredDataRepository enrichActiveDirectoryRepo;
    @Autowired
    private FileEnrichStoredDataRepository enrichFileRepo;

    private Map<String, List<String>> operationTypeToCategoriesActiveDir = getInstance().getOperationTypeToCategoryMap();
    private Map<String, Integer> operationTypeToEventCodeMap = getInstance().getOperationTypeToEventCodeMap();
    // OperationTypeToCategories.getInstance().getForActiveDirectory();

    private Map<String, List<String>> operationTypeToCategoriesFile =
            OperationTypeToCategories.getInstance().getForFile();

    private SoftAssertions softly = new SoftAssertions();

    private List<String> activeDirOpTypeExcludeList = Lists.newArrayList(
            "SECURITY_ENABLED_GLOBAL_GROUP_CHANGED",
            "SECURITY_ENABLED_UNIVERSAL_GROUP_CHANGED");

    private List<String> fileOpTypeList = Lists.newArrayList(
            //"LOCAL_SHARE_REMOVED"
            "FILE_DELETED",
            "FILE_OPENED",
            "FILE_PERMISSION_CHANGED",
            "FILE_DELETED");

    private Map<String, List<String>> expectedOpTypeCategories = operationTypeToCategoriesActiveDir.entrySet()
            .parallelStream()
            .filter(e -> !activeDirOpTypeExcludeList.contains(e.getKey()))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

    private Map<String, Integer> expectedOpTypeEventCode = operationTypeToEventCodeMap.entrySet()
            .parallelStream()
            .filter(e -> !activeDirOpTypeExcludeList.contains(e.getKey()))
            .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));


    @Test
    public void active_directory_operation_type_should_contain_defined_categories() {
        for (Map.Entry<String, List<String>> element : expectedOpTypeCategories.entrySet()) {
            List<ActiveDirectoryEnrichStoredData> actualEvents =
                    enrichActiveDirectoryRepo.findByOperationType(element.getKey());

            softly.assertThat(actualEvents)
                    .withFailMessage(element.getKey() + " operation type is missing from enriched_active_directory")
                    .isNotEmpty();

            if (actualEvents.size() > 0) {
                Function<ActiveDirectoryEnrichStoredData, Stream<String>> toOperationTypeCategories =
                        event -> Arrays.stream(event.getOperationTypeCategories().toArray()).map(Object::toString);

                List<String> distinctOperationTypeCategories = actualEvents.parallelStream()
                        .flatMap(toOperationTypeCategories)
                        .distinct()
                        .collect(toList());

                softly.assertThat(distinctOperationTypeCategories)
                        .withFailMessage("OperationType = " + element.getKey())
                        .containsAll(element.getValue());

                LOGGER.debug(element.getKey());
            }
        }
        softly.assertAll();
    }


    @Test
    public void active_directory_operation_type_should_be_mapped_to_defined_event_code() {
        for (Map.Entry<String, Integer> element : expectedOpTypeEventCode.entrySet()) {
            List<ActiveDirectoryEnrichStoredData> actualEvents =
                    enrichActiveDirectoryRepo.findByOperationType(element.getKey());

            softly.assertThat(actualEvents)
                    .withFailMessage(element.getKey() + " operation type is missing from enriched_active_directory")
                    .isNotEmpty();

            if (actualEvents.size() > 0) {
                Function<ActiveDirectoryEnrichStoredData, Integer> toEventCode =
                        event -> Integer.parseInt(event.getDataSource());

                List<Integer> distinctEventCode = actualEvents.parallelStream()
                        .map(toEventCode)
                        .distinct()
                        .collect(toList());

                softly.assertThat(distinctEventCode)
                        .withFailMessage("OperationType = " + element.getKey())
                        .containsExactly(element.getValue());

                LOGGER.debug(element.getKey());
            }
        }
        softly.assertAll();
    }


    @Test
    public void file_operation_type_should_contain_defined_categories() {
        for (String opType : fileOpTypeList) {
            List<ActiveDirectoryEnrichStoredData> actualEvents =
                    enrichFileRepo.findByOperationType(opType);

            softly.assertThat(actualEvents)
                    .withFailMessage(opType + " operation type is missing from enriched_file")
                    .isNotEmpty();

            if (actualEvents.size() > 0) {
                Function<ActiveDirectoryEnrichStoredData, Stream<String>> toOperationTypeCategories =
                        event -> Arrays.stream(event.getOperationTypeCategories().toArray()).map(Object::toString);

                List<String> distinctOperationTypeCategories = actualEvents.parallelStream()
                        .flatMap(toOperationTypeCategories)
                        .distinct()
                        .collect(toList());

                softly.assertThat(distinctOperationTypeCategories)
                        .containsExactlyInAnyOrderElementsOf(operationTypeToCategoriesFile.get(opType));

                LOGGER.debug(opType);
            }
        }
        softly.assertAll();
    }


}

