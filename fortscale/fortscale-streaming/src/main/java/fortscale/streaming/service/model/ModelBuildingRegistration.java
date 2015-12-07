package fortscale.streaming.service.model;

import java.util.Date;

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
