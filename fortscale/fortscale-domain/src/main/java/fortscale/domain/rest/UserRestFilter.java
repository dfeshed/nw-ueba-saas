package fortscale.domain.rest;

/**
 * Created by alexp on 18/08/2016.
 */
public class UserRestFilter extends UserFilter {

	private String sortField;
	private String sortDirection;

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


}
