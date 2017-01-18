package fortscale.utils.qradar;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.utils.logging.Logger;
import fortscale.utils.qradar.requests.CreateSearchRequest;
import fortscale.utils.qradar.requests.GenericRequest;
import fortscale.utils.qradar.requests.SearchInformationRequest;
import fortscale.utils.qradar.responses.SearchResponse;
import fortscale.utils.qradar.result.SearchResultRequestReader;
import fortscale.utils.qradar.utility.QRadarAPIUtility;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Amir Keren on 2/29/16.
 */
public class QRadarAPI {

	private static final Logger logger = Logger.getLogger(QRadarAPI.class);

	public enum RequestType {create_search, search_result, search_information}

	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
	private static final int SLEEP_TIME = 1000;

	private String hostname;
	private String token;
	private ObjectMapper objectMapper;

	public QRadarAPI(String hostname, String token) {
		this.hostname = hostname;
		this.token = token;
		this.objectMapper = new ObjectMapper();
	}

	public SearchResultRequestReader runQuery(String savedSearch, String returnKeys, String startTime, String endTime,
			int batchSize, int maxNumberOfRetries, long sleepInMilliseconds) throws Exception {
		// Convert time parameters to QRadar format
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		String start = sdf.format(new Date(TimestampUtils.convertToMilliSeconds(Long.parseLong(startTime))));
		String end = sdf.format(new Date(TimestampUtils.convertToMilliSeconds(Long.parseLong(endTime))));
		// Create QRadar query
		String query = String.format(savedSearch, returnKeys, start, end);
		try {
			GenericRequest request = new CreateSearchRequest(query);
			String response = QRadarAPIUtility.sendRequest(hostname, token, request, true, maxNumberOfRetries,
						sleepInMilliseconds);
			SearchResponse sr = objectMapper.readValue(response.toString(), SearchResponse.class);
			while (sr.getStatus() != SearchResponse.Status.COMPLETED) {
				Thread.sleep(SLEEP_TIME);
				request = new SearchInformationRequest(sr.getSearch_id());
				response = QRadarAPIUtility.sendRequest(hostname, token, request, true, maxNumberOfRetries,
						sleepInMilliseconds);
				sr = objectMapper.readValue(response.toString(), SearchResponse.class);
			}
			return new SearchResultRequestReader(sr, hostname, token, batchSize, maxNumberOfRetries,
					sleepInMilliseconds);
		} catch (Exception ex) {
			logger.error("error sending request - ", ex);
		}
		return null;
	}

	public String canConnect() throws IOException, InterruptedException {
		GenericRequest request = new CreateSearchRequest("select * from events limit 1");
		try {
			String response = QRadarAPIUtility.sendRequest(hostname, token, request, true, 1, 0);
			if (StringUtils.isBlank(response)) {
				return "Failed to connect, possibly incorrect token";
			}
			SearchResponse sr = objectMapper.readValue(response, SearchResponse.class);
			while (sr.getStatus() != SearchResponse.Status.COMPLETED) {
				Thread.sleep(100);
				request = new SearchInformationRequest(sr.getSearch_id());
				response = QRadarAPIUtility.sendRequest(hostname, token, request, true, 1, 0);
				sr = objectMapper.readValue(response, SearchResponse.class);
			}
		} catch (Exception ex) {
			logger.info("failed to connect to {}", this, ex);
			throw ex;
		}
		return "";
	}

}