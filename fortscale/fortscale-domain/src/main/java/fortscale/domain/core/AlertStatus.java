package fortscale.domain.core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent status of evidence/alert
 * Date: 6/22/2015.
 */
public enum AlertStatus {
	Open ("OPEN", "Reviewed"),
	Closed("CLOSED", "Unreviewed");

	private String upperCaseValue;
	private String prettyValue;

	private AlertStatus(String upperCaseValue, String prettyValue) {

		this.upperCaseValue = upperCaseValue;
		this.prettyValue = prettyValue;
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

	public String getPrettyValue(){
		return this.prettyValue;
	}
}
