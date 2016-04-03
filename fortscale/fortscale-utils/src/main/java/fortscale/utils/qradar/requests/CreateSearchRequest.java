package fortscale.utils.qradar.requests;

import fortscale.utils.qradar.QRadarAPI;

/**
 * Created by Amir Keren on 2/29/16.
 */
public class CreateSearchRequest extends GenericRequest {

	private String query;

	public CreateSearchRequest(String query) {
		super("");
		this.query = query;
		this.requestType = QRadarAPI.RequestType.create_search;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

}