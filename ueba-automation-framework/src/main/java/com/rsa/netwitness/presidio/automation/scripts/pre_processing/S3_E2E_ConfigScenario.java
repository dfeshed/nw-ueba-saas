package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.data.processing.airflow.AirflowHelper;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.AdapterTestManager;
import com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario;

import java.time.Instant;

import static com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario.E2E_S3;

class S3_E2E_ConfigScenario implements PreProcessingConfigScenario {
    private AdapterTestManager adapterTestManager;
    private Instant startDate;

    S3_E2E_ConfigScenario(AdapterTestManager adapterTestManager, Instant startDate) {
        this.adapterTestManager = adapterTestManager;
        this.startDate = startDate;
    }

    @Override
    public ConfigurationScenario configScenarioEnum() {
        return E2E_S3;
    }

    @Override
    public void execute() {
        adapterTestManager.backupTransformerConfig();
        adapterTestManager.setAdapterConfigurationPropertiesToProductionMode();
        adapterTestManager.runUebaServerConfigScript(startDate);
        adapterTestManager.setEngineConfigurationParametersToTestingValues();
        adapterTestManager.setS3E2EConfigurationForAdapterAndTransformer();
        adapterTestManager.setBuildingModelsRange(7, 2, 2);
        AirflowHelper.INSTANCE.startAirflowScheduler().output.forEach(System.out::println);
    }
}
