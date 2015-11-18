package fortscale.ml.model.store;

import fortscale.ml.model.Model;
import fortscale.utils.time.TimestampUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

public class ModelDAO {
	static final String CONTEXT_ID_FIELD = "contextId";
	private static final String CREATION_TIME_FIELD = "creationTime";
	private static final String MODEL_FIELD = "model";
	private static final String SESSION_ID_FIELD = "sessionID";

	@Id
	private String id;
	@Field(CONTEXT_ID_FIELD)
	private final String contextId;
	@Field(CREATION_TIME_FIELD)
	private final Date creationTime;
	@Field(MODEL_FIELD)
	private final Model model;
	@Field(SESSION_ID_FIELD)
	private final long sessionId;

	public ModelDAO(String contextId, Model model, long sessionID) {
		this.contextId = contextId;
		this.model = model;
		this.sessionId = sessionID;
		this.creationTime = new Date(TimestampUtils.convertToMilliSeconds(System.currentTimeMillis()));
	}

	public String getContextId() {
		return contextId;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public Model getModel() {
		return model;
	}

	public long getSessionId() {
		return sessionId;
	}
}
