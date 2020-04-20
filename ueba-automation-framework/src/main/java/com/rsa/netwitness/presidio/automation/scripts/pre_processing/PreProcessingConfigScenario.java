package com.rsa.netwitness.presidio.automation.scripts.pre_processing;

import com.rsa.netwitness.presidio.automation.enums.ConfigurationScenario;

public interface PreProcessingConfigScenario {

    ConfigurationScenario configScenarioEnum();

    void execute();

}
