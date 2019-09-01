package presidio.output.domain.records.alerts;

import fortscale.common.general.Schema;

import java.util.List;

public class HistoricalData {

    private String indicatorId;

    private Schema schema;

    private List<Aggregation> aggregation;

    public HistoricalData() {
    }

    public HistoricalData(List<Aggregation> aggregation) {
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

    public List<Aggregation> getAggregation() {
        return aggregation;
    }

    public void setAggregation(List<Aggregation> aggregation) {
        this.aggregation = aggregation;
    }
}
