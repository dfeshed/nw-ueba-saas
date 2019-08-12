package com.rsa.netwitness.presidio.automation.test.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.config.store.NetwitnessEventStoreConfig;
import com.rsa.netwitness.presidio.automation.enums.PRE_PROCESSING_CONFIGURATION_SCENARIO;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.utils.adapter.config.AdapterTestManagerConfig;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.rsa.netwitness.presidio.automation.enums.PRE_PROCESSING_CONFIGURATION_SCENARIO.E2E_BROKER;

@Deprecated
@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, AdapterTestManagerConfig.class, NetwitnessEventStoreConfig.class})
public class ConfigurationsAfterDataInjection extends AbstractTestNGSpringContextTests {
    private static  ch.qos.logback.classic.Logger LOGGER = (ch.qos.logback.classic.Logger)
            LoggerFactory.getLogger(ConfigurationsAfterDataInjection.class.getName());

    @Autowired
    private AdapterTestManager adapterTestManager;

    private Instant startDate = Instant.now();
    private Instant endDate = Instant.now();

    @Parameters({"historical_days_back", "anomaly_day","pre_processing_configuration_scenario"})
    @BeforeClass
    public void setup(@Optional("14") int historicalDaysBack, @Optional("1") int anomalyDay,
                      @Optional("MONGO") PRE_PROCESSING_CONFIGURATION_SCENARIO preProcessingConfigurationScenario){

        LOGGER.info(" ####### ConfigurationsAfterDataInjection()");
        endDate     = Instant.now().truncatedTo(ChronoUnit.DAYS);
        startDate   = endDate.minus(historicalDaysBack, ChronoUnit.DAYS);
        LOGGER.info("historicalDaysBack=" + historicalDaysBack + " anomalyDay=" + anomalyDay + " preProcessingConfigurationScenario=" + preProcessingConfigurationScenario);

        adapterTestManager.runUebaServerConfigScript(startDate);
        adapterTestManager.setEngineConfigurationParametersToTestingValues();
        if (preProcessingConfigurationScenario.equals(E2E_BROKER)) {
            LOGGER.debug("Going to execute: setTlsTimeFieldToEventTime.sh");
            adapterTestManager.setTlsTimeFieldToEventTime();
        }
        setBuildingModelsRange(7,2,2);
    }

    public void  setBuildingModelsRange(int enriched_records_days  ,int feature_aggregation_records_days , int smart_records_days )  {
       String workflows_default_file ="/etc/netwitness/presidio/configserver/configurations/airflow/workflows-default.json" ;
        ObjectMapper mapper = new ObjectMapper();
       JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader(workflows_default_file));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject workflows =  (JSONObject) obj;
        JSONObject components =  (JSONObject) workflows.get("components");
        JSONObject ade =  (JSONObject) components.get("ade");
        JSONObject models =  (JSONObject) ade.get("models");

        JSONObject enriched_records = (JSONObject) models.get("enriched_records");
        JSONObject feature_aggregation_records = (JSONObject) models.get("feature_aggregation_records");
        JSONObject smart_records = (JSONObject) models.get("smart_records");
        enriched_records.put ("min_data_time_range_for_building_models_in_days",feature_aggregation_records_days);
        feature_aggregation_records.put ("min_data_time_range_for_building_models_in_days",enriched_records_days);
        smart_records.put ("min_data_time_range_for_building_models_in_days",smart_records_days);

        models.put("enriched_records",enriched_records);
        models.put("feature_aggregation_records",feature_aggregation_records);
        models.put("smart_records",smart_records);
        ade.put("models",models);
        components.put("ade",ade);
        workflows.put("components",components);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(workflows_default_file), workflows);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void adapterProcessTest(){
        Assert.assertTrue(true);
    }
}