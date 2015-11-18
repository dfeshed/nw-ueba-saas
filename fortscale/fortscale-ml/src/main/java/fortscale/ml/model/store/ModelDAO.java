package fortscale.ml.model.store;

import fortscale.ml.model.Model;
import fortscale.utils.time.TimestampUtils;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class ModelDAO {
	static final String CONTEXT_ID_FIELD = "contextId";
	private static final String CREATION_TIME_FIELD = "creationTime";
	private static final String MODEL_FIELD = "model";
	private static final String SESSION_START_TIME_FIELD = "sessionStartTime";
	private static final String SESSION_END_TIME_FIELD = "sessionEndTime";

	@Id
	private String id;
	@Field(CONTEXT_ID_FIELD)
	private final String contextId;
	@Field(CREATION_TIME_FIELD)
	private final DateTime creationTime;
	@Field(MODEL_FIELD)
	private final Model model;
	@Field(SESSION_START_TIME_FIELD)
	private final DateTime sessionStartTime;
	@Field(SESSION_END_TIME_FIELD)
	private final DateTime sessionEndTime;

	public ModelDAO(String contextId, Model model, DateTime sessionStartTime, DateTime sessionEndTime) {
		this.contextId = contextId;
		this.model = model;
		this.sessionStartTime = sessionStartTime;
		this.sessionEndTime = sessionEndTime;
		this.creationTime = new DateTime(System.currentTimeMillis());
	}
}
