package fortscale.ml.model.store;

import fortscale.ml.model.Model;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class ModelDAO {
	public static final String SESSION_ID_FIELD = "sessionId";
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String CREATION_TIME_FIELD = "creationTime";
	public static final String MODEL_FIELD = "model";
	public static final String END_TIME_FIELD = "endTime";

	@Id
	private String id;

	@Field(SESSION_ID_FIELD)
	private final String sessionId;
	@Field(CONTEXT_ID_FIELD)
	private final String contextId;
	@Field(CREATION_TIME_FIELD)
	private final DateTime creationTime;
	@Field(MODEL_FIELD)
	private final Model model;
	@Field(END_TIME_FIELD)
	private final DateTime endTime;

	public ModelDAO(String sessionId, String contextId, Model model, DateTime endTime) {
		this.sessionId = sessionId;
		this.contextId = contextId;
		this.model = model;
		this.endTime = endTime;
		this.creationTime = new DateTime(System.currentTimeMillis());
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getContextId() {
		return contextId;
	}

	public DateTime getCreationTime() {
		return creationTime;
	}

	public Model getModel() {
		return model;
	}

	public DateTime getEndTime() {
		return endTime;
	}
}
