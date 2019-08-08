package com.rsa.netwitness.presidio.automation.test.data.processing;

import com.rsa.netwitness.presidio.automation.domain.config.MongoConfig;
import com.rsa.netwitness.presidio.automation.domain.output.AlertsStoredRecord;
import com.rsa.netwitness.presidio.automation.domain.output.EntitiesStoredRecord;
import com.rsa.netwitness.presidio.automation.utils.output.OutputTestManager;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import static java.time.Instant.parse;
import static presidio.data.generators.utils.TimeUtils.calcDaysBack;

@TestPropertySource(properties = {"spring.main.allow-bean-definition-overriding=true",})
@SpringBootTest(classes = { OutputTestManager.class, MongoConfig.class})
public class OutputRunPrepareData extends AbstractTestNGSpringContextTests {

    @Autowired
    private OutputTestManager testManager;

    private String testName;

    @BeforeClass
    public void beforeClass() throws JSONException {
        System.out.println("Starting OutputRunPrepareData beforeClass...");

        testManager.process(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z"),"userId_hourly");
        testManager.process(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z"),"sslSubject_hourly");
        testManager.process(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z"),"ja3_hourly");
        System.out.println("Done processing output.");
        testManager.recalculate_user_score(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z"), "userId");
        testManager.recalculate_user_score(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z"), "ja3");
        testManager.recalculate_user_score(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(2) + "T13:00:00.00Z"), "sslSubject");
//        firstCalculateResult = calcUsersScoreSeverity();
        System.out.println("presidio-output-processor have been finished to run");
        testManager.process(parse(calcDaysBack(2) + "T13:00:00.00Z"), parse(calcDaysBack(1) + "T23:59:59.00Z"),"userId_hourly");
        testManager.process(parse(calcDaysBack(2) + "T13:00:00.00Z"), parse(calcDaysBack(1) + "T23:59:59.00Z"),"sslSubject_hourly");
        testManager.process(parse(calcDaysBack(2) + "T13:00:00.00Z"), parse(calcDaysBack(1) + "T23:59:59.00Z"),"ja3_hourly");
        System.out.println("Done Processing output.");
        testManager.recalculate_user_score(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(1) + "T23:59:59.00Z"), "userId");
        testManager.recalculate_user_score(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(1) + "T23:59:59.00Z"), "ja3");
        testManager.recalculate_user_score(parse(calcDaysBack(30) + "T00:00:00.00Z"), parse(calcDaysBack(1) + "T23:59:59.00Z"), "sslSubject");
        System.out.println("presidio-output-processor have been finished to run");
//        secondCalculateResult = calcUsersScoreSeverity();
    }

    @BeforeMethod
    public void beforeName(Method method){
        testName = method.getName();
        System.out.println("Start running test: " + testName);
    }

    @Test
    public void CalcUserScore() {
        List<EntitiesStoredRecord> users = testManager.getEntities("sortDirection=DESC&sortFieldNames=SCORE&expand=true&pageSize=10000&pageNumber=0");
        int sumScoreSeverity = 0;

        for(EntitiesStoredRecord user : users) {
            List<AlertsStoredRecord> alerts = user.getAlerts();
            if (alerts.size() > 0) {
                for (AlertsStoredRecord alert : alerts) {
                    sumScoreSeverity += getSeverityScore(alert.getSeverity());
                }
            }

            Assert.assertEquals(Integer.parseInt(user.getScore()), sumScoreSeverity);
            sumScoreSeverity = 0;
        }
    }

    private int getSeverityScore(String name) {
        HashMap<String, Integer> severityScoreMap = new HashMap<>();
        severityScoreMap.put("CRITICAL", 20);
        severityScoreMap.put("HIGH", 15);
        severityScoreMap.put("MEDIUM", 10);
        severityScoreMap.put("LOW", 1);

        return severityScoreMap.get(name);
    }
}