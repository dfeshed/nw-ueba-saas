package fortscale.utils;

/**
 * Created by tomerd on 18/09/2015.
 */
public class CustomedFilter {

	private String key;
	private String value;
	private String operator;

	public String getKey() {
		return key;
	}

	public String getValue() {
		return value;
	}

	public String getOperator() {
		return operator;
	}

	public CustomedFilter(String key, String operator, String value) {
		this.key = key;
		this.value = value;
		this.operator = operator;
	}
}
