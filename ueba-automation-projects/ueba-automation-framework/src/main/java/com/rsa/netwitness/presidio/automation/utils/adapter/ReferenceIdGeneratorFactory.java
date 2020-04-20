package com.rsa.netwitness.presidio.automation.utils.adapter;

import presidio.data.generators.common.IStringGenerator;
import presidio.data.generators.common.StringCyclicValuesGenerator;

import java.util.Map;

public class ReferenceIdGeneratorFactory {
    public static final String REFERENCE_ID_GENERATOR_TYPE_CONFIG_KEY = "referenceIdGeneratorType";

    public static IStringGenerator create(Map<String, String> config, String[] referenceIds) {
        String referenceIdGeneratorType = config.get(REFERENCE_ID_GENERATOR_TYPE_CONFIG_KEY);

        if (referenceIdGeneratorType.equalsIgnoreCase("cyclic")) {
            return new StringCyclicValuesGenerator(referenceIds);
        } else {
            String s = String.format("Reference ID generator type %s is not supported.", referenceIdGeneratorType);
            throw new IllegalArgumentException(s);
        }
    }
}
