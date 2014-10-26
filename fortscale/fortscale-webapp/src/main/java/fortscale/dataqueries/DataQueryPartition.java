package fortscale.dataqueries;

/**
 * Represents a partition configuration
 */
public class DataQueryPartition {
    public DataQueryPartition(String entityField, DataQueryPartitionType type, String partitionField){
        this.entityField = entityField;
        this.type = type;
        this.partitionField = partitionField;
    }

    public String entityField;
    public String partitionField;
    public DataQueryPartitionType type;
}
