package fortscale.domain.email;

import java.util.List;

/**
 * Created by Amir Keren on 17/01/16.
 */
public class NewAlert {

	private List<String> severities;

	public List<String> getSeverities() {
		return severities;
	}

	public void setSeverities(List<String> severities) {
		this.severities = severities;
	}

}