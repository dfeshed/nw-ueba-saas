package fortscale.domain.core;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;
import java.util.Map;

//Todo: when you subclass this make sure that you add the new document class to UserActivityDocumentFactory.getInstanceByActivityName(String activityName)
public abstract class UserActivityDocument extends AbstractAuditableDocument {
	public static final String USER_NAME_FIELD_NAME = "normalizedUsername";
	public static final String START_TIME_FIELD_NAME = "startTime";
	public static final String END_TIME_FIELD_NAME = "endTime";
	public static final String DATA_SOURCES_FIELD_NAME = "dataSources";
	@Indexed
	@Field(USER_NAME_FIELD_NAME)
	protected String normalizedUsername;
	@Indexed
	@Field(START_TIME_FIELD_NAME)
	protected Long startTime;
	@Field(END_TIME_FIELD_NAME)
	protected Long endTime;
	@Field(DATA_SOURCES_FIELD_NAME)
	private List<String> dataSources;

	public String getNormalizedUsername() {
		return normalizedUsername;
	}

	public void setNormalizedUsername(String normalizedUsername) {
		this.normalizedUsername = normalizedUsername;
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

	public abstract Map<String, Integer> getHistogram();
}
