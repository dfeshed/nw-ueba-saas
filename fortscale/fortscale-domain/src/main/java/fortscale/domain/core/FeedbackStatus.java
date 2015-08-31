package fortscale.domain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent analyst feedback
 * Date: 8/30/2015.
 */
public enum FeedbackStatus {
	None ("NONE"),
	Approved ("APPROVED"),
	Rejected("REJECTED");

	private String upperCaseValue;

	private FeedbackStatus(String upperCaseValue) {
		this.upperCaseValue = upperCaseValue;
	}

	public static List<String> getUpperCaseValues(){
		List<String> upperCaseValues = new ArrayList<>();
		for (FeedbackStatus status : FeedbackStatus.values()){
			upperCaseValues.add(status.upperCaseValue);
		}
		return upperCaseValues;
	}
	public static FeedbackStatus getByStringCaseInsensitive(String value){
		for (FeedbackStatus status : FeedbackStatus.values()){
			if (status.upperCaseValue.equals(value.toUpperCase())){
				return status;
			}
		}
		return null;
	}
}
