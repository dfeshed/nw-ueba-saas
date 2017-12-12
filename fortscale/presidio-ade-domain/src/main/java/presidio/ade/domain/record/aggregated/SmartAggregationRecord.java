package presidio.ade.domain.record.aggregated;

/**
 * This is a wrapper POJO for the {@link AdeAggregationRecord} - It contains additional
 * information regarding the aggregation record in context of a specific {@link SmartRecord}.
 */
public class SmartAggregationRecord {
    private AdeAggregationRecord aggregationRecord;
    private Double contribution;
    private Double scoreAndWeightProduct;

    public SmartAggregationRecord(
            AdeAggregationRecord aggregationRecord,
            Double contribution,
            Double scoreAndWeightProduct) {

        this.aggregationRecord = aggregationRecord;
        this.contribution = contribution;
        this.scoreAndWeightProduct = scoreAndWeightProduct;
    }

    public SmartAggregationRecord(AdeAggregationRecord aggregationRecord) {
        this(aggregationRecord, null, null);
    }

    public SmartAggregationRecord() {
        this(null);
    }

    public AdeAggregationRecord getAggregationRecord() {
        return aggregationRecord;
    }

    public void setAggregationRecord(AdeAggregationRecord aggregationRecord) {
        this.aggregationRecord = aggregationRecord;
    }

    public Double getContribution() {
        return contribution;
    }

    public void setContribution(Double contribution) {
        this.contribution = contribution;
    }

    public Double getScoreAndWeightProduct() {
        return scoreAndWeightProduct;
    }

    public void setScoreAndWeightProduct(Double scoreAndWeightProduct) {
        this.scoreAndWeightProduct = scoreAndWeightProduct;
    }
}
