package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.AdapterTestManager;

import java.time.Instant;

import static com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario.CORE_MONGO;

class CoreMongoConfigScenario implements PreProcessingConfigScenario {
    private AdapterTestManager adapterTestManager;
    private Instant startDate;

    CoreMongoConfigScenario(AdapterTestManager adapterTestManager, Instant startDate) {
        this.adapterTestManager = adapterTestManager;
        this.startDate = startDate;
    }

    @Override
    public ConfigurationScenario configScenarioEnum() {
        return CORE_MONGO;
    }

    @Override
    public void execute() {
        adapterTestManager.backupTransformerConfig();
        adapterTestManager.submitMongoDbDetailsIntoAdapterConfigurationProperties();
        adapterTestManager.setAdapterConfigurationPropertiesToTestMode();
        adapterTestManager.runUebaServerConfigScript(startDate);
        adapterTestManager.setEngineConfigurationParametersToTestingValues();
        adapterTestManager.setMongoConfigurationForTransformer();
    }
}
