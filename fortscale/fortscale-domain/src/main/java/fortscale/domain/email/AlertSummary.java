package fortscale.domain.email;

import java.util.List;

/**
 * Created by Amir Keren on 17/01/16.
 */
public class AlertSummary {

	private List<String> severities;
	private List<EmailFrequency> frequencies;

	public List<String> getSeverities() {
		return severities;
	}

	public void setSeverities(List<String> severities) {
		this.severities = severities;
	}

	public List<EmailFrequency> getFrequencies() {
		return frequencies;
	}

	public void setFrequencies(List<EmailFrequency> frequencies) {
		this.frequencies = frequencies;
	}

}