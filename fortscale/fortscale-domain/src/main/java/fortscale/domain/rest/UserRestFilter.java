package fortscale.domain.rest;

import java.util.List;

/**
 * Created by alexp on 18/08/2016.
 */
public class UserRestFilter extends UserFilter {

	private String sortField;
	private String sortDirection;
	private String searchValue;
	private List<String> userIds;

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

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

	public List<String> getUserIds() {
		return userIds;
	}

	public void setUserIds(List<String> userIds) {
		this.userIds = userIds;
	}


}
