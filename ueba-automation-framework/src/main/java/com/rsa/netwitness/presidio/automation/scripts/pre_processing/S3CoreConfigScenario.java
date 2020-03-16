package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario;

import java.time.Instant;

import static com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario.CORE_S3;

class S3CoreConfigScenario implements PreProcessingConfigScenario {
    private AdapterTestManager adapterTestManager;
    private Instant startDate;

    S3CoreConfigScenario(AdapterTestManager adapterTestManager, Instant startDate) {
        this.adapterTestManager = adapterTestManager;
        this.startDate = startDate;
    }

    @Override
    public ConfigurationScenario configScenarioEnum() {
        return CORE_S3;
    }

    @Override
    public void execute() {
        adapterTestManager.backupTransformerConfig();
        adapterTestManager.runAwsUebaServerConfigScript(startDate);
        adapterTestManager.setEngineConfigurationParametersToTestingValues();
    }
}
