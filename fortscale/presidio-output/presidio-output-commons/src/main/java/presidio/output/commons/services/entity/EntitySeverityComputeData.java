package presidio.output.commons.services.entity;

public class EntitySeverityComputeData {
    private Double percentageOfEntities;
    private Double minimumDeltaFactor;
    private Double maximumEntities;

    public EntitySeverityComputeData(Double percentageOfEntities, Double minimumDeltaFactor, Double maximumEntity) {
        this.percentageOfEntities = percentageOfEntities;
        this.minimumDeltaFactor = minimumDeltaFactor;
        this.maximumEntities = maximumEntity;
    }

    public EntitySeverityComputeData(Double percentageOfEntities) {
        this(percentageOfEntities, null, null);
    }

    public EntitySeverityComputeData(Double percentageOfEntities, Double minimumDeltaFactor) {
        this(percentageOfEntities, minimumDeltaFactor, null);
    }

    public Double getPercentageOfEntities() {
        return percentageOfEntities;
    }

    public void setPercentageOfEntities(Double percentageOfEntities) {
        this.percentageOfEntities = percentageOfEntities;
    }

    public Double getMinimumDeltaFactor() {
        return minimumDeltaFactor;
    }

    public void setMinimumDeltaFactor(Double minimumDeltaFactor) {
        this.minimumDeltaFactor = minimumDeltaFactor;
    }

    public Double getMaximumEntities() {
        return maximumEntities;
    }

    public void setMaximumEntities(Double maximumEntities) {
        this.maximumEntities = maximumEntities;
    }
}