package fortscale.domain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent analyst feedback
 * Date: 8/30/2015.
 */
public enum AlertFeedback {
	None ("NONE"),
	Approved ("APPROVED"),
	Rejected("REJECTED");

	private String upperCaseValue;

	private AlertFeedback(String upperCaseValue) {
		this.upperCaseValue = upperCaseValue;
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
}
