package presidio.output.domain.records.alerts;

import fortscale.common.general.Schema;

public class HistoricalData {

    private String indicatorId;

    private Schema schema;

    private Aggregation aggregation;

    public HistoricalData() {
    }

    public HistoricalData(Aggregation aggregation) {
        this.aggregation = aggregation;
    }

    public String getIndicatorId() {
        return indicatorId;
    }

    public void setIndicatorId(String indicatorId) {
        this.indicatorId = indicatorId;
    }

    public Schema getSchema() {
        return schema;
    }

    public void setSchema(Schema schema) {
        this.schema = schema;
    }

    public Aggregation getAggregation() {
        return aggregation;
    }

    public void setAggregation(Aggregation aggregation) {
        this.aggregation = aggregation;
    }
}
