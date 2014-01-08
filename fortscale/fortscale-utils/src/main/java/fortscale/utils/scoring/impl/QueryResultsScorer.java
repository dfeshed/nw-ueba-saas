package fortscale.utils.scoring.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fortscale.ebs.EBSPigUDF;
import fortscale.ebs.EventBulkScorer;
import fortscale.utils.logging.Logger;
import fortscale.utils.scoring.IEBSResult;
import fortscale.utils.scoring.IQueryResultsScorer;

public class QueryResultsScorer implements IQueryResultsScorer{
	private static Logger logger = Logger.getLogger(QueryResultsScorer.class);
	
	private static final String EVENT_SCORE = "eventScore";
	

	@Override
	public IEBSResult runEBSOnQueryResults(List<Map<String, Object>> resultsMap, Map<String, String> rowFieldRegexFilter, Set<String> timeFieldNameSet, Set<String> fieldNamesFilterSet, String fieldSpliter, Object fieldSpliterGlobalScoreValue){
		IEBSResult ret = null;
		if(StringUtils.isEmpty(fieldSpliter)){
			ret = runEBSOnQueryResults(resultsMap, rowFieldRegexFilter, timeFieldNameSet, fieldNamesFilterSet);
		} else{
			List<Map<String, Object>> resultsMapForGlobalScore = new ArrayList<>();
			List<Map<String, Object>> otherResultsMap = new ArrayList<>();
			for(Map<String,Object> result: resultsMap){
				Object val = result.get(fieldSpliter);
				if(val != null && val.equals(fieldSpliterGlobalScoreValue)){
					resultsMapForGlobalScore.add(result);
				} else{
					otherResultsMap.add(result);
				}
			}
			IEBSResult globalScoreEbsResult = runEBSOnQueryResults(resultsMapForGlobalScore, rowFieldRegexFilter, timeFieldNameSet, fieldNamesFilterSet);
			IEBSResult otherEbsResult = runEBSOnQueryResults(otherResultsMap, rowFieldRegexFilter, timeFieldNameSet, fieldNamesFilterSet);
			List<Map<String, Object>> resultsList = new ArrayList<>();
			if(globalScoreEbsResult.getResultsList() != null){
				resultsList.addAll(globalScoreEbsResult.getResultsList());
			}
			if(otherEbsResult.getResultsList() != null){
				resultsList.addAll(otherEbsResult.getResultsList());
			}
			Double globalScore = globalScoreEbsResult.getGlobalScore() > otherEbsResult.getGlobalScore() ? globalScoreEbsResult.getGlobalScore() : otherEbsResult.getGlobalScore();

			ret = new EBSResult(resultsList, globalScore, 0, resultsList.size());
		}
		return ret;
	}

	@Override
	public IEBSResult runEBSOnQueryResults(List<Map<String, Object>> resultsMap, Map<String, String> rowFieldRegexFilter, Set<String> timeFieldNameSet, Set<String> fieldNamesFilterSet) {
		if(resultsMap.isEmpty()){
			return new EBSResult(null,  0.d, 0, 0);
		}
		List<EventBulkScorer.InputStruct> listResults = new ArrayList<EventBulkScorer.InputStruct>((int)resultsMap.size());

		List<String> keys = new ArrayList<>();
		for(String fieldName: resultsMap.get(0).keySet()){
			if(fieldNamesFilterSet.contains(fieldName)){
				continue;
			}
			keys.add(fieldName);
		}
		for (Map<String, Object> map : resultsMap) {
			if(filterRowResults(map, rowFieldRegexFilter)){
				continue;
			}

			List<String> workingSet = new ArrayList<String>(keys.size());
			List<String> allData = new ArrayList<String>(keys.size());
			for (int i = 0; i < keys.size(); i++) {
				String fieldName = keys.get(i);
				Object tmp = map.get(fieldName);
				
				processFieldRow(tmp, fieldName, timeFieldNameSet, workingSet, allData);
			}
			
			for(String fieldName: fieldNamesFilterSet){
				processFieldRow(map.get(fieldName), fieldName, timeFieldNameSet, null, allData);
			}
			
			EventBulkScorer.InputStruct inp = new EventBulkScorer.InputStruct();
			inp.working_set = workingSet;
			inp.all_data = allData;
			listResults.add(inp);
		}

		return processEbsResults(keys, listResults, timeFieldNameSet, fieldNamesFilterSet);
	}
	
	private EBSResult processEbsResults(List<String> keys, List<EventBulkScorer.InputStruct> listResults, Set<String> timeFieldNameSet, Set<String> fieldNamesFilterSet){
		EventBulkScorer ebs = new EventBulkScorer();
		EventBulkScorer.EBSResult ebsresult = ebs.work( listResults );
				
		List<Map<String, Object>> eventResultList = new ArrayList<>();
		
		for (EventBulkScorer.EventScoreStore eventScore : ebsresult.event_score_list) {
			Map<String, Object> eventMap = new HashMap<>();
			int i=0;
			for (;i<keys.size();i++) {
				eventMap.put(keys.get(i), eventScore.event.get(i));
				eventMap.put(formatKeyScore(keys.get(i)), eventScore.explain.get(i));
			}
			for(String fieldName: fieldNamesFilterSet){
				eventMap.put(fieldName, eventScore.event.get(i));
			}
			eventMap.put(EVENT_SCORE, (double)Math.round(eventScore.score));
			eventResultList.add(eventMap);
		}
		
		return new EBSResult(eventResultList, ebsresult.global_score, 0, eventResultList.size());
	}
	
	private String formatKeyScore(String key){
		return String.format("%sscore",key);
	}
	
	
	
	private boolean filterRowResults(Map<String, Object> rowVals, Map<String, String> rowFieldRegexFilters){
		if(rowFieldRegexFilters == null || rowFieldRegexFilters.isEmpty()){
			return false;
		}
		boolean isFilter = false;
		for(Entry<String, String> entry: rowFieldRegexFilters.entrySet()){
			String val = (String)rowVals.get(entry.getKey());
			if(val != null && val.matches(entry.getValue())){
				logger.debug("filtering the event with {} ({}) by regex ({})", entry.getKey(), val, entry.getValue());
				isFilter = true;
				break;
			}
		}
		
		return isFilter;
	}
	
	private void processFieldRow(Object tmp, String fieldName, Set<String> timeFieldNameSet, List<String> workingSet, List<String> allData){
		String val = null;
		if(tmp != null) {
			val = tmp.toString();
		} else {
			logger.warn("no value returned for the column {}", fieldName);
			val ="";
		}
		allData.add(val);
		if(workingSet != null){
			if(timeFieldNameSet != null && timeFieldNameSet.contains(fieldName)){
				try {
					val = EBSPigUDF.normalized_date_string(val);
				} catch (Exception e) {
					logger.warn("got the following event while trying to normalize date", e);
				}
			}
			workingSet.add(val);
		}
	}
}
