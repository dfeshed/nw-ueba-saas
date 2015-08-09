package fortscale.domain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent status of evidence/alert
 * Date: 6/22/2015.
 */
public enum AlertStatus {
	Open ("OPEN"),
	Approved ("APPROVED"),
	Rejected("REJECTED"),
	Closed("CLOSED");

	private String upperCaseValue;

	private AlertStatus(String upperCaseValue) {
		this.upperCaseValue = upperCaseValue;
	}

	public static List<String> getUpperCaseValues(){
		List<String> upperCaseValues = new ArrayList<>();
		for (AlertStatus status : AlertStatus.values()){
			upperCaseValues.add(status.upperCaseValue);
		}
		return upperCaseValues;
	}
	public static AlertStatus getByStringCaseInsensitive(String value){
		for (AlertStatus status : AlertStatus.values()){
			if (status.upperCaseValue.equals(value.toUpperCase())){
				return status;
			}
		}
		return null;
	}
}
