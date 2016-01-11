package fortscale.collection.jobs.gds;

/**
 * Enum of configuration types
 *
 * @author gils
 * 05/01/2016
 */
public enum GDSConfigurationType {
    SCHEMA,
    COLLECTION,
    USER_NORMALIZATION,
    IP_RESOLVING,
    COMPUTER_TAGGING,
    GEO_LOCATION,
    USER_MONGO_UPDATE,
    HDFS_WRITER,
	RAW_MODEL_AND_SCORE,
	AGGREGATED_FEATURE_MODEL_AND_SCORE,
	ENTITY_EVENTS_MODEL_AND_SCORE;
}
