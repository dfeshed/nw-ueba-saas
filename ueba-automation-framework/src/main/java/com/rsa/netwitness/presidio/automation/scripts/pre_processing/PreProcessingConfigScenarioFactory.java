package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.enums.CONFIGURATION_SCENARIO;
import com.rsa.netwitness.presidio.automation.utils.adapter.AdapterTestManager;

import java.time.Instant;

import static com.rsa.netwitness.presidio.automation.enums.CONFIGURATION_SCENARIO.*;

public class PreProcessingConfigScenarioFactory {
    private AdapterTestManager adapterTestManager;
    private Instant startDate;

    public PreProcessingConfigScenarioFactory(AdapterTestManager adapterTestManager, Instant startDate) {
        this.adapterTestManager = adapterTestManager;
        this.startDate = startDate;
    }

    public PreProcessingConfigScenario get(CONFIGURATION_SCENARIO label) {
        if (label.equals(E2E_BROKER)) {
            return new E2eBrokerConfigScenario(adapterTestManager, startDate);
        }

        if (label.equals(CORE_MONGO)) {
            return new CoreMongoConfigScenario(adapterTestManager, startDate);
        }

        throw new RuntimeException("Missing implementation for key: " + label);
    }
}
