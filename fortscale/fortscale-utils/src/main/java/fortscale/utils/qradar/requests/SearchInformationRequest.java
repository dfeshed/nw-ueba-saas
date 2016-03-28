package fortscale.utils.qradar.requests;

import fortscale.utils.qradar.QRadarAPI;

/**
 * Created by Amir Keren on 2/29/16.
 */
public class SearchInformationRequest extends GenericRequest {

	public SearchInformationRequest(String searchId) {
		super(searchId);
		this.requestType = QRadarAPI.RequestType.search_information;
	}

}