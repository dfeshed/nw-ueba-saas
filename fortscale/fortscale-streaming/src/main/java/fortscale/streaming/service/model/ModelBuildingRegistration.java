package fortscale.streaming.service.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;

@JsonAutoDetect(fieldVisibility = Visibility.ANY, getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE)
public class ModelBuildingRegistration {
	private final String sessionId;
	private final String modelConfName;
	private Date previousEndTime;
	private Date currentEndTime;
	private ModelBuildingExtraParams extraParams;

	@JsonCreator
	public ModelBuildingRegistration(
			@JsonProperty("sessionId") String sessionId,
			@JsonProperty("modelConfName") String modelConfName,
			@JsonProperty("previousEndTime") Date previousEndTime,
			@JsonProperty("currentEndTime") Date currentEndTime,
			@JsonProperty("extraParams") ModelBuildingExtraParams extraParams) {

		this.sessionId = sessionId;
		this.modelConfName = modelConfName;
		this.previousEndTime = previousEndTime;
		this.currentEndTime = currentEndTime;
		this.extraParams = extraParams;
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

	public ModelBuildingExtraParams getExtraParams() {
		return extraParams;
	}
}
