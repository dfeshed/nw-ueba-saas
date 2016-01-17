package fortscale.domain.email;

import java.util.List;

/**
 * Created by Amir Keren on 17/01/16.
 */
public class EmailConfiguration {

	private List<EmailGroup> emailGroups;

	public List<EmailGroup> getEmailGroups() {
		return emailGroups;
	}

	public void setEmailGroups(List<EmailGroup> emailGroups) {
		this.emailGroups = emailGroups;
	}

	public boolean shouldSendNewAlert(String severity) {
		for (EmailGroup emailGroup: emailGroups) {
			if (emailGroup.getNewAlert().getSeverities().contains(severity)) {
				return true;
			}
		}
		return false;
	}

}