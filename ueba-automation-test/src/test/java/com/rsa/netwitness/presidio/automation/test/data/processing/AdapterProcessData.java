package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import com.rsa.netwitness.presidio.automation.utils.common.TitlesPrinter;
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
import java.time.temporal.ChronoUnit;

import static com.rsa.netwitness.presidio.automation.config.AutomationConf.CORE_SCHEMAS_TO_PROCESS;
import static org.assertj.core.api.Assertions.assertThat;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class AdapterProcessData extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AdapterProcessData.class.getName());
    private static TitlesPrinter ART_GEN = new TitlesPrinter();

    @Autowired
    private AdapterTestManager adapterTestManager;
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
    @Autowired
    private NetwitnessEventStore netwitnessEventStore;

    private Instant startDate = Instant.now();
    private Instant endDate = Instant.now();


    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("10") int historicalDaysBack,
                      @Optional("1") int anomalyDay) {

        TitlesPrinter.printTitle(getClass().getSimpleName());
        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack + " anomalyDay=" + anomalyDay);
        endDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
        startDate = endDate.minus(historicalDaysBack, ChronoUnit.DAYS);
        LOGGER.info("startDate=" + startDate + " endDate=" + endDate);
        LOGGER.info("CORE_SCHEMAS_TO_PROCESS = ".concat(String.join(", ", CORE_SCHEMAS_TO_PROCESS)));
    }

    @Test
    public void adapterFileTest() {
        if (CORE_SCHEMAS_TO_PROCESS.contains("FILE")) {
            adapterTestManager.process(startDate, endDate, "hourly", "FILE");
            long actualEventsCount = fileRepository.count();
            assertThat(actualEventsCount).as("input_file_raw_events count").isGreaterThan(0);
        }
    }

    @Test
    public void adapterAuthenticationTest() {
        if (CORE_SCHEMAS_TO_PROCESS.contains("AUTHENTICATION")) {
            adapterTestManager.process(startDate, endDate, "hourly", "AUTHENTICATION");
            long actualEventsCount = authenticationRepository.count();
            assertThat(actualEventsCount).as("input_authentication_raw_events count").isGreaterThan(0);
        }
    }

    @Test
    public void adapterActiveDirectoryTest() {
        if (CORE_SCHEMAS_TO_PROCESS.contains("ACTIVE_DIRECTORY")) {
            adapterTestManager.process(startDate, endDate, "hourly", "ACTIVE_DIRECTORY");
            long actualEventsCount = activeDirectoryRepository.count();
            assertThat(actualEventsCount).as("input_active_directory_raw_events count").isGreaterThan(0);
        }
    }

    @Test
    public void adapterProcessTest() {
        if (CORE_SCHEMAS_TO_PROCESS.contains("PROCESS")) {
            adapterTestManager.process(startDate, endDate, "hourly", "PROCESS");
            long actualEventsCount = processRepository.count();
            assertThat(actualEventsCount).as("input_process_raw_events count").isGreaterThan(0);
        }
    }

    @Test
    public void adapterRegistryTest() {
        if (CORE_SCHEMAS_TO_PROCESS.contains("REGISTRY")) {
            adapterTestManager.process(startDate, endDate, "hourly", "REGISTRY");
            long actualEventsCount = registryRepository.count();
            assertThat(actualEventsCount).as("input_registry_raw_events count").isGreaterThan(0);
        }
    }

    @Test
    public void adapterTlsTest() {
        if (CORE_SCHEMAS_TO_PROCESS.contains("TLS")) {
            adapterTestManager.process(startDate, endDate, "hourly", "TLS");
            long actualEventsCount = tlsRepository.count();
            assertThat(actualEventsCount).as("input_tls_raw_events count").isGreaterThan(0);
        }
    }
}
