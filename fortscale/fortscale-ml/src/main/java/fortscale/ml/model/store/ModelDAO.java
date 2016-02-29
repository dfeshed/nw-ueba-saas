package fortscale.ml.model.store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.util.Assert;
import java.util.Date;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelDAO {
	public static final String SESSION_ID_FIELD = "sessionId";
	public static final String CONTEXT_ID_FIELD = "contextId";
	public static final String CREATION_TIME_FIELD = "creationTime";
	public static final String MODEL_FIELD = "model";
	public static final String START_TIME_FILED = "startTime";
	public static final String END_TIME_FIELD = "endTime";

	@SuppressWarnings("unused")
	@Id
	private String id;

	@Field(SESSION_ID_FIELD)
	private final String sessionId;
	@Field(CONTEXT_ID_FIELD)
	private final String contextId;
	@Field(CREATION_TIME_FIELD)
	private final Date creationTime;
	@Field(MODEL_FIELD)
	private Model model;
	@Field(START_TIME_FILED)
	private Date startTime;
	@Field(END_TIME_FIELD)
	private Date endTime;

	public ModelDAO(String sessionId, String contextId, Model model, Date startTime, Date endTime) {
		Assert.hasText(sessionId);
		Assert.hasText(contextId);
		this.sessionId = sessionId;
		this.contextId = contextId;
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
}
