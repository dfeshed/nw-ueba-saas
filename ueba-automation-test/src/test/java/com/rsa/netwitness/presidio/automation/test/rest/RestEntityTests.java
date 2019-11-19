package com.rsa.netwitness.presidio.automation.test.rest;

import com.rsa.netwitness.presidio.automation.config.AutomationConf;
import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.rest.helper.RestHelper;
import com.rsa.netwitness.presidio.automation.test_managers.OutputTestManager;
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

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true"})
@SpringBootTest(classes = {MongoConfig.class, OutputTestManager.class})
public class RestEntityTests extends AbstractTestNGSpringContextTests {

    @Autowired
    private OutputTestManager testManager;

    private RestHelper restHelper = new RestHelper();
    private String testName;
    private final boolean printRequest = true;

    @BeforeClass
    public void preconditionCheck() {
        List<EntitiesStoredRecord> entities = testManager.getEntities(printRequest);
        Assert.assertTrue(entities.size() > 0,"Unable to commit the test - Entities list is empty.");

        entities = testManager.getEntities("Expand=true");
        Assert.assertTrue(entities.size() > 0, "Unable to commit the test - Entities list is empty while using expand=true.");
    }

    @BeforeMethod
    public void nameBefore(Method method) {
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }

    @Test
    public void sortDecScoreTest() {
        List<EntitiesStoredRecord> entities = testManager.getEntities("sortDirection=DESC&sortFieldNames=SCORE&pageSize=500&pageNumber=0");

        for(int i=0 ; i<entities.size()-1 ; i++) {
            int current = Integer.parseInt(entities.get(i).getScore());
            int next = Integer.parseInt(entities.get(i+1).getScore());

            if(current < next) {
                Assert.fail("Scores are not sorted correctly. \ncurrent score is " + current + ", next score is " + next + "\nentity: " + entities.get(i).toString() );
            }
        }
    }

    @Test
    public void sortAscScoreTest() {
        List<EntitiesStoredRecord> entities = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&pageSize=500&pageNumber=0");


        for(int i=0 ; i < entities.size()-1 ; i++) {
            int current = Integer.parseInt(entities.get(i).getScore());
            int next = Integer.parseInt(entities.get(i+1).getScore());

            if(current > next) {
                Assert.fail("Scores are not sorted correctly. \ncurrent score is " + current + ", next score is " + next + "\nentity: " + entities.get(i).toString() );
            }
        }
    }

    @Test
    public void expandArgumentTest() {
        List<EntitiesStoredRecord> entities = testManager.getEntities("expand=true&minScore=50");
        Assert.assertTrue(entities.get(0).getAlerts() != null, "Alerts list did not appears with the Expand=true flag.");
    }

    @Test
    public void getEntityByEntityIdTest() {
        List<EntitiesStoredRecord> entities = testManager.getEntities(printRequest);

        String entityId = entities.get(0).getId();
        entities = testManager.getEntities("/" + entityId);

        for(EntitiesStoredRecord usr : entities) {
            Assert.assertEquals(usr.getId(), entityId, "Filter by id return incorrect entity.");
        }
    }


    @Test
    public void alertsCountTest() {
        List<EntitiesStoredRecord> entities = testManager.getEntities("Expand=true");

        for(EntitiesStoredRecord entity : entities) {
            int alertCount = entity.getAlertCount();
            int alertSize = entity.getAlerts().size();

            Assert.assertEquals(alertCount, alertSize, "The alertCount and the size of the alert list are not equals for entityId " + entity.getId());
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

        List<EntitiesStoredRecord> entities = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&aggregateBy=SEVERITY&Expand=false&pageSize=10000&pageNumber=0" + filter);

        int low = 0, medium = 0, high = 0, critical = 0;

        for(EntitiesStoredRecord usr : entities) {
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
                    Assert.assertEquals(low, (int) value, "low severity count is not match to actual entities severity");
                }
                else if(key.equals("MEDIUM")){
                    Assert.assertEquals(medium, (int) value, "medium severity count is not match to actual entities severity");
                }
                else if(key.equals("HIGH")){
                    Assert.assertEquals(high, (int) value, "high severity count is not match to actual entities severity");
                }
                else if(key.equals("CRITICAL")){
                    Assert.assertEquals(critical, (int) value, "critical severity count is not match to actual entities severity");
                }
            }

            if(filter.contains("minScore")){
                int actualMinScore = getMinScore(entities);
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

            List<EntitiesStoredRecord> entities = null;

            Iterator<String> keyItr = indicatorsAggregation.keys();
            while(keyItr.hasNext()) {
                String key = keyItr.next();
                Object value = indicatorsAggregation.get(key);
                int aggregated = (int)value;
                entities = testManager.getEntities("indicatorsName=" + key + "&pageSize=1000&pageNumber=0");

                Assert.assertEquals(entities.size(), aggregated,  "Indicator '" + key + "' aggregateBy count is not matched to 'indicatorName=" + key + " request. ");
            }

        } catch(Exception e){
            e.printStackTrace();
        }

    }

    @Test
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

        List<EntitiesStoredRecord> entities = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&minScore=" + minScore + "&maxScore=" + maxScore + "&pageSize=10000&pageNumber=0");

        Assert.assertEquals(Integer.parseInt(entities.get(0).getScore()), minScore, "minScore is not the minScore that supposed to be.");
        Assert.assertEquals(Integer.parseInt(entities.get(entities.size()-1).getScore()), maxScore, "maxScore is not the maxScore that supposed to be.");
    }


    @Test
    public void searchEntityIgnoreCaseTest() throws UnsupportedEncodingException {
        List<EntitiesStoredRecord> entities = testManager.getEntities(printRequest);
        EntitiesStoredRecord testEntity = entities.get(entities.size()/2);

        String entityName = testEntity.getEntityName().toUpperCase();
        String entityId = testEntity.getId();

        List<EntitiesStoredRecord> entity = testManager.getEntities("entityName=" + URLEncoder.encode(entityName, "UTF-8"));
        Assert.assertTrue(entities.size() > 0,"Entities list is empty. unable to getOperationTypeToCategoryMap entityName=" + entityName);

        Assert.assertEquals(entityId, entity.get(0).getId(), "Failed to getOperationTypeToCategoryMap the specific entityName with ignore case.\nrequested entity: " + entityName.toUpperCase() +
            "\nentityId: " + entityId + "\nThe result is entityId: " + entity.get(0).getId() + "\nWith the entityName: " + entity.get(0).getEntityName());

    }

    @Test
    public void searchEntityUsingContainsSearch(){
        List<EntitiesStoredRecord> entities = testManager.getEntities("pageSize=10000&pageNumber=0");
        if(entities == null || entities.size() == 0){
            Assert.fail("Cannot commit the test. Can't getOperationTypeToCategoryMap the entity list.");
        }
        String entityName = null;
        String entityId = "";
        for(EntitiesStoredRecord usr : entities) {
            if(usr.getEntityName().contains("\\")){
                entityName = usr.getEntityName();
                entityId = usr.getId();
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
            Assert.assertTrue(entities.size() > 0, "fail to getOperationTypeToCategoryMap entities by contains search. requested entityName = " + entityName);
            boolean exist = false;
            for(EntitiesStoredRecord usr : entities) {
                if(usr.getId().equals(entityId)){
                    exist = true;
                    break;
                }
            }

            Assert.assertTrue(exist, "Requested entity is not in the result entity list (verified by entity id).");
        } catch (Exception e){
            e.printStackTrace();
        }

    }


    @Test
    public void verifyMatchingOfEntitiesWithEntitiesAlerts() {
        List<EntitiesStoredRecord> entities = testManager.getEntities("expand=true&pageSize=10000&pageNumber=0&minScore=1");

        for(EntitiesStoredRecord usr : entities) {
            String entityId = usr.getId();
            List<AlertsStoredRecord> alerts = usr.getAlerts();
            for(AlertsStoredRecord alert : alerts) {
                String msg = "alert's entityId is not matched to the it's entity Id.\n" +
                        "EntityId = " + entityId + "\n" +
                        "AlertId = " + alert.getId() + "\n" +
                        "url --> http://" + AutomationConf.UEBA_HOST + "/presidio-output/entities/" + entityId + "?expand=true";
                Assert.assertTrue(entityId.equals(alert.getEntityDocumentId()), msg);
            }
        }

    }


        // find the minimum score exist in the entity list
    private int getMinScore(List<EntitiesStoredRecord> entities) {
        int min = Integer.MAX_VALUE;

        for(EntitiesStoredRecord usr : entities) {
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
        List<EntitiesStoredRecord> entities = testManager.getEntities("sortDirection=ASC&sortFieldNames=SCORE&pageSize=10000&pageNumber=0");

        List<Integer> scores = new ArrayList<>();

        int lastScore = Integer.parseInt(entities.get(0).getScore());
        scores.add(lastScore);

        for(EntitiesStoredRecord usr : entities){
            if(Integer.parseInt(usr.getScore()) > lastScore) {
                lastScore = Integer.parseInt(usr.getScore());
                scores.add(lastScore);
            }
        }

        return scores;
    }


}
