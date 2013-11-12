package fortscale.services.actions.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.services.domain.AbstractTest;

public class ActionRateImplTest extends AbstractTest{
	
	@Autowired
	private ActionRateImpl actionRate;
	
	String rateField  = "time";
	String eventField = "event";

	
	@Test
	public void testRateTable() {
		
		List<Map<String, Object>> input = setTestInput();
		Map<String, Integer> expectedOutputMap = setTestExpectedOutput();

		List<Map<String, Object>> parsedOutput = actionRate.rateTable(input, rateField, "1d");

		for (int i=0; i<parsedOutput.size(); i++) {
			Map<String,Object> parsedOutputMap = parsedOutput.get(i);
			String parsedDate  = parsedOutputMap.get(ActionRateImpl.OUTPUT_TIME_KEY_NAME).toString(); 
			int parsedCount = (int) parsedOutputMap.get(ActionRateImpl.OUTPUT_COUNT_KEY_NAME);

			Assert.assertTrue(parsedCount == expectedOutputMap.get(parsedDate));
		}
	}
	

	private List<Map<String,Object>> setTestInput() {
		List<Map<String, Object>> inputList = new ArrayList<Map<String, Object>>();
		Map<String,Object> inputMap;
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-01 09:15:58.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-01 09:30:12.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-02 08:42:08.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-02 11:07:25.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-02 16:32:19.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-02 18:46:31.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-03 09:15:58.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-04 09:15:58.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-05 09:15:58.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);
		
		inputMap = new HashMap<String,Object>();
		inputMap.put(rateField, "2013-10-05 10:15:58.0");
		inputMap.put(eventField, "4769");
		inputList.add(inputMap);

		
		return inputList;
	}

	
	private Map<String, Integer> setTestExpectedOutput() {
		Map<String,Integer> expectedOutputMap = new HashMap<String,Integer>();
		
		expectedOutputMap.put("2013/10/01",2);
		expectedOutputMap.put("2013/10/02",4);
		expectedOutputMap.put("2013/10/03",1);
		expectedOutputMap.put("2013/10/04",1);
		expectedOutputMap.put("2013/10/05",2);
		
		return expectedOutputMap;
	}

}
