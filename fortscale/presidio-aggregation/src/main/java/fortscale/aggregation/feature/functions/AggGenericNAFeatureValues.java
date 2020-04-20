package fortscale.aggregation.feature.functions;

import java.util.HashSet;
import java.util.Set;

/**
 * @author gils
 * 03/07/2016
 */
public class AggGenericNAFeatureValues {
    public static final String NOT_AVAILABLE = "N/A";

    private static Set<String> naValues = new HashSet<>();

    static {
        naValues.add(NOT_AVAILABLE);
    }

    static Set<String> getNAValues() {
        return naValues;
    }
}
