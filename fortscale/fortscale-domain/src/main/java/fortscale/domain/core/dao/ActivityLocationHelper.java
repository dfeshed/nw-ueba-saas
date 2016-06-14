package fortscale.domain.core.dao;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gils
 * 08/06/2016
 */
class ActivityLocationHelper {
    private static final String RESERVED_RANGE_COUNTRY_VALUE = "Reserved Range";
    private static final String NOT_AVAILABLE_COUNTRY_VALUE = "N/A";

    private static final Set<String> countryValuesToFilter;

    static {
        countryValuesToFilter = new HashSet<>();
        countryValuesToFilter.add(RESERVED_RANGE_COUNTRY_VALUE);
        countryValuesToFilter.add(NOT_AVAILABLE_COUNTRY_VALUE);
    }

    static Set<String> getUnknownCountryValues() {
        return Collections.unmodifiableSet(countryValuesToFilter);
    }
}
