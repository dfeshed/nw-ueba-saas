package fortscale.utils.qradar.result;

import fortscale.utils.qradar.requests.GenericRequest;
import fortscale.utils.qradar.requests.SearchResultRequest;
import fortscale.utils.qradar.responses.SearchResponse;
import fortscale.utils.qradar.utility.QRadarAPIUtility;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

/**
 * Created by tomerd on 16/03/2016.
 */
public class SearchResultRequestReader {

	private int batchSize;
	private int currentPosition = 0;
	private SearchResponse sr;
	String hostname;
	String token;

	public SearchResultRequestReader(SearchResponse sr, String hostname, String token, int batchSize) {
		this.sr = sr;
		this.hostname = hostname;
		this.token = token;
		this.batchSize = batchSize;

		// If batch size equal -1,
		if (batchSize == -1) {
			this.batchSize = sr.getRecord_count();
		}
	}

	public String getNextBatch() throws IOException {
		if (currentPosition >= sr.getRecord_count()) {
			return null;
		}

		GenericRequest request = new SearchResultRequest(sr.getSearch_id(), currentPosition, currentPosition + batchSize);
		currentPosition += batchSize;
		return QRadarAPIUtility.sendRequest(hostname, token, request, false);
	}
}