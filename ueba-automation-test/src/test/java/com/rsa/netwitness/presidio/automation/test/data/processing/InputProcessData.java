package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.repository.*;
import com.rsa.netwitness.presidio.automation.utils.input.InputTestManager;
import com.rsa.netwitness.presidio.automation.utils.input.config.InputTestManagerConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/***
 * This class runs as intermediate step in core components test. Adapter_Process_Data suite should run before.
 * This class can run as isolated input component test, input_..._raw_events collections should be populated externally.
 *
 * Input Test manager processes data that already exist in input raw data collections
 */
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, InputTestManagerConfig.class})
public class InputProcessData extends AbstractTestNGSpringContextTests {
    static ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(InputProcessData.class.getName());

    @Autowired
    private InputTestManager inputTestManager;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private AdapterActiveDirectoryStoredDataRepository activeDirectoryInputRawRepository;
    @Autowired
    private AdapterTlsStoredDataRepository tlsInputRawRepository;
    @Autowired
    private AdapterAuthenticationStoredDataRepository authenticationInputRawRepository;
    @Autowired
    private AdapterFileStoredDataRepository fileInputRawRepository;
    @Autowired
    private AdapterProcessStoredDataRepository processInputRawRepository;
    @Autowired
    private AdapterRegistryStoredDataRepository registryInputRawRepository;
//    @Autowired
//    private AdapterIocStoredDataRepository iocInputRawRepository;

    @Autowired
    private FileEnrichStoredDataRepository fileEnrichStoredDataRepository;
    @Autowired
    private TlsEnrichStoredDataRepository tlsEnrichStoredDataRepository;
    @Autowired
    private AuthenticationEnrichStoredDataRepository authenticationEnrichStoredDataRepository;
    @Autowired
    private ActiveDirectoryEnrichStoredDataRepository activeDirectoryEnrichStoredDataRepository;
    @Autowired
    private ProcessEnrichStoredDataRepository processEnrichStoredDataRepository;
    @Autowired
    private RegistryEnrichStoredDataRepository registryEnrichStoredDataRepository;
//    @Autowired
//    private IocEnrichStoredDataRepository iocEnrichStoredDataRepository;

    private Instant startDate = Instant.now();
    private Instant endDate = Instant.now();

    @Parameters("historical_days_back")
    @BeforeClass
    public void prepareTestData(@Optional("10") int historicalDaysBack){
        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack);
        endDate     = Instant.now().truncatedTo(ChronoUnit.DAYS);
        startDate   = endDate.minus(historicalDaysBack, ChronoUnit.DAYS);
        LOGGER.info("startDate=" + startDate + " endDate=" + endDate);
    }

    @Test
    public void inputFileTest(){
        clearCollections("FILE");
        inputTestManager.process(startDate, endDate, "FILE");
        long rawFileEventsCount = fileInputRawRepository.count();
        long enrichedFileEventsCount = fileEnrichStoredDataRepository.count();
        Assert.assertEquals(enrichedFileEventsCount, rawFileEventsCount,  "Events count is wrong in enriched_file");
    }

    @Test
    public void inputAuthenticationTest(){
        clearCollections("AUTHENTICATION");
        inputTestManager.process(startDate, endDate, "AUTHENTICATION");
        long rawAuthenticationEventsCount = authenticationInputRawRepository.count();
        long enrichedAuthenticationEventsCount = authenticationEnrichStoredDataRepository.count();
        Assert.assertEquals(enrichedAuthenticationEventsCount, rawAuthenticationEventsCount, "Events count is wrong in enriched_authentication");
    }

    @Test
    public void inputActiveDirectoryTest(){
        clearCollections("ACTIVE_DIRECTORY");
        inputTestManager.process(startDate, endDate, "ACTIVE_DIRECTORY");
        long rawActiveDirectoryEventsCount = activeDirectoryInputRawRepository.count();
        long enrichedActiveDirectoryEventsCount = activeDirectoryEnrichStoredDataRepository.count();
        Assert.assertEquals(enrichedActiveDirectoryEventsCount, rawActiveDirectoryEventsCount, "Events count is wrong in enriched_active_directory");
    }
    @Test
    public void inputProcessTest(){
        clearCollections("PROCESS");
        inputTestManager.process(startDate, endDate, "PROCESS");
        long rawProcessEventsCount = processInputRawRepository.count();
        long enrichedProcessEventsCount = processEnrichStoredDataRepository.count();
        Assert.assertEquals(enrichedProcessEventsCount, rawProcessEventsCount, "Events count is wrong in enriched_process");
    }
    @Test
    public void inputRegistryTest(){
        clearCollections("REGISTRY");
        inputTestManager.process(startDate, endDate, "REGISTRY");
        long rawRegistryEventsCount = registryInputRawRepository.count();
        long enrichedRegistryEventsCount = registryEnrichStoredDataRepository.count();
        Assert.assertEquals(enrichedRegistryEventsCount, rawRegistryEventsCount, "Events count is wrong in enriched_registry");
    }

    @Test
    public void inputTlsTest(){
        // clearCollections("TLS");
        inputTestManager.process(startDate, endDate, "TLS");
        long rawRegistryEventsCount = tlsInputRawRepository.count();
        long enrichedRegistryEventsCount = tlsEnrichStoredDataRepository.count();
        Assert.assertEquals(enrichedRegistryEventsCount, rawRegistryEventsCount, "Events count is wrong in enriched_tls");
    }
//    @Test
//    public void inputIocTest(){
//        clearCollections("IOC");
//        inputTestManager.process(startDate, endDate, "IOC");
//        long rawIocEventsCount = iocInputRawRepository.count();
//        long enrichedIocEventsCount = iocEnrichStoredDataRepository.count();
//        Assert.assertEquals(rawIocEventsCount, enrichedIocEventsCount, "Events count is wrong in enriched_ioc");
//    }

    public void clearCollections(String schemaName) {
        String schemaNameInCollectionName = (schemaName.equalsIgnoreCase("active_directory")?"activedirectory":schemaName.toLowerCase());
        mongoTemplate.getCollectionNames().forEach(collectionName -> {
            if ((collectionName.startsWith("enriched_") || collectionName.startsWith("output_")) &&
                    // workaround for ADE collections naming convention: in some cases it is in Camel case, in other - with underscore
                    (collectionName.toLowerCase().contains(schemaNameInCollectionName) ||
                            collectionName.toLowerCase().contains(schemaName.toLowerCase())))
            {
                mongoTemplate.dropCollection(collectionName);
            }
        });
    }

}