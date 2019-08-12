package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.domain.store.NetwitnessEventStore;
import com.rsa.netwitness.presidio.automation.log_player.MongoCollectionsMonitor;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * This class purpose is to monitor Airflow progress on generated scenarios data.
 * The progress measured by comparison of events in mongo collections: netwitness_file_events vs input_file_raw_events.
 * The number of events should be equal in current scenario (no filtering test cases included).
 * Assuming that it's enough to measure progress on one schema. This is because Airflow subdags for all schemas together hour by hour.
 * <p>
 * Status is printed to cosole and written into file "waitAirflowFinish.log" in the working directory.
 * Timeout defined for entire scenario - 20h
 * <p>
 * TODO: improve timeout - check it hourly. This will allow to continue with better status, in case of single hour failure
 **/
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class MonitorBrokerAdapterDataProcessing extends AbstractTestNGSpringContextTests {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private AdapterTestManager adapterTestManager;
    @Autowired
    private AdapterActiveDirectoryStoredDataRepository activeDirectoryRepository;
    @Autowired
    private AdapterAuthenticationStoredDataRepository authenticationRepository;
    @Autowired
    private AdapterFileStoredDataRepository fileRepository;
    @Autowired
    private AdapterRegistryStoredDataRepository registryRepository;
    @Autowired
    private AdapterTlsStoredDataRepository tlsRepository;
    @Autowired
    private NetwitnessEventStore netwitnessEventStore;

    // Will wait until reach hour 23:00 on "days back" 1. (all anomalies happen until DB 1 end of day, stopping at hour 23 - to avoid heavy run of modeling at this hour)

    private Instant startDate = Instant.now();
    private Instant endDate = Instant.now();

    private static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(MonitorBrokerAdapterDataProcessing.class.getName());

    @Parameters({"historical_days_back", "anomaly_day"})
    @BeforeClass
    public void setup(@Optional("30") int historicalDaysBack, @Optional("1") int anomalyDay) {
        logger.debug("historicalDaysBack =" + historicalDaysBack);
        endDate = Instant.now().truncatedTo(ChronoUnit.DAYS);
        startDate = endDate.minus(historicalDaysBack, ChronoUnit.DAYS);
    }


    @Test
    public void waitForComplete_Test() throws InterruptedException {

        List<MongoRepository> repositories =
                new LinkedList<>(Arrays.asList(
                        activeDirectoryRepository,
                        authenticationRepository,
                        fileRepository,
                        registryRepository,
                        tlsRepository
                ));

        MongoCollectionsMonitor monitor = new MongoCollectionsMonitor(repositories);
        LOGGER.debug(" startDate=" + startDate + " endDate=" + endDate);
        monitor.createTasks(startDate, endDate);
        monitor.execute();
        boolean result = monitor.waitForResult();
        monitor.shutdown();
        Assert.assertTrue(result, "Data processing has not reached the last day.");
    }
}
