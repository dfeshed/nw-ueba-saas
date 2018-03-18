package fortscale.domain.core;


import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent status of evidence/alert
 * Date: 6/22/2015.
 */
public enum Severity {
	Critical("CRITICAL"),
	High("HIGH"),
	Medium("MEDIUM"),
	Low("LOW");


	private String upperCaseValue;

	private Severity(String upperCaseValue) {
		this.upperCaseValue = upperCaseValue;
	}

	public static List<String> getUpperCaseValues(){
		List<String> upperCaseValues = new ArrayList<>();
		for (Severity severity : Severity.values()){
			upperCaseValues.add(severity.upperCaseValue);
		}
		return upperCaseValues;
	}

	@JsonCreator
	public static Severity getByStringCaseInsensitive(String value){
		for (Severity severity : Severity.values()){
			if (severity.upperCaseValue.equals(value.toUpperCase())){
				return severity;
			}
		}
		return null;
	}
}
