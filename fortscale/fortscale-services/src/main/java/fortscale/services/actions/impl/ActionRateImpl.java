package fortscale.services.actions.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import fortscale.utils.logging.Logger;

@Service("ActionRateImpl")
public class ActionRateImpl  {
	
	private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.0" ;
	private static final String OUTPUT_BUCKET_DATE_FORMAT = "yyyy/MM/dd" ;
	private static final String OUTPUT_TIME_KEY_NAME  = "Date" ;
	private static final String OUTPUT_COUNT_KEY_NAME = "EventsCount" ;

	private static final Logger logger = Logger.getLogger(ActionRateImpl.class);

	public List<Map<String, Object>> rateTable(List<Map<String, Object>> inputMap, String rateField, String interval) {

		List<Map<String, Object>> ret = new ArrayList<Map<String,Object>>();
		
		Map<String, Integer> eventsDistribution = new HashMap<String,Integer>();
		for (Map<String,Object> eventMap : inputMap) {

			Date eventDate = null;
			try {
				SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
				eventDate = pattern.parse(eventMap.get(rateField).toString());
			}
			catch (ParseException e) {
				logger.error("Parse Exception occured within the Rate() action while parsing {}", eventMap.get(rateField));
				continue;
			}
			
			String bucket = null;
			if (interval.equals("1d")) {
				SimpleDateFormat datePattern = new SimpleDateFormat(OUTPUT_BUCKET_DATE_FORMAT);
				bucket = datePattern.format(eventDate);
			}
			
			int newCounter = eventsDistribution.containsKey(bucket) ? eventsDistribution.get(bucket) + 1 : 1 ;
			eventsDistribution.put(bucket, newCounter);
		}
		
		for (Entry<String,Integer> entry : eventsDistribution.entrySet()) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put(OUTPUT_TIME_KEY_NAME, entry.getKey());
			outputMap.put(OUTPUT_COUNT_KEY_NAME, entry.getValue());
			ret.add(outputMap);
		}
		
		return ret;
	}

}
