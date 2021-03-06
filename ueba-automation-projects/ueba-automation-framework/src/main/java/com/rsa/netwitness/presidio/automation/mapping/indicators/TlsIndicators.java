package com.rsa.netwitness.presidio.automation.mapping.indicators;

import com.google.common.collect.Lists;
import com.rsa.netwitness.presidio.automation.utils.common.Lazy;

import java.util.List;

import static fortscale.common.general.Schema.TLS;

class TlsIndicators {
    private static List<String> TLS_EXCLUDED_INDICATORS = Lists.newArrayList();

    private static Lazy<List<String>> lazy = new Lazy<>();
    static final List<String> TLS_MANDATORY_INDICATORS = lazy.getOrCompute(TlsIndicators::getFromResource);

    private static List<String> getFromResource() {
        List<String> indicators = IndicatorsInfoSupplier.getIndicatorsBySchema(TLS);
        indicators.removeAll(TLS_EXCLUDED_INDICATORS);
        return indicators;
    }

}
