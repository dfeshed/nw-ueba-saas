package fortscale.web.beans;

import java.util.Set;

/**
 * Created by Amir Keren on 05/09/2016.
 */
public class AlertTypesCountBean {

    private Set<String> alertTypes;
    private int count;

    public AlertTypesCountBean(Set<String> alertTypes, int count) {
        this.alertTypes = alertTypes;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

	public Set<String> getAlertTypes() {
		return alertTypes;
	}

	public void setAlertTypes(Set<String> alertTypes) {
		this.alertTypes = alertTypes;
	}

}