package fortscale.streaming.service.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

import java.util.Date;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelBuildingRegistration {
	private final String sessionId;
	private final String modelConfName;
	private Date previousEndTime;
	private Date currentEndTime;

	public ModelBuildingRegistration(String sessionId, String modelConfName, Date previousEndTime, Date currentEndTime) {
		this.sessionId = sessionId;
		this.modelConfName = modelConfName;
		this.previousEndTime = previousEndTime;
		this.currentEndTime = currentEndTime;
	}

	public String getSessionId() {
		return sessionId;
	}

	public String getModelConfName() {
		return modelConfName;
	}

	public Date getPreviousEndTime() {
		return previousEndTime;
	}

	public void setPreviousEndTime(Date previousEndTime) {
		this.previousEndTime = previousEndTime;
	}

	public Date getCurrentEndTime() {
		return currentEndTime;
	}

	public void setCurrentEndTime(Date currentEndTime) {
		this.currentEndTime = currentEndTime;
	}
}
