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

	@Id
	private String id;
	@Field(CONTEXT_ID_FIELD)
	private final String contextID;
	@Field(CREATION_TIME_FIELD)
	private final Date creationTime;
	@Field(MODEL_FIELD)
	private final Model model;

	public ModelDAO(String contextID, Model model) {
		this.contextID = contextID;
		this.model = model;
		this.creationTime = new Date(TimestampUtils.convertToMilliSeconds(System.currentTimeMillis()));
	}
}
