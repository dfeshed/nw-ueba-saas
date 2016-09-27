package fortscale.domain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent analyst feedback
 * Date: 8/30/2015.
 */
public enum AlertFeedback {
	None ("NONE", "No Feedback"),
	Approved ("APPROVED", "Actual Risk"),
	Rejected("REJECTED", "No Risk");

	private String upperCaseValue;
	private String prettyValue;

	private AlertFeedback(String upperCaseValue, String prettyValue) {

		this.upperCaseValue = upperCaseValue;
		this.prettyValue = prettyValue;
	}

	public static List<String> getUpperCaseValues(){
		List<String> upperCaseValues = new ArrayList<>();
		for (AlertFeedback status : AlertFeedback.values()){
			upperCaseValues.add(status.upperCaseValue);
		}
		return upperCaseValues;
	}
	public static AlertFeedback getByStringCaseInsensitive(String value){
		for (AlertFeedback status : AlertFeedback.values()){
			if (status.upperCaseValue.equals(value.toUpperCase())){
				return status;
			}
		}
		return null;
	}

	public String getPrettyValue(){
		return this.prettyValue;
	}
}
