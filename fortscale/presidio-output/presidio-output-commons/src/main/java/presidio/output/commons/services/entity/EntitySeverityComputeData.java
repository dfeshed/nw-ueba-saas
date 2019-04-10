package presidio.output.commons.services.entity;

public class EntitySeverityComputeData {
    private Double percentageOfEntities;
    private Double minimumDeltaFactor;
    private Double maximumEntities;

    public EntitySeverityComputeData(double percentageOfEntities, double minimumDeltaFactor, Double maximumEntity) {
        this.percentageOfEntities = percentageOfEntities;
        this.minimumDeltaFactor = minimumDeltaFactor;
        this.maximumEntities = maximumEntity;
    }

    public EntitySeverityComputeData(double percentageOfEntities) {
        this.percentageOfEntities = percentageOfEntities;
    }

    public EntitySeverityComputeData(Double percentageOfEntities, Double minimumDeltaFactor) {
        this.percentageOfEntities = percentageOfEntities;
        this.minimumDeltaFactor = minimumDeltaFactor;
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