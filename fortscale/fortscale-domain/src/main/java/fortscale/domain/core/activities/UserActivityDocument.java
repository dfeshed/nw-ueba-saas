package fortscale.domain.core.activities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import fortscale.domain.core.AbstractAuditableDocument;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;


@CompoundIndexes({
		@CompoundIndex(name = "user_start_time", def = "{'entityId': -1, 'startTime': 1}")
})
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class UserActivityDocument extends AbstractAuditableDocument { //Todo: when you subclass this make sure that you add the new document class to UserActivityDocumentFactory.getInstanceByActivityName(String activityName)
	public static final String ENTITY_ID_FIELD_NAME = "entityId";
	public static final String START_TIME_FIELD_NAME = "startTime";
	public static final String END_TIME_FIELD_NAME = "endTime";
	public static final String DATA_SOURCES_FIELD_NAME = "dataSources";
	@Indexed
	@Field(ENTITY_ID_FIELD_NAME)
	protected String entityId;
	@Indexed
	@Field(START_TIME_FIELD_NAME)
	protected Long startTime;
	@Field(END_TIME_FIELD_NAME)
	protected Long endTime;
	@Field(DATA_SOURCES_FIELD_NAME)
	private List<String> dataSources;

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long endTime) {
		this.endTime = endTime;
	}

	public List<String> getDataSources() {
		return dataSources;
	}

	public void setDataSources(List<String> dataSources) {
		this.dataSources = dataSources;
	}

	public abstract Map<String, Double> getHistogram();
}
