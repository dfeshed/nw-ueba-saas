package fortscale.utils.qradar.result;

import fortscale.utils.qradar.requests.GenericRequest;
import fortscale.utils.qradar.requests.SearchResultRequest;
import fortscale.utils.qradar.responses.SearchResponse;
import fortscale.utils.qradar.utility.QRadarAPIUtility;

import java.io.IOException;

/**
 * Created by tomerd on 16/03/2016.
 */
public class SearchResultRequestReader {

	private int batchSize;
	private int currentPosition;
	private SearchResponse sr;
	private String hostname;
	private String token;
	private int maxNumberOfRetries;
	private long sleepInMilliseconds;

	public SearchResultRequestReader(SearchResponse sr, String hostname, String token, int batchSize,
			int maxNumberOfRetries, long sleepInMilliseconds) {
		this.sr = sr;
		this.hostname = hostname;
		this.token = token;
		this.batchSize = batchSize;
		this.currentPosition = 0;
		this.maxNumberOfRetries = maxNumberOfRetries;
		this.sleepInMilliseconds = sleepInMilliseconds;

		// If batch size equal -1,
		if (batchSize == -1) {
			this.batchSize = sr.getRecord_count();
		}
	}

	public String getNextBatch() throws IOException, InterruptedException {

		// If reached the end of results, return null
		if (currentPosition >= sr.getRecord_count()) {
			return null;
		}

		// Retry variables
		boolean isRequestSuccessful = false;
		int retryNumber = 1;
		String result = "";

		// Create request object
		GenericRequest request = new SearchResultRequest(sr.getSearch_id(), currentPosition, currentPosition + batchSize);

		// Send request
		while (!isRequestSuccessful && maxNumberOfRetries > retryNumber) {
			result = QRadarAPIUtility.sendRequest(hostname, token, request, false);

			// If a response was received, finish sending.
			if (result != null && !result.equals("")) {
				isRequestSuccessful = true;
			} else {
				retryNumber++;
				Thread.sleep(sleepInMilliseconds);
			}
		}

		// If the sending was not successful, abort job
		if (!isRequestSuccessful) {
			throw new RuntimeException("Reach maximum number of sending; Aborting job");
		}

		// If the request was successful, increment position and return the response
		currentPosition += batchSize + 1;
		return result;
	}
}