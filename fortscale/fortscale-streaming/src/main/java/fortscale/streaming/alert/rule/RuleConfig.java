package fortscale.streaming.alert.rule;

/**
 * Created by danal on 04/08/2015.
 */
// inner class for holding rule configuration
public class RuleConfig {

	private String name;
	private String statement;
	private boolean autoCreate;
	private String subscriberBeanName;

	public RuleConfig(String name, String statement, boolean autoCreate, String subscriberBeanName) {
		this.name = name;
		this.statement = statement;
		this.autoCreate = autoCreate;
		this.subscriberBeanName = subscriberBeanName;
	}

	public RuleConfig(RuleConfig other) {
		this.name = other.name;
		this.statement = other.statement;
		this.autoCreate = other.autoCreate;
		this.subscriberBeanName = other.subscriberBeanName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatement() {
		return statement;
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public String getSubscriberBeanName() {
		return subscriberBeanName;
	}

	public void setSubscriberBeanName(String subscriberBeanName) {
		this.subscriberBeanName = subscriberBeanName;
	}

	public boolean isAutoCreate() {
		return autoCreate;
	}

	public void setAutoCreate(boolean autoCreate) {
		this.autoCreate = autoCreate;
	}

	@Override public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		RuleConfig that = (RuleConfig) o;

		if (autoCreate != that.autoCreate)
			return false;
		if (name != null ? !name.equals(that.name) : that.name != null)
			return false;
		if (statement != null ? !statement.equals(that.statement) : that.statement != null)
			return false;
		if (subscriberBeanName != null ? !subscriberBeanName.equals(that.subscriberBeanName) : that.subscriberBeanName != null)
			return false;

		return true;
	}

	@Override public int hashCode() {
		int result = name != null ? name.hashCode() : 0;
		result = 31 * result + (statement != null ? statement.hashCode() : 0);
		result = 31 * result + (autoCreate ? 1 : 0);
		result = 31 * result + (subscriberBeanName != null ? subscriberBeanName.hashCode() : 0);
		return result;
	}
}
