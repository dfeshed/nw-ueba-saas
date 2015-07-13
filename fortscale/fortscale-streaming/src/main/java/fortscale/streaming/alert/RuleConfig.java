package fortscale.streaming.alert;

/**
 * Created by danal on 12/07/2015.
 */
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
}
