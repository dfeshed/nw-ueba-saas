package com.rsa.netwitness.presidio.automation.mapping.indicators;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;

import static fortscale.common.general.Schema.TLS;

class TlsIndicators {
    private static List<String> TLS_EXCLUDED_INDICATORS = Lists.newArrayList(
            "high_number_of_bytes_sent_to_new_ja3_outbound",
            "high_number_of_bytes_sent_to_new_ssl_subject_outbound",
            "high_number_of_distinct_src_ip_for_new_ssl_subject_outbound",
            "high_number_of_bytes_sent_by_src_ip_to_new_ssl_subject_outbound"
    );

    private static Lazy<List<String>> lazy = new Lazy<>();
    static final List<String> TLS_MANDATORY_INDICATORS = lazy.getOrCompute(TlsIndicators::getFromResource);

    private static List<String> getFromResource() {
        List<String> indicators = IndicatorsInfoSupplier.getIndicatorsBySchema(TLS);
        indicators.removeAll(TLS_EXCLUDED_INDICATORS);
        return indicators;
    }

}
