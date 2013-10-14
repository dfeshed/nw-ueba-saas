package fortscale.web.beans;

import fortscale.domain.analyst.Analyst;

public class AnalystBean {

	private Analyst analyst;
	
	public AnalystBean(Analyst analyst) {
		this.analyst = analyst;
	}
	
	public String getFirstName() {
		return analyst.getFirstName();
	}
	public String getLastName() {
		return analyst.getLastName();
	}
	public String getUserName() {
		return analyst.getUserName();
	}
	public String getEmailAddress() {
		return analyst.getEmailAddress().toString();
	}
}
