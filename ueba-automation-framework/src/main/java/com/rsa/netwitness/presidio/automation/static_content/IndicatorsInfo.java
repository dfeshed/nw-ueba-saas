package com.rsa.netwitness.presidio.automation.static_content;

import fortscale.common.general.Schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class IndicatorsInfo {

    public static List<String> TLS_MANDATORY_INDICATORS = NetworkIndicators.TLS_MANDATORY_INDICATORS;
    public static List<String> AUTHENTICATION_MANDATORY_INDICATORS = OperationsIndicators.AUTHENTICATION_MANDATORY_INDICATORS;
    public static List<String> ACTIVE_DIRECTORY_MANDATORY_INDICATORS = OperationsIndicators.ACTIVE_DIRECTORY_MANDATORY_INDICATORS;
    public static List<String> FILE_MANDATORY_INDICATORS = OperationsIndicators.FILE_MANDATORY_INDICATORS;
    public static List<String> PROCESS_MANDATORY_INDICATORS = OperationsIndicators.PROCESS_MANDATORY_INDICATORS;
    public static List<String> REGISTRY_MANDATORY_INDICATORS = OperationsIndicators.REGISTRY_MANDATORY_INDICATORS;

    public static List<String> ALL_MANDATORY_INDICATORS = Stream.of(
            TLS_MANDATORY_INDICATORS.stream(),
            AUTHENTICATION_MANDATORY_INDICATORS.stream(),
            ACTIVE_DIRECTORY_MANDATORY_INDICATORS.stream(),
            FILE_MANDATORY_INDICATORS.stream(),
            PROCESS_MANDATORY_INDICATORS.stream(),
            REGISTRY_MANDATORY_INDICATORS.stream()
    ).flatMap(e -> e).collect(Collectors.toList());


    public static Map<String, Schema> getIndicatorsToSchemaMap() {
        return IndicatorsInfoSupplier.indicatorToSchema.get();
    }

    public static String getSchemaNameByIndicator(String indicatorName) {
        return IndicatorsInfoSupplier.indicatorToSchema.get().get(indicatorName).getName();
    }

    public static Map<String, String> getIndicatorsToClassificationMap() {
        return IndicatorsInfoSupplier.indicatorToClassification.get();
    }

    public static String getClassificationByIndicator(String indicatorName) {
        return IndicatorsInfoSupplier.indicatorToClassification.get().get(indicatorName);
    }

    public static List<String> getClassificationsByPrioritiesAsc() {
        return IndicatorsInfoSupplier.classificationsByPrioritiesAsc.get();
    }

    public static String getFeatureNameByIndicator(String indicator) {
        return IndicatorsInfoSupplier.indicatorToFeatureNames.get().get(indicator);
    }
}
