package fortscale.utils.qradar.requests;

import fortscale.utils.qradar.QRadarAPI;

/**
 * Created by Amir Keren on 2/29/16.
 */
public class SearchResultRequest extends GenericRequest {

	private int rangeStart;
	private int rangeEnd;

	public SearchResultRequest(String searchId, int rangeStart, int rangeEnd) {
		super(searchId);
		this.rangeStart = rangeStart;
		this.rangeEnd = rangeEnd;
		this.requestType = QRadarAPI.RequestType.search_result;
	}

	public int getRangeStart() {
		return rangeStart;
	}

	public void setRangeStart(int rangeStart) {
		this.rangeStart = rangeStart;
	}

	public int getRangeEnd() {
		return rangeEnd;
	}

	public void setRangeEnd(int rangeEnd) {
		this.rangeEnd = rangeEnd;
	}

}