package fortscale.domain.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.Severity;

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
	private Boolean addAllWatched;
	private List<String> userTags;
	private Boolean isWatched;
	private List<String> alertTypes;
	private AlertRestFilter.DataSourceAnomalyTypePairListWrapper indicatorTypes;
	private List<String> locations;
	private Severity severity;
	private Double minScore;
	private Double maxScore;

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

	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		this.severity = severity;
	}

	public Double getMinScore() {
		return minScore;
	}

	public void setMinScore(Double minScore) {
		this.minScore = minScore;
	}

	public Double getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Double maxScore) {
		this.maxScore = maxScore;
	}

	@JsonIgnore
	public Set<DataSourceAnomalyTypePair> getAnomalyTypesAsSet() {
		if (indicatorTypes == null){
			return null;
		} else {
			return indicatorTypes.getAnomalyList();
		}
	}

	public Boolean getAddAllWatched() {
		return addAllWatched;
	}

	public void setAddAllWatched(Boolean addAllWatched) {
		this.addAllWatched = addAllWatched;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		UserFilter that = (UserFilter) o;

		if (disabledSince != null ? !disabledSince.equals(that.disabledSince) : that.disabledSince != null)
			return false;
		if (isDisabled != null ? !isDisabled.equals(that.isDisabled) : that.isDisabled != null) return false;
		if (isDisabledWithActivity != null ? !isDisabledWithActivity.equals(that.isDisabledWithActivity) : that.isDisabledWithActivity != null)
			return false;
		if (isTerminatedWithActivity != null ? !isTerminatedWithActivity.equals(that.isTerminatedWithActivity) : that.isTerminatedWithActivity != null)
			return false;
		if (inactiveSince != null ? !inactiveSince.equals(that.inactiveSince) : that.inactiveSince != null)
			return false;
		if (dataEntities != null ? !dataEntities.equals(that.dataEntities) : that.dataEntities != null) return false;
		if (entityMinScore != null ? !entityMinScore.equals(that.entityMinScore) : that.entityMinScore != null)
			return false;
		if (isServiceAccount != null ? !isServiceAccount.equals(that.isServiceAccount) : that.isServiceAccount != null)
			return false;
		if (searchFieldContains != null ? !searchFieldContains.equals(that.searchFieldContains) : that.searchFieldContains != null)
			return false;
		if (addAlertsAndDevices != null ? !addAlertsAndDevices.equals(that.addAlertsAndDevices) : that.addAlertsAndDevices != null)
			return false;
		if (userTags != null ? !userTags.equals(that.userTags) : that.userTags != null) return false;
		if (isWatched != null ? !isWatched.equals(that.isWatched) : that.isWatched != null) return false;
		if (alertTypes != null ? !alertTypes.equals(that.alertTypes) : that.alertTypes != null) return false;
		if (indicatorTypes != null ? !indicatorTypes.equals(that.indicatorTypes) : that.indicatorTypes != null)
			return false;
		if (locations != null ? !locations.equals(that.locations) : that.locations != null) return false;
		if (severity != that.severity) return false;
		if (minScore != null ? !minScore.equals(that.minScore) : that.minScore != null) return false;

		return maxScore != null ? maxScore.equals(that.maxScore) : that.maxScore == null;

	}

	@Override
	public int hashCode() {
		int result = disabledSince != null ? disabledSince.hashCode() : 0;
		result = 31 * result + (isDisabled != null ? isDisabled.hashCode() : 0);
		result = 31 * result + (isDisabledWithActivity != null ? isDisabledWithActivity.hashCode() : 0);
		result = 31 * result + (isTerminatedWithActivity != null ? isTerminatedWithActivity.hashCode() : 0);
		result = 31 * result + (inactiveSince != null ? inactiveSince.hashCode() : 0);
		result = 31 * result + (dataEntities != null ? dataEntities.hashCode() : 0);
		result = 31 * result + (entityMinScore != null ? entityMinScore.hashCode() : 0);
		result = 31 * result + (isServiceAccount != null ? isServiceAccount.hashCode() : 0);
		result = 31 * result + (searchFieldContains != null ? searchFieldContains.hashCode() : 0);
		result = 31 * result + (addAlertsAndDevices != null ? addAlertsAndDevices.hashCode() : 0);
		result = 31 * result + (userTags != null ? userTags.hashCode() : 0);
		result = 31 * result + (isWatched != null ? isWatched.hashCode() : 0);
		result = 31 * result + (alertTypes != null ? alertTypes.hashCode() : 0);
		result = 31 * result + (indicatorTypes != null ? indicatorTypes.hashCode() : 0);
		result = 31 * result + (locations != null ? locations.hashCode() : 0);
		result = 31 * result + (severity != null ? severity.hashCode() : 0);
		result = 31 * result + (minScore != null ? minScore.hashCode() : 0);
		result = 31 * result + (maxScore != null ? maxScore.hashCode() : 0);
		return result;
	}
}