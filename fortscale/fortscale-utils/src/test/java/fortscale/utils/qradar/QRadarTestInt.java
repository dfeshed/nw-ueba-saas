package fortscale.utils.qradar;

import fortscale.utils.qradar.result.SearchResultRequestReader;
import fortscale.utils.test.category.SplunkTestCategory;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(SplunkTestCategory.class)
public class QRadarTestInt {

	private String hostName = "fs-qradar-01";
	private String token = "5252ef69-1946-4731-8e61-20c271023ab7";
	private String savedQuery = "select StartTime,%s from events where EventID=4769 order by StartTime asc START \\'%s\\' STOP\\'%s\\'";
	private String returnKeys = "EventID,AccountDomain,AccountName,sourceip,ServiceName,ServiceID,TicketOptions,FailureCode";
	private int batchSize = 1000;
	private int numberOfRetries = 5;
	private long sleepTime = 1000;

	@Test
	@Ignore
	public void testQRadarQuery() {
		QRadarAPI qRadarAPI = new QRadarAPI(hostName, token);
		String earliest = "1457965426";
		String latest = "1458051826";
		try {
			SearchResultRequestReader reader = qRadarAPI.runQuery(savedQuery, returnKeys, earliest, latest, batchSize,
					numberOfRetries, sleepTime);
			StringBuilder result = new StringBuilder();
			String queryResults = reader.getNextBatch();
			while (queryResults != null) {
				result.append(queryResults);
				queryResults = reader.getNextBatch();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}