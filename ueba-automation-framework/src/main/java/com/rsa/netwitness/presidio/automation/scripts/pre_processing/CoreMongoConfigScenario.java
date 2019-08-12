package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;

import java.time.Instant;

public class CoreMongoConfigScenario extends PreProcessingConfigScenario {
    private AdapterTestManager adapterTestManager;
    private Instant startDate;

    CoreMongoConfigScenario(AdapterTestManager adapterTestManager, Instant startDate) {
        this.adapterTestManager = adapterTestManager;
        this.startDate = startDate;
    }

    @Override
    public void execute() {
        adapterTestManager.setAdapterConfigurationPropertiesToTestMode();
        adapterTestManager.submitMongoDbDetailsIntoAdapterConfigurationProperties();
        adapterTestManager.runUebaServerConfigScript(startDate);
        adapterTestManager.setEngineConfigurationParametersToTestingValues();
    }
}
