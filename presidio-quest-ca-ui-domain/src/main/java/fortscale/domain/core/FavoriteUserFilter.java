package fortscale.domain.core;

import fortscale.domain.rest.UserFilter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * Created by alexp on 16/08/2016.
 */
@Document(collection=FavoriteUserFilter.COLLECTION_NAME)
public class FavoriteUserFilter extends AbstractDocument {

	public static final String COLLECTION_NAME = "favorite_user_filter";
	public static final String filterNameField = "filterName";
	private static final String filterField = "filter";
	private static final String dateCreatedField = "dateCreated";

	@Field(filterNameField)
	@Indexed(unique = true)
	private String filterName;

	@Field(filterField)
	private UserFilter filter;

	@Field(dateCreatedField)
	private long dateCreated;

	public String getFilterName() {
		return filterName;
	}

	public void setFilterName(String filterName) {
		this.filterName = filterName;
	}

	public long getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(long dateCreated) {
		this.dateCreated = dateCreated;
	}

	public UserFilter getFilter() {
		return filter;
	}

	public void setFilter(UserFilter filter) {
		this.filter = filter;
	}
}
