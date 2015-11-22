package fortscale.ml.model.store;

import fortscale.ml.model.Model;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

public class ModelDAO {
	static final String CONTEXT_ID_FIELD = "contextId";
	private static final String CREATION_TIME_FIELD = "creationTime";
	private static final String MODEL_FIELD = "model";
	private static final String END_TIME_FIELD = "endTime";

	@Id
	private String id;
	@Field(CONTEXT_ID_FIELD)
	private final String contextId;
	@Field(CREATION_TIME_FIELD)
	private final DateTime creationTime;
	@Field(MODEL_FIELD)
	private final Model model;
	@Field(END_TIME_FIELD)
	private final DateTime endTime;

	public ModelDAO(String contextId, Model model, DateTime endTime) {
		this.contextId = contextId;
		this.model = model;
		this.endTime = endTime;
		this.creationTime = new DateTime(System.currentTimeMillis());
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
