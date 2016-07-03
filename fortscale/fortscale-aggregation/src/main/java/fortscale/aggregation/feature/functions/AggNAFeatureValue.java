package fortscale.aggregation.feature.functions;

/**
 * @author gils
 * 03/07/2016
 */
public enum AggNAFeatureValue {
    NOT_AVAILABLE("N/A"),
    RESERVED_RANGE("Reserved Range");

    private String value;

    AggNAFeatureValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
