package fortscale.collection.jobs.gds;

/**
 * Enum of configuration types
 *
 * @author gils
 * 05/01/2016
 */
public enum GDSConfigurationType {

    SCHEMA("Schema Definition"),
    COLLECTION("Collection"),
    USER_NORMALIZATION("User Normalization Task"),
    IP_RESOLVING("IP Resolving Task"),
    COMPUTER_TAGGING("Computer Tagging Task"),
    GEO_LOCATION("Geo Location Task"),
    USER_MONGO_UPDATE("User Mongo Update Task"),
    HDFS_WRITER("HDFS Writer Task"),
    ENTITIES_PROPERTIES("Entities properties configs");

    private String label;

    GDSConfigurationType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

}
