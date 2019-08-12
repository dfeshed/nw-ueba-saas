package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.common.helpers.RunCmdUtils;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;

import java.time.Instant;

class E2eBrokerConfigScenario extends PreProcessingConfigScenario {
    private AdapterTestManager adapterTestManager;
    private Instant startDate;

    public E2eBrokerConfigScenario(AdapterTestManager adapterTestManager, Instant startDate) {
        this.adapterTestManager = adapterTestManager;
        this.startDate = startDate;
    }

    @Override
    public void execute() {
        adapterTestManager.setAdapterConfigurationPropertiesToProductionMode();
        adapterTestManager.runUebaServerConfigScript(startDate);
        adapterTestManager.setEngineConfigurationParametersToTestingValues();
        adapterTestManager.setTlsTimeFieldToEventTime();
        adapterTestManager.setBuildingModelsRange(7,2,2);
        RunCmdUtils.runCmd("sudo systemctl start airflow-scheduler");
    }
}
