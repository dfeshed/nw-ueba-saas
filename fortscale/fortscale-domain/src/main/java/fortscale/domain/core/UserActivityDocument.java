package fortscale.domain.core;

public class UserActivityDocument extends AbstractAuditableDocument {
	public static final String USER_NAME_FIELD_NAME = "normalizedUsername";
	public static final String START_TIME_FIELD_NAME = "startTime";
	public static final String END_TIME_FIELD_NAME = "endTime";
	public static final String DATA_SOURCES_FIELD_NAME = "dataSources";
}
