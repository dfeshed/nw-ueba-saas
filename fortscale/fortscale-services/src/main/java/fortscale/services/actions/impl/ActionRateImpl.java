package fortscale.services.actions.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

@Service("ActionRateImpl")
public class ActionRateImpl  {
	

	public List<Map<String, Object>> rateTable(List<Map<String, Object>> inputMap, String rateField, String interval) {
		List<Map<String, Object>> ret = new ArrayList<Map<String,Object>>();
		
		Map<String, Integer> eventsDistribution = new HashMap<String,Integer>();
		for (Map<String,Object> eventMap : inputMap) {
			String bucket = eventMap.get(rateField).toString().split(" ")[0];
			int newCounter = eventsDistribution.containsKey(bucket) ? eventsDistribution.get(bucket) + 1 : 1 ;
			eventsDistribution.put(bucket, newCounter);
		}
		
		for (Entry<String,Integer> entry : eventsDistribution.entrySet()) {
			Map<String, Object> outputMap = new HashMap<String, Object>();
			outputMap.put("Date", entry.getKey());
			outputMap.put("Events Count", entry.getValue());
			ret.add(outputMap);
		}
		
		return ret;
	}

}
