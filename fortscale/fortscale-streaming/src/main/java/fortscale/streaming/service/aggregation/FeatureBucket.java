package fortscale.streaming.service.aggregation;

import org.springframework.data.mongodb.core.mapping.Field;

public class FeatureBucket {
	public static final String STRATEGY_ID_FIELD = "strategyId";
	public static final String USER_NAME_FIELD = "userName";
	public static final String MACHINE_NAME_FIELD = "machineName";
	public static final String START_TIME_FIELD = "startTime";
	// public static final String END_TIME_FIELD = "endTime";

	@Field(STRATEGY_ID_FIELD)
	private String strategyId;
	@Field(USER_NAME_FIELD)
	private String userName;
	@Field(MACHINE_NAME_FIELD)
	private String machineName;
	@Field(START_TIME_FIELD)
	private long startTime;
	// @Field(END_TIME_FIELD)
	// private long endTime;
}
