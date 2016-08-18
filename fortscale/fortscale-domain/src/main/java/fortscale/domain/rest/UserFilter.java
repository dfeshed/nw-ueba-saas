package fortscale.domain.rest;

import fortscale.domain.core.DataSourceAnomalyTypePair;

import java.util.List;
import java.util.Set;

public class UserFilter extends RestFilter {


	private String disabledSince;
	private Boolean isDisabled;
	private Boolean isDisabledWithActivity;
	private Boolean isTerminatedWithActivity;
	private String inactiveSince;
	private String dataEntities;
	private Integer entityMinScore;
	private Boolean isServiceAccount;
	private String searchFieldContains;
	private Boolean addAlertsAndDevices;
	private List<String> userTags;
	private Boolean isWatched;
	private Boolean isScored;
	private List<String> alertTypes;
	private AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes;
	private List<String> locations;

	public String getDisabledSince() {
		return disabledSince;
	}

	public void setDisabledSince(String disabledSince) {
		this.disabledSince = disabledSince;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setIsDisabled(Boolean disabled) {
		isDisabled = disabled;
	}

	public Boolean getIsDisabledWithActivity() {
		return isDisabledWithActivity;
	}

	public void setIsDisabledWithActivity(Boolean disabledWithActivity) {
		isDisabledWithActivity = disabledWithActivity;
	}

	public String getInactiveSince() {
		return inactiveSince;
	}

	public void setInactiveSince(String inactiveSince) {
		this.inactiveSince = inactiveSince;
	}

	public String getDataEntities() {
		return dataEntities;
	}

	public void setDataEntities(String dataEntities) {
		this.dataEntities = dataEntities;
	}

	public Integer getEntityMinScore() {
		return entityMinScore;
	}

	public void setEntityMinScore(Integer entityMinScore) {
		this.entityMinScore = entityMinScore;
	}

	public Boolean getIsServiceAccount() {
		return isServiceAccount;
	}

	public void setIsServiceAccount(Boolean serviceAccount) {
		isServiceAccount = serviceAccount;
	}

	public String getSearchFieldContains() {
		return searchFieldContains;
	}

	public void setSearchFieldContains(String searchFieldContains) {
		this.searchFieldContains = searchFieldContains;
	}

	public Boolean getAddAlertsAndDevices() {
		return addAlertsAndDevices;
	}

	public void setAddAlertsAndDevices(Boolean addAlertsAndDevices) {
		this.addAlertsAndDevices = addAlertsAndDevices;
	}

	public List<String> getUserTags() {
		return userTags;
	}

	public void setUserTags(List<String> userTags) {
		this.userTags = userTags;
	}

	public Boolean getIsWatched() {
		return isWatched;
	}

	public void setIsWatched(Boolean isWatched) {
		this.isWatched = isWatched;
	}

	public Boolean getIsScored() {
		return isScored;
	}

	public void setIsScored(Boolean isScored) {
		this.isScored = isScored;
	}

	public Boolean getIsTerminatedWithActivity() {
		return isTerminatedWithActivity;
	}

	public void setIsTerminatedWithActivity(Boolean terminatedWithActivity) {
		isTerminatedWithActivity = terminatedWithActivity;
	}

	public List<String> getAlertTypes() {
		return alertTypes;
	}

	public void setAlertTypes(List<String> alertTypes) {
		this.alertTypes = alertTypes;
	}

	public AlertRestFilter.DataSourceAnomalyTypePairListWrapper getIndicatorTypes() {
		return indicatorTypes;
	}

	public void setIndicatorTypes(AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes) {
		this.indicatorTypes = indicatorTypes;
	}

	public List<String> getLocations() {
		return locations;
	}

	public void setLocations(List<String> locations) {
		this.locations = locations;
	}

	public Set<DataSourceAnomalyTypePair> getAnomalyTypesAsSet() {
		if (indicatorTypes == null){
			return null;
		} else {
			return indicatorTypes.getAnomalyList();
		}
	}
}
