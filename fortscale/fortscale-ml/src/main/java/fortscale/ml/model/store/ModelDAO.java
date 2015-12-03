package fortscale.ml.model.store;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import fortscale.ml.model.Model;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
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
	private final Date creationTime;
	@Field(MODEL_FIELD)
	private Model model;
	@Field(END_TIME_FIELD)
	private Date endTime;

	public ModelDAO(String sessionId, String contextId, Model model, Date endTime) {
		this.sessionId = sessionId;
		this.contextId = contextId;
		this.model = model;
		this.endTime = endTime;
		this.creationTime = new Date(System.currentTimeMillis());
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

	public Date getEndTime() {
		return endTime;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}
}
