package com.rsa.netwitness.presidio.automation.test.mapping;


import com.rsa.netwitness.presidio.automation.domain.activedirectory.AdapterActiveDirectoryStoredData;
import com.rsa.netwitness.presidio.automation.domain.authentication.AdapterAuthenticationStoredData;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.file.AdapterFileStoredData;
import com.rsa.netwitness.presidio.automation.domain.process.AdapterProcessStoredData;
import com.rsa.netwitness.presidio.automation.domain.process.AdapterRegistryStoredData;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.domain.tls.AdapterTlsStoredData;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import fortscale.domain.core.entityattributes.EntityAttributes;
import org.assertj.core.api.Condition;
import org.assertj.core.api.SoftAssertions;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.filter.NotFilter.not;
import static org.assertj.core.util.Lists.list;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class InputRawEventsTest extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(InputRawEventsTest.class.getName());

    @Autowired
    private AdapterActiveDirectoryStoredDataRepository activeDirectoryRepository;
    @Autowired
    private AdapterAuthenticationStoredDataRepository authenticationRepository;
    @Autowired
    private AdapterFileStoredDataRepository fileRepository;
    @Autowired
    private AdapterProcessStoredDataRepository processRepository;
    @Autowired
    private AdapterRegistryStoredDataRepository registryRepository;
    @Autowired
    private AdapterTlsStoredDataRepository tlsRepository;

    private Instant startDate = Instant.now();
    private Instant endDate = Instant.now();

    @BeforeClass
    public void setup() {
        endDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
        startDate = endDate.minus(7, ChronoUnit.DAYS);
        LOGGER.info("startDate=" + startDate + " endDate=" + endDate);
    }


    @Test
    public void missingValuesTlsTest() {
        List<AdapterTlsStoredData> input = tlsRepository.findByTime(startDate, endDate);
        List<Field> classFields = list(AdapterTlsStoredData.class.getDeclaredFields());
        new Validate(input, classFields).allFieldsHaveAtLeastOneNotNullValue();
    }

    @Test
    public void missingValuesActiveDirectoryTest() {
        List<String> exclusions = list("additionalInfo");
        List<AdapterActiveDirectoryStoredData> input = activeDirectoryRepository.findByTime(startDate, endDate);
        List<Field> classFields = Arrays.stream(AdapterActiveDirectoryStoredData.class.getDeclaredFields())
                .filter(field -> !exclusions.contains(field.getName())).collect(toList());
        new Validate(input, classFields).allFieldsHaveAtLeastOneNotNullValue();
    }

    @Test
    public void missingValuesAuthenticationTest() {
        List<String> exclusions = list("additionalInfo", "dstMachineDomain", "site", "country", "city");
        List<AdapterAuthenticationStoredData> input = authenticationRepository.findByTime(startDate, endDate);
        List<Field> classFields = Arrays.stream(AdapterAuthenticationStoredData.class.getDeclaredFields())
                .filter(field -> !exclusions.contains(field.getName())).collect(toList());
        new Validate(input, classFields).allFieldsHaveAtLeastOneNotNullValue();
    }

    @Test
    public void missingValuesFileTest() {
        List<String> exclusions = list("additionalInfo", "dstFilePath", "srcMachineName", "dstMachineName",
                "srcMachineId", "dstMachineId", "isDstDriveShared");
        List<AdapterFileStoredData> input = fileRepository.findByTime(startDate, endDate);
        List<Field> classFields = Arrays.stream(AdapterFileStoredData.class.getDeclaredFields())
                .filter(field -> !exclusions.contains(field.getName())).collect(toList());
        new Validate(input, classFields).allFieldsHaveAtLeastOneNotNullValue();
    }

    @Test
    public void missingValuesProcessTest() {
        List<String> exclusions = list("additionalInfo");
        List<AdapterProcessStoredData> input = processRepository.findByTime(startDate, endDate);
        List<Field> classFields = Arrays.stream(AdapterProcessStoredData.class.getDeclaredFields())
                .filter(field -> !exclusions.contains(field.getName())).collect(toList());
        new Validate(input, classFields).allFieldsHaveAtLeastOneNotNullValue();
    }

    @Test
    public void missingValuesRegistryTest() {
        List<String> exclusions = list("additionalInfo", "srcFilePath", "dstFilePath", "result",
                "srcMachineName", "srcMachineId", "dstMachineName", "dstMachineId", "isSrcDriveShared", "isDstDriveShared");
        List<AdapterRegistryStoredData> input = registryRepository.findByTime(startDate, endDate);
        List<Field> classFields = Arrays.stream(AdapterRegistryStoredData.class.getDeclaredFields())
                .filter(field -> !exclusions.contains(field.getName())).collect(toList());
        new Validate(input, classFields).allFieldsHaveAtLeastOneNotNullValue();
    }


    private Condition<Object> notNull = new Condition<Object>() {
        public boolean matches(Object value) {
            return value != null;
        }
    };

    private class Validate {
        private SoftAssertions softly = new SoftAssertions();
        private final List<?> storedData;
        private final List<Field> classFields;

        private Validate(List<?> storedData, List<Field> classFields ) {
            this.storedData = storedData;
            this.classFields = classFields;
        }

        private void allFieldsHaveAtLeastOneNotNullValue() {
            for (Field field : classFields) {
                String fieldName = field.getName();
                field.setAccessible(true);
                LOGGER.info(fieldName);

                if (field.getType().isArray()
                        || Iterable.class.isAssignableFrom(field.getType())) {

                    softly.assertThat(storedData).as(fieldName)
                            .filteredOn(fieldName, not(null))
                            .flatExtracting(fieldName)
                            .isNotNull().isNotEmpty()
                            .areAtLeastOne(notNull);

                } else if (field.getType().isPrimitive()
                        || Boolean.class.isAssignableFrom(field.getType())
                        || String.class.isAssignableFrom(field.getType())
                        || Instant.class.isAssignableFrom(field.getType())) {

                    softly.assertThat(storedData).as(fieldName)
                            .filteredOn(fieldName, not(null))
                            .extracting(fieldName)
                            .isNotNull().isNotEmpty()
                            .areAtLeastOne(notNull);

                } else if (EntityAttributes.class.isAssignableFrom(field.getType())) {
                    String name = fieldName + ".name";
                    softly.assertThat(storedData).as(fieldName)
                            .filteredOn(fieldName, not(null))
                            .extracting(name)
                            .isNotNull().isNotEmpty()
                            .areAtLeastOne(notNull);

                } else {
                    softly.fail("Field name not found " + fieldName);
                }
            }
            softly.assertAll();
        }


    }



}
