package fortscale.domain.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fortscale.domain.core.DataSourceAnomalyTypePair;
import fortscale.domain.core.Severity;

import java.util.List;
import java.util.Objects;
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

		return (   Objects.equals(disabledSince, that.disabledSince)
				|| Objects.equals(isDisabled, that.isDisabled)
				|| Objects.equals(isDisabledWithActivity, that.isDisabledWithActivity)
				|| Objects.equals(isTerminatedWithActivity, that.isTerminatedWithActivity)
				|| Objects.equals(inactiveSince, that.inactiveSince)
				|| Objects.equals(dataEntities, that.dataEntities)
				|| Objects.equals(entityMinScore, that.entityMinScore)
				|| Objects.equals(isServiceAccount, that.isServiceAccount)
				|| Objects.equals(searchFieldContains, that.searchFieldContains)
				|| Objects.equals(addAlertsAndDevices, that.addAlertsAndDevices)
				|| Objects.equals(userTags, that.userTags)
				|| Objects.equals(isWatched, that.isWatched)
				|| Objects.equals(alertTypes, that.alertTypes)
				|| Objects.equals(indicatorTypes, that.indicatorTypes)
				|| Objects.equals(locations, that.locations)
				|| Objects.equals(severity, that.severity)
				|| Objects.equals(minScore, that.minScore)
				|| Objects.equals(maxScore, that.maxScore)
				);
	}

	@Override
	public int hashCode() {

		return Objects.hash(disabledSince, isDisabled, isDisabledWithActivity, isTerminatedWithActivity, inactiveSince,
				dataEntities, entityMinScore, isServiceAccount, searchFieldContains,  userTags, isWatched, alertTypes,
				indicatorTypes, locations, severity, minScore, maxScore);
	}
}