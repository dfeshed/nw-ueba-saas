package fortscale.domain.core;

import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Supporting information for evidence
 * Date: 7/2/2015.
 */
public class EvidenceSupportingInformation {

	// The 3 top events
	public static final String top3eventsField = "top3eventsJsonStr";

	@Field(top3eventsField)
	private String top3eventsJsonStr;


	//- Getters & Setters

	public String getTop3eventsJsonStr() {
		return top3eventsJsonStr;
	}

	public void setTop3eventsJsonStr(String top3eventsJsonStr) {
		this.top3eventsJsonStr = top3eventsJsonStr;
	}
}
