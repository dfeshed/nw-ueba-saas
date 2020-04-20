package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.data.processing.airflow.AirflowHelper;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario;
import com.rsa.netwitness.presidio.automation.mongo.RespondServerAlertCollectionHelper;

import java.time.Instant;

import static com.rsa.netwitness.presidio.automation.config.EnvironmentProperties.ENVIRONMENT_PROPERTIES;
import static com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario.E2E_MONGO;

class E2eMongoConfigScenario implements PreProcessingConfigScenario {
    private AdapterTestManager adapterTestManager;
    private Instant startDate;

    E2eMongoConfigScenario(AdapterTestManager adapterTestManager, Instant startDate) {
        this.adapterTestManager = adapterTestManager;
        this.startDate = startDate;
    }

    @Override
    public ConfigurationScenario configScenarioEnum() {
        return E2E_MONGO;
    }

    @Override
    public void execute() {
        adapterTestManager.backupTransformerConfig();
        adapterTestManager.submitMongoDbDetailsIntoAdapterConfigurationProperties();
        adapterTestManager.setAdapterConfigurationPropertiesToTestMode();
        adapterTestManager.runUebaServerConfigScript(startDate);
        adapterTestManager.setEngineConfigurationParametersToTestingValues();
        adapterTestManager.setMongoConfigurationForTransformer();
        adapterTestManager.setBuildingModelsRange(7, 7, 2);
        if ( !ENVIRONMENT_PROPERTIES.esaAnalyticsServerIp().isBlank() ) {
            new RespondServerAlertCollectionHelper().truncateCollection();
        }
        AirflowHelper.INSTANCE.startAirflowScheduler().output.forEach(System.out::println);
    }

}



