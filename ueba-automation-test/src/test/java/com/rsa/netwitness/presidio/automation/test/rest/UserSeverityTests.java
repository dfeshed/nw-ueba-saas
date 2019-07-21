package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.utils.output.OutputTestManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, OutputTestManager.class})
public class UserSeverityTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private OutputTestManager testManager;

    private String testName;
    private final boolean printRequest = true;

    private int criticalInd = -1;
    private int highInd = -1;
    private int mediumInd = -1;

    private double criticalPercent = 1.0;
    private double highPercent = 4.0;
    private double mediumPercent = 10.0;
    private double lowPercent = 85.0;

    private Map<String, Integer> severityMap;

    @BeforeClass
    public void prepareData() throws JSONException {
        severityMap = getSeverityMap();
        getSeveritiesBoundariesIndexes();

    }


    @Test
    public void checkPecentageDividing() {
        JSONObject users = testManager.sendGetEntitiesURL("?sortDirection=ASC&sortFieldNames=SCORE",printRequest);
        int total = 0;
        double criticalPercent = 1;
        double highPercent = 4;
        double mediumPercent = 10;
        double lowPercent = 85;

        try {
            total = users.getInt("total");
         } catch (JSONException e) {
            System.out.println(e.getMessage());
         }

        int criticalUsersCount = (int)((criticalPercent / 100) * total);
        if(criticalUsersCount > 5) criticalUsersCount = 5;

        int highUserCount = (int)((highPercent / 100) * total);
        if(highUserCount > 10) highUserCount = 10;

        int mediumUserCount = (int)((mediumPercent / 100) * total);
        int lowUserCount = total - criticalUsersCount - highUserCount - mediumUserCount;

        Map<String, Integer> severities = getSeverityMap();

        Iterator it = severities.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            if(pair.getKey().toString().toLowerCase().equals("critical")){
                Assert.assertTrue((int)pair.getValue() <= criticalUsersCount, "there are too many critical users. critical user count = " + (int)pair.getValue());
            }

            if(pair.getKey().toString().toLowerCase().equals("high")){
                Assert.assertTrue((int)pair.getValue() <= highUserCount, "there are too many high users. high user count = " + (int)pair.getValue());
            }

            if(pair.getKey().toString().toLowerCase().equals("medium")){
                Assert.assertTrue((int)pair.getValue() <= mediumUserCount, "there are too many medium users. medium user count = " + (int)pair.getValue());
            }
        }
    }

    @Test
    public void checkCriticalGroupSeverityByScore() {
        List<EntitiesStoredRecord> users = testManager.getEntities("pageSize=10000&pageNumber=0&sortDirection=ASC&sortFieldNames=SCORE", printRequest);

        Map<String, Integer> severitiesCount = getSeverityMap();
        if(severitiesCount.containsKey("CRITICAL")){
            int criticalCount = severitiesCount.get("CRITICAL");
            if(criticalCount > 0){
                int curr = Integer.parseInt(users.get(criticalInd).getScore());
                int last = Integer.parseInt(users.get(criticalInd-1).getScore());

                String msg = "difference between the last high severity and the first critical severity is less than 50%.\n" +
                        "highest high severity user is '" + users.get(criticalCount-1).getEntityName() + "'\n" +
                        "with the score of --> " + users.get(criticalInd-1).getScore() + "\n" +
                        "and the first critical user is '" + users.get(criticalInd).getEntityName() + "'\n" +
                        "with the score of --> " + users.get(criticalInd).getScore() + "\n" +
                        "The score wa supposed to be greater than " + (last*1.5);

                Assert.assertTrue(curr > (last*1.5), msg);
                Assert.assertTrue(curr > 15, "user " + users.get(criticalInd).getEntityName() + " has a critical severity with score less than 15.");

            }
        } else {
            // Assert.fail("CRITICAL Severity has not been found!");
            System.err.println("Critical severity not found!");
        }
    }

    @Test
    public void checkHighGroupSeverityByScore() {
        List<EntitiesStoredRecord> users = testManager.getEntities("pageSize=10000&pageNumber=0&sortDirection=ASC&sortFieldNames=SCORE", printRequest);

        if(severityMap.containsKey("HIGH")){
            if(severityMap.get("HIGH") > 0){
                int curr = Integer.parseInt(users.get(highInd).getScore());
                int last = Integer.parseInt(users.get(highInd-1).getScore());

                String msg = "difference between the last medium severity and the first high severity is less than 30%.\n" +
                        "highest medium severity user is '" + users.get(highInd-1).getEntityName() + "'\n" +
                        "with the score of --> " + users.get(highInd-1).getScore() + "\n" +
                        "and the first high user severity is '" + users.get(highInd).getEntityName() + "'\n" +
                        "with the score of --> " + users.get(highInd).getScore() + "\n" +
                        "The score wa supposed to be greater than " + (last*1.3);

                Assert.assertTrue(curr >= (last*1.3), msg);
            }
        } else {
//           Assert.fail("HIGH Severity has not been found!");
            System.err.println("High severity not found!");
        }

    }

    @Test
    public void checkMediumGroupSeverityByScore() {
        List<EntitiesStoredRecord> users = testManager.getEntities("pageSize=10000&pageNumber=0&sortDirection=ASC&sortFieldNames=SCORE", printRequest);

        if(severityMap.containsKey("MEDIUM")){
            if(severityMap.get("MEDIUM") > 0){
                int curr = Integer.parseInt(users.get(mediumInd).getScore());
                int last = Integer.parseInt(users.get(mediumInd-1).getScore());

                String msg = "difference between the last low severity and the first medium severity is less than 10%.\n" +
                        "highest low severity user is '" + users.get(mediumInd-1).getEntityName() + "'\n" +
                        "with the score of --> " + users.get(mediumInd-1).getScore() + "\n" +
                        "and the first medium user severity is '" + users.get(mediumInd).getEntityName() + "'\n" +
                        "with the score of --> " + users.get(mediumInd).getScore() + "\n" +
                        "The score wa supposed to be greater than " + (last*1.1);

                Assert.assertTrue(curr >= (last*1.1), msg);
            }
        } else {
//            Assert.fail("MEDIUM Severity has not been found!");
            System.err.println("Medium severity not found!");
        }
    }

    private Map<String, Integer> getSeverityMap() {
        Map<String, Integer> severities = new HashMap<>();
        JSONObject agg = testManager.getAggregationData("SEVERITY", "entities", printRequest);

        try {
            JSONObject severity = agg.getJSONObject("SEVERITY");
            Assert.assertNotNull(severity, "severity keys are null");

            Iterator<String> keysItr = severity.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = severity.get(key);

                severities.put(key, (int)(value));
            }
        } catch(Exception e) {
            Assert.fail(e.getMessage() + " " + e.getStackTrace());
        }
        return severities;
    }

    private void getSeveritiesBoundariesIndexes() {
        List<EntitiesStoredRecord> users = testManager.getEntities("pageSize=10000&pageNumber=0&sortDirection=ASC&sortFieldNames=SCORE", printRequest);

        for(int i=0 ; i < users.size() ; i++) {
            if(mediumInd == -1 && users.get(i).getSeverity().equals("MEDIUM")) {
                mediumInd = i;
            } else if(highInd == -1 && users.get(i).getSeverity().equals("HIGH")) {
                highInd = i;
            } else if(criticalInd == -1 && users.get(i).getSeverity().equals("CRITICAL")) {
                criticalInd = i;
            }
        }
    }

}
