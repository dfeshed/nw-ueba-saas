package fortscale.utils.qradar.requests;

import fortscale.utils.qradar.QRadarAPI;

/**
 * Created by Amir Keren on 2/29/16.
 */
public abstract class GenericRequest {

	protected String searchId;
	protected QRadarAPI.RequestType requestType;

	public GenericRequest(String searchId) {
		this.searchId = searchId;
	}

	public String getSearchId() {
		return searchId;
	}

	public void setSearchId(String searchId) {
		this.searchId = searchId;
	}

	public QRadarAPI.RequestType getRequestType() { return requestType; }

}