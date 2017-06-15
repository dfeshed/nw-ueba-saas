package fortscale.utils.properties;

public class IllegalStructuredProperty extends Exception {
	private static final long serialVersionUID = -6704002773138843751L;

	public IllegalStructuredProperty(String property) {
		super(String.format("the property %s has illegal sturcture", property));
	}
}
