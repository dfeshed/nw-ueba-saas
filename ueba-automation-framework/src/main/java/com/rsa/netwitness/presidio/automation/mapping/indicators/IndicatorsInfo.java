package com.rsa.netwitness.presidio.automation.mapping.indicators;

import com.google.common.collect.ImmutableList;
import fortscale.common.general.Schema;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.ImmutableList.copyOf;

public class IndicatorsInfo {

    public static final ImmutableList<String> TLS_MANDATORY_INDICATORS = copyOf(TlsIndicators.TLS_MANDATORY_INDICATORS);
    public static final ImmutableList<String> AUTHENTICATION_MANDATORY_INDICATORS = copyOf(OperationsIndicators.AUTHENTICATION_MANDATORY_INDICATORS);
    public static final ImmutableList<String> ACTIVE_DIRECTORY_MANDATORY_INDICATORS =  copyOf(OperationsIndicators.ACTIVE_DIRECTORY_MANDATORY_INDICATORS);
    public static final ImmutableList<String> FILE_MANDATORY_INDICATORS =  copyOf(OperationsIndicators.FILE_MANDATORY_INDICATORS);
    public static final ImmutableList<String> PROCESS_MANDATORY_INDICATORS =  copyOf(OperationsIndicators.PROCESS_MANDATORY_INDICATORS);
    public static final ImmutableList<String> REGISTRY_MANDATORY_INDICATORS =  copyOf(OperationsIndicators.REGISTRY_MANDATORY_INDICATORS);

    public static final ImmutableList<String> ALL_MANDATORY_INDICATORS =  copyOf(Stream.of(
            TLS_MANDATORY_INDICATORS.stream(),
            AUTHENTICATION_MANDATORY_INDICATORS.stream(),
            ACTIVE_DIRECTORY_MANDATORY_INDICATORS.stream(),
            FILE_MANDATORY_INDICATORS.stream(),
            PROCESS_MANDATORY_INDICATORS.stream(),
            REGISTRY_MANDATORY_INDICATORS.stream()
    ).flatMap(e -> e).collect(Collectors.toList()));

    public static final ImmutableList<String> ALL_OPERATION_INDICATORS =  copyOf(Stream.of(
            AUTHENTICATION_MANDATORY_INDICATORS.stream(),
            ACTIVE_DIRECTORY_MANDATORY_INDICATORS.stream(),
            FILE_MANDATORY_INDICATORS.stream(),
            PROCESS_MANDATORY_INDICATORS.stream(),
            REGISTRY_MANDATORY_INDICATORS.stream()
    ).flatMap(e -> e).collect(Collectors.toList()));

    public static final ImmutableList<String> MULTIPLE_GRAPHS_INDICATORS =  ImmutableList.of(
            "high_number_of_bytes_sent_by_src_ip_to_domain_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_dst_org_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_dst_port_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_domain_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_dst_org_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_dst_port_ssl_subject_outbound",

            "high_number_of_distinct_src_ip_for_new_domain_for_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_new_domain_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_new_domain_ssl_subject_outbound",

            "high_number_of_distinct_src_ip_for_new_dst_org_for_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_new_dst_org_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_new_dst_org_ssl_subject_outbound",

            "high_number_of_distinct_src_ip_for_new_dst_port_for_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_new_dst_port_ssl_subject_outbound",
            "high_number_of_bytes_sent_to_new_dst_port_ssl_subject_outbound"
    );


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
