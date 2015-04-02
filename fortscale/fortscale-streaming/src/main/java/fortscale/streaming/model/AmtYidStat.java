package fortscale.streaming.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * Time stats for each handled YID in an AMT session.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY, getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE)
public class AmtYidStat {
	private long startTime;
	private long endTime;

	@JsonCreator
	public AmtYidStat(@JsonProperty("startTime") long startTime) {
		this.startTime = startTime;
		this.endTime = startTime;
	}

	public long getEndTime() {
		return this.endTime;
	}

	public void updateEndTime(long endTime) {
		if (this.endTime < endTime)
			this.endTime = endTime;
	}

	public long getDurationMillis() {
		return Math.max(1000, (this.endTime - this.startTime) * 1000);
	}
}
