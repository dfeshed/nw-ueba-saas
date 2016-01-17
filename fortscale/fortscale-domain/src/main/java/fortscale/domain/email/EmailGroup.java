package fortscale.domain.email;

/**
 * Created by Amir Keren on 17/01/16.
 */
public class EmailGroup {

	private String[] users;
	private AlertSummary summary;
	private NewAlert newAlert;

	public String[] getUsers() {
		return users;
	}

	public void setUsers(String[] users) {
		this.users = users;
	}

	public AlertSummary getSummary() {
		return summary;
	}

	public void setSummary(AlertSummary summary) {
		this.summary = summary;
	}

	public NewAlert getNewAlert() {
		return newAlert;
	}

	public void setNewAlert(NewAlert newAlert) {
		this.newAlert = newAlert;
	}

}