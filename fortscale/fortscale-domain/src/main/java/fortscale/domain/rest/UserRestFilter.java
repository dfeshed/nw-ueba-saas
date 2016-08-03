package fortscale.domain.rest;

public class UserRestFilter extends RestFilter {

	private String sortField;
	private String sortDirection;
	private Integer size;
	private Integer fromPage;
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

	public String getSortField() {
		return sortField;
	}

	public void setSortField(String sortField) {
		this.sortField = sortField;
	}

	public String getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(String sortDirection) {
		this.sortDirection = sortDirection;
	}

	@Override public Integer getSize() {
		return size;
	}

	@Override public void setSize(Integer size) {
		this.size = size;
	}

	@Override public Integer getFromPage() {
		return fromPage;
	}

	@Override public void setFromPage(Integer fromPage) {
		this.fromPage = fromPage;
	}

	public String getDisabledSince() {
		return disabledSince;
	}

	public void setDisabledSince(String disabledSince) {
		this.disabledSince = disabledSince;
	}

	public Boolean getIsDisabled() {
		return isDisabled;
	}

	public void setDisabled(Boolean disabled) {
		isDisabled = disabled;
	}

	public Boolean getIsDisabledWithActivity() {
		return isDisabledWithActivity;
	}

	public void setDisabledWithActivity(Boolean disabledWithActivity) {
		isDisabledWithActivity = disabledWithActivity;
	}

	public Boolean getIsTerminatedWithActivity() {
		return isTerminatedWithActivity;
	}

	public void setTerminatedWithActivity(Boolean terminatedWithActivity) {
		isTerminatedWithActivity = terminatedWithActivity;
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

	public void setServiceAccount(Boolean serviceAccount) {
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

}
