package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.domain.config.HostConf;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = {MongoConfig.class, OutputTestManager.class})
public class RestUserTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private OutputTestManager testManager;

    private String testName;
    private final boolean printRequest = true;

    @BeforeClass
    public void preconditionCheck() {
        List<EntitiesStoredRecord> users = testManager.getEntities(printRequest);
        Assert.assertTrue(users.size() > 0,"Unable to commit the test - Users list is empty.");

        users = testManager.getEntities("Expand=true");
        Assert.assertTrue(users.size() > 0, "Unable to commit the test - Users list is empty while using expand=true.");
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }

    @Test
    public void sortDecScoreTest() {
        List<EntitiesStoredRecord> users = testManager.getEntities("sortDirection=DESC&sortFieldNames=SCORE&pageSize=500&pageNumber=0");

        for(int i=0 ; i<users.size()-1 ; i++) {
            int current = Integer.parseInt(users.get(i).getScore());
            int next = Integer.parseInt(users.get(i+1).getScore());

            if(current < next) {
                Assert.fail("Scores are not sorted correctly. \ncurrent score is " + current + ", next score is " + next + "\nuser: " + users.get(i).toString() );
            }
        }
    }

    @Test
    public void sortAscScoreTest() {
        List<EntitiesStoredRecord> users = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&pageSize=500&pageNumber=0");


        for(int i=0 ; i < users.size()-1 ; i++) {
            int current = Integer.parseInt(users.get(i).getScore());
            int next = Integer.parseInt(users.get(i+1).getScore());

            if(current > next) {
                Assert.fail("Scores are not sorted correctly. \ncurrent score is " + current + ", next score is " + next + "\nuser: " + users.get(i).toString() );
            }
        }
    }

    @Test
    public void expandArgumentTest() {
        List<EntitiesStoredRecord> users = testManager.getEntities("expand=true&minScore=50");
        Assert.assertTrue(users.get(0).getAlerts() != null, "Alerts list did not appears with the Expand=true flag.");
    }

    @Test
    public void getUserByUserIdTest() {
        List<EntitiesStoredRecord> users = testManager.getEntities(printRequest);

        String userId = users.get(0).getId();
        users = testManager.getEntities("/" + userId);

        for(EntitiesStoredRecord usr : users) {
            Assert.assertEquals(usr.getId(), userId, "Filter by id return incorrect user.");
        }
    }


    @Test
    public void alertsCountTest() {
        List<EntitiesStoredRecord> users = testManager.getEntities("Expand=true");

        for(EntitiesStoredRecord user : users) {
            int alertCount = user.getAlertCount();
            int alertSize = user.getAlerts().size();

            Assert.assertEquals(alertCount, alertSize, "The alertCount and the size of the alert list are not equals for userId " + user.getId());
        }
    }

    @Test
    public void aggregateBySeverityWithoutMinScoreTest() {
        verifySeverityAggregation("");
    }

    @Test
    public void aggregateBySeverityWithMinScoreTest() {
        verifySeverityAggregation("minScore=" + getSecondMinValue(null, true));
    }

    private void verifySeverityAggregation(String filter) {
        if(!filter.isEmpty()){
            filter = "&" + filter;
        }

        List<EntitiesStoredRecord> users = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&aggregateBy=SEVERITY&Expand=false&pageSize=10000&pageNumber=0" + filter);

        int low = 0, medium = 0, high = 0, critical = 0;

        for(EntitiesStoredRecord usr : users) {
            if(usr.getSeverity().equals("LOW")) low++;
            if(usr.getSeverity().equals("MEDIUM")) medium++;
            if(usr.getSeverity().equals("HIGH")) high++;
            if(usr.getSeverity().equals("CRITICAL")) critical++;
        }

        JSONObject agg = testManager.getAggregationData("SEVERITY" + filter + "&pageSize=10000&pageNumber=0", "entities", printRequest);
        try {
            JSONObject severity = agg.getJSONObject("SEVERITY");
            Assert.assertNotNull(severity, "severity keys are null");

            Iterator<String> keysItr = severity.keys();
            while(keysItr.hasNext()) {
                String key = keysItr.next();
                Object value = severity.get(key);

                if(key.equals("LOW")){
                    Assert.assertEquals(low, (int) value, "low severity count is not match to actual users severity");
                }
                else if(key.equals("MEDIUM")){
                    Assert.assertEquals(medium, (int) value, "medium severity count is not match to actual users severity");
                }
                else if(key.equals("HIGH")){
                    Assert.assertEquals(high, (int) value, "high severity count is not match to actual users severity");
                }
                else if(key.equals("CRITICAL")){
                    Assert.assertEquals(critical, (int) value, "critical severity count is not match to actual users severity");
                }
            }

            if(filter.contains("minScore")){
                int actualMinScore = getMinScore(users);
                int requestedMinScore = Integer.parseInt(filter.split("=")[1]);
                Assert.assertTrue(actualMinScore >= requestedMinScore, "The minimum score filter did not worked properly");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void aggregatedByIndicatorsTest() {
        JSONObject indicatorsAggregation = testManager.getAggregationData("INDICATORS", "entities", printRequest);
        try{
            indicatorsAggregation = indicatorsAggregation.getJSONObject("INDICATORS");
            Assert.assertNotNull(indicatorsAggregation, "indicator's aggregation keys are null");

            List<EntitiesStoredRecord> users = null;

            Iterator<String> keyItr = indicatorsAggregation.keys();
            while(keyItr.hasNext()) {
                String key = keyItr.next();
                Object value = indicatorsAggregation.get(key);
                int aggregated = (int)value;
                users = testManager.getEntities("indicatorsName=" + key + "&pageSize=1000&pageNumber=0");

                Assert.assertEquals(users.size(), aggregated,  "Indicator '" + key + "' aggregateBy count is not matched to 'indicatorName=" + key + " request. ");
            }

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    //@Test
    public void maxMinScoreRangeTest() {
        //TODO: enable when issue fixed
        List<Integer> scores = getScoresList();

        int minScore = getSecondMinValue(scores, true);
        int maxScore;
        if(scores.size() > 2){
            maxScore = scores.get(scores.size()-2);
        } else {
            maxScore = scores.get(scores.size()-1);
        }

        List<EntitiesStoredRecord> users = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&minScore=" + minScore + "&maxScore=" + maxScore + "&pageSize=10000&pageNumber=0");

        Assert.assertEquals(Integer.parseInt(users.get(0).getScore()), minScore, "minScore is not the minScore that supposed to be.");
        Assert.assertEquals(Integer.parseInt(users.get(users.size()-1).getScore()), maxScore, "maxScore is not the maxScore that supposed to be.");
    }


    @Test
    public void searchUserIgnoreCaseTest() throws UnsupportedEncodingException {
        List<EntitiesStoredRecord> entities = testManager.getEntities(printRequest);
        EntitiesStoredRecord testUser = entities.get(entities.size()/2);

        String entityName = testUser.getEntityName().toUpperCase();
        String userId = testUser.getId();

        List<EntitiesStoredRecord> user = testManager.getEntities("entityName=" + URLEncoder.encode(entityName, "UTF-8"));
        Assert.assertTrue(entities.size() > 0,"Users list is empty. unable to get entityName=" + entityName);

        Assert.assertEquals(userId, user.get(0).getId(), "Failed to get the specific entityName with ignore case.\nrequested user: " + entityName.toUpperCase() +
            "\nuserId: " + userId + "\nThe result is userId: " + user.get(0).getId() + "\nWith the entityName: " + user.get(0).getEntityName());

    }

    @Test
    public void searchUserUsingContainsSearch(){
        List<EntitiesStoredRecord> entities = testManager.getEntities("pageSize=10000&pageNumber=0");
        if(entities == null || entities.size() == 0){
            Assert.fail("Cannot commit the test. Can't get the user list.");
        }
        String entityName = null;
        String userId = "";
        for(EntitiesStoredRecord usr : entities) {
            if(usr.getEntityName().contains("\\")){
                entityName = usr.getEntityName();
                userId = usr.getId();
                break;
            }
        }

        if(entityName != null){
            entityName = entityName.split("\\\\")[1];
        } else {
            Assert.fail("cannot commit the test. entityName contains \\ is not exist.");
        }

        try{
            entities = testManager.getEntities("entityName=" + URLEncoder.encode(entityName, "UTF-8"));
            Assert.assertTrue(entities.size() > 0, "fail to get entities by contains search. requested entityName = " + entityName);
            boolean exist = false;
            for(EntitiesStoredRecord usr : entities) {
                if(usr.getId().equals(userId)){
                    exist = true;
                    break;
                }
            }

            Assert.assertTrue(exist, "Requested user is not in the result user list (verified by user id).");
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    @Test
    public void verifyMatchingOfUsersWithUsersAlerts() {
        List<EntitiesStoredRecord> entities = testManager.getEntities("expand=true&pageSize=10000&pageNumber=0&minScore=1");

        for(EntitiesStoredRecord usr : entities) {
            String userId = usr.getId();
            List<AlertsStoredRecord> alerts = usr.getAlerts();
            for(AlertsStoredRecord alert : alerts) {
                String msg = "alert's userId is not matched to the it's user Id.\n" +
                        "UserId = " + userId + "\n" +
                        "AlertId = " + alert.getId() + "\n" +
                        "url --> http://" + HostConf.getServerHostname() + "/presidio-output/entities/" + userId + "?expand=true";
                Assert.assertTrue(userId.equals(alert.getEntityDocumentId()), msg);
            }
        }

    }


        // find the minimum score exist in the user list
    private int getMinScore(List<EntitiesStoredRecord> users) {
        int min = Integer.MAX_VALUE;

        for(EntitiesStoredRecord usr : users) {
            if(Integer.parseInt(usr.getScore()) < min){
                min = Integer.parseInt(usr.getScore());
            }
        }

        return min;
    }



    private int getSecondMinValue(List<Integer> values, boolean isSorted) {
        List<Integer> scores;
        if(values == null) {
            scores = getScoresList();
        }
        else {
            if(!isSorted){
                Collections.sort(values);
            }
            scores = values;
        }

        int secondMin;
        int count = scores.size();
        if(count >= 2 ) {
            secondMin = scores.get(1);
        }
        else {
            secondMin = scores.get(0);
        }

        return secondMin;
    }

    private List<Integer> getScoresList() {
        List<EntitiesStoredRecord> users = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&pageSize=10000&pageNumber=0");

        List<Integer> scores = new ArrayList<>();

        int lastScore = Integer.parseInt(users.get(0).getScore());
        scores.add(lastScore);

        for(EntitiesStoredRecord usr : users){
            if(Integer.parseInt(usr.getScore()) > lastScore) {
                lastScore = Integer.parseInt(usr.getScore());
                scores.add(lastScore);
            }
        }

        return scores;
    }


}
