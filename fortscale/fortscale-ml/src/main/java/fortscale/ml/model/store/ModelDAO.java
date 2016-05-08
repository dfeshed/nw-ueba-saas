package fortscale.ml.model.store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;

import java.util.Date;

@JsonAutoDetect(fieldVisibility = Visibility.NONE, getterVisibility = Visibility.NONE, setterVisibility = Visibility.ANY)
public class ModelDAO {
	public static final String SESSION_ID_FIELD = "sessionId";
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String CREATION_TIME_FIELD = "creationTime";
	public static final String MODEL_FIELD = "model";
	public static final String START_TIME_FILED = "startTime";
	public static final String END_TIME_FIELD = "endTime";
	private static final String SESSION_ID_CAN_BE_SET_ONLY_ONCE_ERROR_MSG = "sessionId field can be set only once in the obejct lifetime..";
	private static final String CONTEXT_ID_CAN_BE_SET_ONLY_ONCE_ERROR_MSG = "contextId field can be set only once in the obejct lifetime..";
	private static final String CREATION_TIME_CAN_BE_SET_ONLY_ONCE_ERROR_MSG = "creationTime field can be set only once in the obejct lifetime.";

	@SuppressWarnings("unused")
	@Id
	private String id;

	@Field(SESSION_ID_FIELD)
	private String sessionId;
	@Field(CONTEXT_ID_FIELD)
	private String contextId;
	@Field(CREATION_TIME_FIELD)
	private Date creationTime;
	@Field(MODEL_FIELD)
	private Model model;
	@Field(START_TIME_FILED)
	private Date startTime;
	@Field(END_TIME_FIELD)
	private Date endTime;

	// This constructor is required for deserialization from RockDB
	public ModelDAO() {
	}

	public ModelDAO(String sessionId, String contextId, Model model, Date startTime, Date endTime) {
		setSessionId(sessionId);
		setContextId(contextId);
		this.creationTime = new Date();
		setModel(model);
		setStartTime(startTime);
		setEndTime(endTime);
	}

	public String getSessionId() {
		return sessionId;
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

	public void setModel(Model model) {
		Assert.notNull(model);
		this.model = model;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		Assert.notNull(startTime);
		this.startTime = startTime;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		Assert.notNull(endTime);
		this.endTime = endTime;
	}

	public ModelDAO setSessionId(String sessionId) {
		Assert.isNull(this.sessionId, SESSION_ID_CAN_BE_SET_ONLY_ONCE_ERROR_MSG);
		Assert.hasText(sessionId);

		this.sessionId = sessionId;
		return this;
	}

	public ModelDAO setContextId(String contextId) {
		Assert.isNull(this.contextId, CONTEXT_ID_CAN_BE_SET_ONLY_ONCE_ERROR_MSG);
		//TODO: contextId is null for global models. We should use something other than null (and then uncomment the assertion)
//		Assert.hasText(contextId);
		this.contextId = contextId;
		return this;
	}

	public ModelDAO setCreationTime(Date creationTime) {
		Assert.isNull(this.creationTime, CREATION_TIME_CAN_BE_SET_ONLY_ONCE_ERROR_MSG);
		this.creationTime = creationTime;
		return this;
	}
}
