package fortscale.domain.adapter;

public class SchemaMapping {
    private String schemaName;
    private String collectionName;
    private String timeFieldName;

    private int numberOfRetainedDays;

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    public void setCollectionName(String collectionName) {
        this.collectionName = collectionName;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

    public int getNumberOfRetainedDays() {
        return numberOfRetainedDays;
    }

    public void setNumberOfRetainedDays(int numberOfRetainedDays) {
        this.numberOfRetainedDays = numberOfRetainedDays;
    }

    @Override
    public String toString() {
        return "SchemaMapping{" +
                "schemaName='" + schemaName + '\'' +
                ", collectionName='" + collectionName + '\'' +
                ", timeFieldName='" + timeFieldName + '\'' +
                ", numberOfRetainedDays=" + numberOfRetainedDays +
                '}';
    }
}
