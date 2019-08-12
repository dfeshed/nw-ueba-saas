package com.rsa.netwitness.presidio.automation.test.config;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario;
import com.rsa.netwitness.presidio.automation.scripts.pre_processing.PreProcessingConfigScenarioFactory;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
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


@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class PreProcessingConfiguration extends AbstractTestNGSpringContextTests {

    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(PreProcessingConfiguration.class.getName());

    @Autowired
    private AdapterTestManager adapterTestManager;

    private Instant startDate = Instant.now();

    @Parameters({"historical_days_back", "anomaly_day","pre_processing_configuration_scenario"})
    @BeforeClass
    public void setup(@Optional("14") int historicalDaysBack, @Optional("1") int anomalyDay,
                      @Optional("CORE_MONGO") ConfigurationScenario preProcessingConfigurationScenario){

        LOGGER.info("\t***** " + getClass().getSimpleName() + " started with historicalDaysBack=" + historicalDaysBack + " anomalyDay=" + anomalyDay + " preProcessingConfigurationScenario=" + preProcessingConfigurationScenario);
        LOGGER.info(preProcessingConfigurationScenario + " configuration will be executed.");
        PreProcessingConfigScenarioFactory configScenario = new PreProcessingConfigScenarioFactory(adapterTestManager, startDate);
        configScenario.get(preProcessingConfigurationScenario).execute();
    }



    @Test
    public void adapterProcessTest(){
        Assert.assertTrue(true);
    }
}