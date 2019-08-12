package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.enums.CONFIGURATION_SCENARIO;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import com.rsa.netwitness.presidio.automation.utils.common.ASCIIArtGenerator;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class AdapterProcessData extends AbstractTestNGSpringContextTests {
    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(AdapterProcessData.class.getName());
    private static ASCIIArtGenerator ART_GEN = new ASCIIArtGenerator();

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


    @Parameters({"historical_days_back", "anomaly_day", "pre_processing_configuration_scenario"})
    @BeforeClass
    public void setup(@Optional("10") int historicalDaysBack,
                      @Optional("1") int anomalyDay,
                      @Optional("MONGO") CONFIGURATION_SCENARIO preProcessingConfigurationScenario) {

        ART_GEN.printTextArt(getClass().getSimpleName());
        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack + " anomalyDay=" + anomalyDay + " preProcessingConfigurationScenario=" + preProcessingConfigurationScenario);
        endDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
        startDate = endDate.minus(historicalDaysBack, ChronoUnit.DAYS);
        LOGGER.info("startDate=" + startDate + " endDate=" + endDate);
    }

    @Test
    public void adapterFileTest() {
        adapterTestManager.process(startDate, endDate, "hourly", "FILE");

        long actualEventsCount = fileRepository.count();
        Assert.assertTrue(actualEventsCount > 0, "No data in input_file_raw_events");
    }

    @Test
    public void adapterAuthenticationTest() {
        adapterTestManager.process(startDate, endDate, "hourly", "AUTHENTICATION");

        long actualEventsCount = authenticationRepository.count();
        Assert.assertTrue(actualEventsCount > 0, "No data in input_authentication_raw_events");
    }

    @Test
    public void adapterActiveDirectoryTest() {
        adapterTestManager.process(startDate, endDate, "hourly", "ACTIVE_DIRECTORY");

        long actualEventsCount = activeDirectoryRepository.count();
        Assert.assertTrue(actualEventsCount > 0, "No data in input_active_directory_raw_events");
    }

    @Test
    public void adapterProcessTest() {
        adapterTestManager.process(startDate, endDate, "hourly", "PROCESS");

        long actualEventsCount = processRepository.count();
        Assert.assertTrue(actualEventsCount > 0, "No data in input_process_raw_events");
    }

    @Test
    public void adapterRegistryTest() {
        adapterTestManager.process(startDate, endDate, "hourly", "REGISTRY");

        long actualEventsCount = registryRepository.count();
        Assert.assertTrue(actualEventsCount > 0, "No data in input_registry_raw_events");
    }

    @Test
    public void adapterTlsTest() {
        adapterTestManager.process(startDate, endDate, "hourly", "TLS");
        long actualEventsCount = tlsRepository.count();
        Assert.assertTrue(actualEventsCount > 0, "No data in input_tls_raw_events");
    }
}
