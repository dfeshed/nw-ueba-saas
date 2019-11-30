package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario;
import com.rsa.netwitness.presidio.automation.data.processing.mongo_core.AdapterTestManager;
import org.assertj.core.util.Lists;

import java.time.Instant;
import java.util.List;

public class PreProcessingConfigScenarioFactory {
    private List<PreProcessingConfigScenario> scenarios;

    public PreProcessingConfigScenarioFactory(AdapterTestManager adapterTestManager, Instant startDate) {
        scenarios = Lists.newArrayList(
                new E2eBrokerConfigScenario(adapterTestManager, startDate),
                new CoreMongoConfigScenario(adapterTestManager, startDate)
        );
    }

    public PreProcessingConfigScenario get(ConfigurationScenario label) {
        return scenarios.stream()
                .filter(scenario -> scenario.configScenarioEnum().equals(label))
                .findFirst().orElseThrow(() -> new RuntimeException("Missing implementation for key: " + label));
    }
}
